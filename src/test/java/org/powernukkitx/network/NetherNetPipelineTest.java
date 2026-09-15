package org.powernukkitx.network;

import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.netty.codec.FrameIdCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.batch.BedrockBatchDecoder;
import org.cloudburstmc.protocol.bedrock.netty.codec.batch.BedrockBatchEncoder;
import org.cloudburstmc.protocol.bedrock.netty.codec.compression.CompressionCodec;
import org.cloudburstmc.protocol.bedrock.netty.codec.packet.BedrockPacketCodec;
import org.junit.jupiter.api.Test;
import org.powernukkitx.network.nethernet.NetherNetServerInitializer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class NetherNetPipelineTest {

    private static Channel initialized() {
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.pipeline().addLast(new NetherNetServerInitializer(11, false) {
            @Override
            protected void initSession(BedrockServerSession session) {
            }
        });
        return channel;
    }

    @Test
    void carriesNoFrameId() {
        // NetherNet delivers one batch per message, so there is no 0xFE frame id to strip
        assertNull(initialized().pipeline().get(FrameIdCodec.NAME));
    }

    @Test
    void buildsTheBedrockPipeline() {
        Channel channel = initialized();

        for (String name : List.of(CompressionCodec.NAME, BedrockBatchDecoder.NAME, BedrockBatchEncoder.NAME,
            BedrockPacketCodec.NAME, BedrockPeer.NAME)) {
            assertNotNull(channel.pipeline().get(name), name + " is missing from the pipeline");
        }
    }

    @Test
    void compressionSitsAheadOfBatching() {
        List<String> names = initialized().pipeline().names();
        assertEquals(true, names.indexOf(CompressionCodec.NAME) < names.indexOf(BedrockBatchDecoder.NAME));
    }
}
