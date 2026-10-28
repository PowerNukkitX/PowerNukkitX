package org.powernukkitx.network.nethernet;

import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCounted;
import org.cloudburstmc.netty.signaling.ProviderClient.GameOutcome;
import org.cloudburstmc.netty.signaling.admission.AdmissionPrincipal;
import org.cloudburstmc.protocol.bedrock.data.payload.connection.DisconnectPacketMessages;
import org.cloudburstmc.protocol.bedrock.netty.BedrockBatchWrapper;
import org.cloudburstmc.protocol.bedrock.netty.BedrockPacketWrapper;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.DisconnectPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestNetworkSettingsPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameOutcomeReporterTest {
    private record Report(String ticketId, GameOutcome outcome, String reason) {
    }

    private final List<Report> reports = new ArrayList<>();
    private boolean accepting = true;
    private final GameOutcomeReporter reporter = new GameOutcomeReporter((ticketId, outcome, reason) -> {
        reports.add(new Report(ticketId, outcome, reason));
        return accepting;
    });

    private EmbeddedChannel channel() {
        EmbeddedChannel channel = new EmbeddedChannel(reporter.observer(protocol -> protocol == 100));
        channel.attr(AdmissionPrincipal.KEY).set(new AdmissionPrincipal("ticket", "network", "private-identity", "K001"));
        return channel;
    }

    private static void read(EmbeddedChannel channel, Object message) {
        assertTrue(channel.writeInbound(message));
        assertSame(message, channel.readInbound());
        assertEquals(1, ((ReferenceCounted) message).refCnt());
        ((ReferenceCounted) message).release();
    }

    private static void write(EmbeddedChannel channel, Object message) {
        var promise = channel.newPromise();
        channel.writeAndFlush(message, promise);
        assertTrue(promise.isSuccess());
        assertSame(message, channel.readOutbound());
        assertEquals(1, ((ReferenceCounted) message).refCnt());
        ((ReferenceCounted) message).release();
    }

    private static BedrockPacketWrapper packet(int target, BedrockPacket packet) {
        return BedrockPacketWrapper.create(0, 0, target, packet, null);
    }

    private static RequestNetworkSettingsPacket settings(int protocol) {
        RequestNetworkSettingsPacket packet = new RequestNetworkSettingsPacket();
        packet.setClientNetworkVersion(protocol);
        return packet;
    }

    private static DisconnectPacket disconnect() {
        DisconnectPacket packet = new DisconnectPacket();
        packet.setMessages(new DisconnectPacketMessages("Private text must never reach telemetry", ""));
        return packet;
    }

    @Test
    void reportsEarlyUnsupportedVersionWithoutChangingPacketsOrPromiseOwnership() {
        EmbeddedChannel channel = channel();
        try {
            read(channel, packet(0, settings(101)));
            write(channel, packet(0, disconnect()));
            write(channel, packet(0, disconnect()));
            assertEquals(List.of(new Report("ticket", GameOutcome.REJECTED, "unsupported_version")), reports);
            assertFalse(reports.toString().contains("Private"));
            assertFalse(reports.toString().contains("private-identity"));
        } finally {
            channel.finishAndReleaseAll();
        }
    }

    @Test
    void readsPacketsInsideBatches() {
        EmbeddedChannel channel = channel();
        try {
            read(channel, BedrockBatchWrapper.create(0, settings(100)));
            write(channel, BedrockBatchWrapper.create(0, disconnect()));
            assertEquals(List.of(new Report("ticket", GameOutcome.REJECTED, "server_rejected")), reports);
        } finally {
            channel.finishAndReleaseAll();
        }
    }

    @Test
    void reportsExplicitRejectionWithoutInferringItFromClientClose() {
        EmbeddedChannel channel = channel();
        read(channel, packet(0, settings(100)));
        write(channel, packet(0, disconnect()));
        channel.finishAndReleaseAll();
        assertEquals("server_rejected", reports.get(0).reason());
        reports.clear();
        channel = channel();
        read(channel, packet(0, settings(101)));
        channel.finishAndReleaseAll();
        assertTrue(reports.isEmpty());
    }

    @Test
    void joinedThenDisconnectIsNotReclassifiedAsRejected() {
        EmbeddedChannel channel = channel();
        try {
            read(channel, packet(0, new SetLocalPlayerAsInitializedPacket()));
            read(channel, packet(0, new SetLocalPlayerAsInitializedPacket()));
            write(channel, packet(0, disconnect()));
            assertEquals(List.of(new Report("ticket", GameOutcome.JOINED, "play_ready")), reports);
        } finally {
            channel.finishAndReleaseAll();
        }
    }

    @Test
    void ignoresNonAdmittedConnectionsAndSplitScreen() {
        EmbeddedChannel channel = channel();
        try {
            write(channel, packet(1, disconnect()));
            read(channel, packet(1, new SetLocalPlayerAsInitializedPacket()));
            assertTrue(reports.isEmpty());
            channel.attr(AdmissionPrincipal.KEY).set(null);
            read(channel, packet(0, settings(101)));
            write(channel, packet(0, disconnect()));
            assertTrue(reports.isEmpty());
        } finally {
            channel.finishAndReleaseAll();
        }
    }

    @Test
    void countsDroppedOutcomesOncePerPlayer() {
        accepting = false;
        EmbeddedChannel channel = channel();
        try {
            read(channel, packet(0, new SetLocalPlayerAsInitializedPacket()));
            write(channel, packet(0, disconnect()));
            assertEquals(1, reporter.droppedEvents());
            assertEquals(1, reports.size(), "A dropped join is not later reported as a rejection");
        } finally {
            channel.finishAndReleaseAll();
        }
    }
}
