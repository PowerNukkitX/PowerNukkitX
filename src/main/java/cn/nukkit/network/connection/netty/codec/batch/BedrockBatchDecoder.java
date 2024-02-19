package cn.nukkit.network.connection.netty.codec.batch;

import cn.nukkit.network.connection.netty.BedrockBatchWrapper;
import cn.nukkit.utils.ByteBufVarInt;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

@Sharable
public class BedrockBatchDecoder extends MessageToMessageDecoder<BedrockBatchWrapper> {

    public static final String NAME = "bedrock-batch-decoder";

    @Override
    protected void decode(ChannelHandlerContext ctx, BedrockBatchWrapper msg, List<Object> out) {
        if (msg.getUncompressed() == null) {
            throw new IllegalStateException("Batch packet was not decompressed");
        }

        ByteBuf buffer = msg.getUncompressed().slice();
        while (buffer.isReadable()) {
            int packetLength = ByteBufVarInt.readUnsignedInt(buffer);
            ByteBuf packetBuf = buffer.readRetainedSlice(packetLength);
            out.add(packetBuf);
        }
    }
}
