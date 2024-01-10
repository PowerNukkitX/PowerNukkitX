package cn.nukkit.network.connection.netty.codec.packet;

import cn.nukkit.network.connection.netty.BedrockPacketWrapper;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.utils.ByteBufVarInt;
import io.netty.buffer.ByteBuf;

public class BedrockPacketCodec_v3 extends BedrockPacketCodec {

    @Override
    public void encodeHeader(ByteBuf buf, DataPacket msg) {
        int header = 0;
        header |= (msg.pid() & 0x3ff);
        header |= (msg.getSenderSubClientId() & 3) << 10;
        header |= (msg.getTargetSubClientId() & 3) << 12;
        ByteBufVarInt.writeUnsignedInt(buf, header);
    }

    @Override
    public void decodeHeader(ByteBuf buf, BedrockPacketWrapper msg) {
        int header = ByteBufVarInt.readUnsignedInt(buf);
        msg.setPacketId(header & 0x3ff);
        msg.setSenderSubClientId((header >> 10) & 3);
        msg.setTargetSubClientId((header >> 12) & 3);
    }
}
