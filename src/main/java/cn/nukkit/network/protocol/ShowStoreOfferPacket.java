package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ShowStoreOfferPacket extends DataPacket {
    public static byte MARKETPLACE_OFFER = 0;
    public static byte DRESSING_ROOM_OFFER = 1;
    public static byte THIRD_PARTY_SERVER_PAGE = 2;


    public String offerId;
    public byte type;
    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.offerId = byteBuf.readString();
        this.type = byteBuf.readByte();
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(offerId);
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
