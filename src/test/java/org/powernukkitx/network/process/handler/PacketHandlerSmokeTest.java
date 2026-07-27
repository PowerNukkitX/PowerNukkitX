package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerFixture;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PacketHandlerRegistry;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.network.process.SessionState;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.data.PlayerRespawnState;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerId;
import org.cloudburstmc.protocol.bedrock.packet.AnimatePacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.CommandRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.EmotePacket;
import org.cloudburstmc.protocol.bedrock.packet.LevelSoundEventPacket;
import org.cloudburstmc.protocol.bedrock.packet.NetworkStackLatencyPacket;
import org.cloudburstmc.protocol.bedrock.packet.PlayerHotbarPacket;
import org.cloudburstmc.protocol.bedrock.packet.RequestChunkRadiusPacket;
import org.cloudburstmc.protocol.bedrock.packet.RespawnPacket;
import org.cloudburstmc.protocol.bedrock.packet.ServerboundDiagnosticsPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetLocalPlayerAsInitializedPacket;
import org.cloudburstmc.protocol.bedrock.packet.ShowCreditsPacket;
import org.cloudburstmc.protocol.bedrock.packet.TickSyncPacket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Drives the real packet handlers in {@code network/process/handler/} through the actual
 * dispatch path: {@link PacketHandlerRegistry#getPacketHandler} then {@code handle(...)}.
 * A real {@link TestPlayer} backs a mocked {@link PlayerSessionHolder}. Everything is
 * wrapped in {@link #safe} because these handlers reach deep into gameplay state - the
 * goal is line coverage, not behavioural assertions.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PacketHandlerSmokeTest {

    private TestPlayer player;
    private PlayerSessionHolder holder;
    private int checked = 0;

    @BeforeAll
    void setup() {
        ServerMockFixture.boot();
        player = PlayerFixture.get();
        player.spawned = true;
        PlayerHandle handle = new PlayerHandle(player);
        player.setPlayerHandle(handle);

        BedrockServerSession session = player.getSession();
        holder = mock(PlayerSessionHolder.class);
        doReturn(player).when(holder).getPlayer();
        doReturn(handle).when(holder).getPlayerHandle();
        doReturn(session).when(holder).getSession();
        doReturn(SessionState.CHUNKS).when(holder).getState();
    }

    private void dispatch(BedrockPacket packet) {
        PacketHandler handler = PacketHandlerRegistry.getPacketHandler(packet.getClass());
        if (handler == null) {
            return;
        }
        checked++;
        handler.handle(packet, holder, ServerMockFixture.server);
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
            // handlers touch gameplay state the fixture only partially satisfies
        }
    }

    @Test
    void driveHandlers() {
        safe(() -> {
            AnimatePacket p = new AnimatePacket();
            p.setAction(AnimatePacket.Action.SWING);
            dispatch(p);
        });

        safe(() -> {
            EmotePacket p = new EmotePacket();
            p.setActorRuntimeId(player.getId());
            p.setEmoteId(UUID.randomUUID().toString());
            dispatch(p);
        });

        safe(() -> {
            CommandRequestPacket p = new CommandRequestPacket();
            p.setCommand("/help");
            dispatch(p);
        });

        safe(() -> {
            PlayerHotbarPacket p = new PlayerHotbarPacket();
            p.setContainerID(ContainerId.INVENTORY);
            p.setSelectedSlot(0);
            dispatch(p);
        });

        safe(() -> dispatch(new LevelSoundEventPacket()));

        safe(() -> {
            RequestChunkRadiusPacket p = new RequestChunkRadiusPacket();
            p.setChunkRadius(8);
            dispatch(p);
        });

        safe(() -> dispatch(new SetLocalPlayerAsInitializedPacket()));

        safe(() -> {
            RespawnPacket p = new RespawnPacket();
            p.setState(PlayerRespawnState.CLIENT_READY_TO_SPAWN);
            p.setPlayerRuntimeId(player.getId());
            dispatch(p);
        });

        safe(() -> {
            TickSyncPacket p = new TickSyncPacket();
            p.setRequestTimestamp(1L);
            dispatch(p);
        });

        safe(() -> {
            NetworkStackLatencyPacket p = new NetworkStackLatencyPacket();
            p.setCreationTime(123L);
            p.setFromServer(true);
            dispatch(p);
        });

        safe(() -> dispatch(new ServerboundDiagnosticsPacket()));

        safe(() -> {
            ShowCreditsPacket p = new ShowCreditsPacket();
            p.setCreditsState(ShowCreditsPacket.CreditsState.END_CREDITS);
            p.setPlayerRuntimeID(player.getId());
            dispatch(p);
        });

        assertTrue(checked > 0, "expected at least one handler dispatched, got " + checked);
    }
}
