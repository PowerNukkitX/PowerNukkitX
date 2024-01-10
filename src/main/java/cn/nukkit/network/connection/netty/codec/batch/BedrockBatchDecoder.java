package cn.nukkit.network.connection.netty.codec.batch;

import cn.nukkit.utils.ByteBufVarInt;
import cn.nukkit.utils.VarInt;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

@Sharable
public class BedrockBatchDecoder extends MessageToMessageDecoder<ByteBuf> {

    public static final String NAME = "bedrock-batch-decoder";

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        while (in.isReadable()) {
            int packetLength = ByteBufVarInt.readUnsignedInt(in);
            ByteBuf packetBuf = in.readRetainedSlice(packetLength);

            out.add(packetBuf);
        }
    }
}
