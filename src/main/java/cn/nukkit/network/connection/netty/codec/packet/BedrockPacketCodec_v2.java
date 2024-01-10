package cn.nukkit.network.connection.netty.codec.packet;

import cn.nukkit.network.connection.netty.BedrockPacketWrapper;
import cn.nukkit.network.protocol.DataPacket;
import io.netty.buffer.ByteBuf;

public class BedrockPacketCodec_v2 extends BedrockPacketCodec {

    @Override
    public void encodeHeader(ByteBuf buf, DataPacket msg) {
        buf.writeByte(msg.pid());
        buf.writeByte(msg.getSenderSubClientId());
        buf.writeByte(msg.getTargetSubClientId());
    }

    @Override
    public void decodeHeader(ByteBuf buf, BedrockPacketWrapper msg) {
        msg.setPacketId(buf.readUnsignedByte());
        msg.setSenderSubClientId(buf.readUnsignedByte());
        msg.setTargetSubClientId(buf.readUnsignedByte());
    }
}
