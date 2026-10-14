package org.powernukkitx.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.embedded.EmbeddedChannel;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.netty.BedrockPacketWrapper;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec_v3;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetActorMotionPacket;
import org.cloudburstmc.protocol.bedrock.packet.UnknownPacket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SharedPacketTest {

    private static EmbeddedChannel channel() {
        BedrockPacketCodec_v3 codec = new BedrockPacketCodec_v3();
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline().addLast(BedrockPacketCodec.NAME, codec);
        codec.setCodec(NetworkConstants.CODEC);
        channel.pipeline().addAfter(BedrockPacketCodec.NAME, SharedPacketEncoder.NAME, new SharedPacketEncoder());
        return channel;
    }

    private static byte[] write(EmbeddedChannel channel, BedrockPacket packet) {
        channel.writeOutbound(BedrockPacketWrapper.create(0, 0, 0, packet, null));
        BedrockPacketWrapper written = channel.readOutbound();
        try {
            ByteBuf buffer = written.getPacketBuffer();
            return ByteBufUtil.getBytes(buffer, buffer.readerIndex(), buffer.readableBytes());
        } finally {
            written.release();
        }
    }

    private static ByteBuf root(ByteBuf buffer) {
        while (buffer.unwrap() != null) {
            buffer = buffer.unwrap();
        }
        return buffer;
    }

    private static SetActorMotionPacket motion() {
        SetActorMotionPacket packet = new SetActorMotionPacket();
        packet.setTargetRuntimeID(42L);
        packet.setMotion(Vector3f.from(0.5f, -1f, 2f));
        return packet;
    }

    @Test
    void sharedPacketIsWrittenAsTheSameBytes() {
        byte[] regular = write(channel(), motion());
        BedrockPacket shared = SharedPacket.forBroadcast(motion(), 2);
        Assertions.assertInstanceOf(SharedPacket.class, shared);

        Assertions.assertArrayEquals(regular, write(channel(), shared));
        Assertions.assertArrayEquals(regular, write(channel(), shared), "a second session writes the same bytes");
    }

    @Test
    void encodedOnceForAllSessions() {
        SharedPacket shared = (SharedPacket) SharedPacket.forBroadcast(motion(), 3);
        BedrockPacketCodec codec = channel().pipeline().get(BedrockPacketCodec.class);

        ByteBuf first = shared.encoded(codec, BedrockPacketWrapper.create(0, 0, 0, shared, null));
        ByteBuf second = shared.encoded(codec, BedrockPacketWrapper.create(0, 0, 0, shared, null));

        Assertions.assertNotNull(first);
        Assertions.assertNotSame(first, second);
        Assertions.assertSame(root(first), root(second));
    }

    @Test
    void subClientsEncodeThemselves() {
        SharedPacket shared = (SharedPacket) SharedPacket.forBroadcast(motion(), 2);
        BedrockPacketCodec codec = channel().pipeline().get(BedrockPacketCodec.class);

        Assertions.assertNull(shared.encoded(codec, BedrockPacketWrapper.create(0, 1, 0, shared, null)));
        Assertions.assertArrayEquals(write(channel(), motion()), write(channel(), shared));
    }

    @Test
    void notWrappedWhenNothingIsShared() {
        SetActorMotionPacket single = motion();
        Assertions.assertSame(single, SharedPacket.forBroadcast(single, 1));
        Assertions.assertSame(single, SharedPacket.forBroadcast(single, 0));

        UnknownPacket owning = new UnknownPacket();
        Assertions.assertSame(owning, SharedPacket.forBroadcast(owning, 5));

        BedrockPacket shared = SharedPacket.forBroadcast(motion(), 2);
        Assertions.assertSame(shared, SharedPacket.forBroadcast(shared, 2));
    }
}
