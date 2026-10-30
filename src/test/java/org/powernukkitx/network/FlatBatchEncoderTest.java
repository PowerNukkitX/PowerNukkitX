package org.powernukkitx.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import org.cloudburstmc.math.vector.Vector3f;
import org.cloudburstmc.protocol.bedrock.netty.BedrockBatchWrapper;
import org.cloudburstmc.protocol.bedrock.netty.BedrockPacketWrapper;
import org.cloudburstmc.protocol.bedrock.netty.codec.batch.BedrockBatchEncoder;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec_v3;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.SetActorMotionPacket;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;
import org.cloudburstmc.protocol.bedrock.data.TextPacketType;
import org.cloudburstmc.protocol.bedrock.data.payload.text.MessageOnly;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class FlatBatchEncoderTest {

    private static EmbeddedChannel channel(ChannelHandler batchEncoder) {
        BedrockPacketCodec_v3 codec = new BedrockPacketCodec_v3();
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline().addLast(BedrockBatchEncoder.NAME, batchEncoder);
        channel.pipeline().addLast(BedrockPacketCodec.NAME, codec);
        codec.setCodec(NetworkConstants.CODEC);
        return channel;
    }

    private static List<BedrockPacket> packets() {
        SetActorMotionPacket motion = new SetActorMotionPacket();
        motion.setTargetRuntimeID(42L);
        motion.setMotion(Vector3f.from(0.5f, -1f, 2f));

        MessageOnly body = new MessageOnly();
        body.setMessage("x".repeat(300));
        TextPacket text = new TextPacket();
        text.setMessageType(TextPacketType.RAW);
        text.setSendersXUID("");
        text.setBody(body);

        SetActorMotionPacket other = new SetActorMotionPacket();
        other.setTargetRuntimeID(7L);
        other.setMotion(Vector3f.ZERO);
        return List.of(motion, text, other);
    }

    private static byte[] batch(ChannelHandler batchEncoder) {
        EmbeddedChannel channel = channel(batchEncoder);
        for (BedrockPacket packet : packets()) {
            channel.write(BedrockPacketWrapper.create(0, 0, 0, packet, null));
        }
        channel.flush();
        BedrockBatchWrapper written = channel.readOutbound();
        Assertions.assertNull(channel.readOutbound(), "one flush writes one batch");
        try {
            ByteBuf uncompressed = written.getUncompressed();
            return ByteBufUtil.getBytes(uncompressed, uncompressed.readerIndex(), uncompressed.readableBytes());
        } finally {
            written.release();
            channel.finishAndReleaseAll();
        }
    }

    @Test
    void writesTheSameBatchBytesAsTheProtocolLibrary() {
        Assertions.assertArrayEquals(batch(new BedrockBatchEncoder()), batch(new FlatBatchEncoder()));
    }

    @Test
    void flushWithoutPacketsWritesNothing() {
        EmbeddedChannel channel = channel(new FlatBatchEncoder());
        channel.flush();
        Assertions.assertNull(channel.readOutbound());
        channel.finishAndReleaseAll();
    }

    @Test
    void eachFlushWritesItsOwnBatch() {
        EmbeddedChannel channel = channel(new FlatBatchEncoder());
        for (BedrockPacket packet : packets()) {
            channel.writeAndFlush(BedrockPacketWrapper.create(0, 0, 0, packet, null));
        }
        int batches = 0;
        BedrockBatchWrapper batch;
        while ((batch = channel.readOutbound()) != null) {
            Assertions.assertEquals(1, batch.getPackets().size());
            batch.release();
            batches++;
        }
        Assertions.assertEquals(3, batches);
        channel.finishAndReleaseAll();
    }
}
