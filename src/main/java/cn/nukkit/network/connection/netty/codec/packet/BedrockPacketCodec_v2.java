package cn.nukkit.network.connection.netty.codec.packet;

import cn.nukkit.network.connection.netty.BedrockPacketWrapper;
import io.netty.buffer.ByteBuf;

public class BedrockPacketCodec_v2 extends BedrockPacketCodec {

    @Override
    /**
     * @deprecated 
     */
    
    public void encodeHeader(ByteBuf buf, BedrockPacketWrapper msg) {
        buf.writeByte(msg.getPacketId());
        buf.writeByte(msg.getSenderSubClientId());
        buf.writeByte(msg.getTargetSubClientId());
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decodeHeader(ByteBuf buf, BedrockPacketWrapper msg) {
        msg.setPacketId(buf.readUnsignedByte());
        msg.setSenderSubClientId(buf.readUnsignedByte());
        msg.setTargetSubClientId(buf.readUnsignedByte());
    }
}
