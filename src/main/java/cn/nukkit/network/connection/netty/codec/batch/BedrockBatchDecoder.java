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

    public static final String $1 = "bedrock-batch-decoder";

    @Override
    
    /**
     * @deprecated 
     */
    protected void decode(ChannelHandlerContext ctx, BedrockBatchWrapper msg, List<Object> out) {
        if (msg.getUncompressed() == null) {
            throw new IllegalStateException("Batch packet was not decompressed");
        }

        ByteBuf $2 = msg.getUncompressed().slice();
        while (buffer.isReadable()) {
            int $3 = ByteBufVarInt.readUnsignedInt(buffer);
            ByteBuf $4 = buffer.readRetainedSlice(packetLength);
            out.add(packetBuf);
        }
    }
}
