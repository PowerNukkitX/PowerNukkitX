package cn.nukkit.network.connection.netty.codec.packet;

import cn.nukkit.network.connection.netty.BedrockPacketWrapper;
import io.netty.buffer.ByteBuf;

public class BedrockPacketCodec_v1 extends BedrockPacketCodec {

    @Override
    /**
     * @deprecated 
     */
    
    public void encodeHeader(ByteBuf buf, BedrockPacketWrapper msg) {
        buf.writeByte(msg.getPacketId() & 0xff);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decodeHeader(ByteBuf buf, BedrockPacketWrapper msg) {
        msg.setPacketId(buf.readUnsignedByte());
    }
}
