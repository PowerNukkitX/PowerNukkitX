package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShowStoreOfferPacket extends DataPacket {
    public static byte MARKETPLACE_OFFER = 0;
    public static byte DRESSING_ROOM_OFFER = 1;
    public static byte THIRD_PARTY_SERVER_PAGE = 2;


    public UUID offerId;
    public byte type;
    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.offerId = byteBuf.readUUID();
        this.type = byteBuf.readByte();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeUUID(offerId);
        byteBuf.writeByte(type);
    }

    @Override
    public int pid() {
        return ProtocolInfo.SHOW_STORE_OFFER_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
