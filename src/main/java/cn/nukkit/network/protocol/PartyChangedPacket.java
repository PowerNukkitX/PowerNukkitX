package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.OptionalValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PartyChangedPacket extends DataPacket {
    private String partyId;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        setPartyId(byteBuf.readOptional(null, byteBuf::readString));
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeNotNull(partyId, byteBuf::writeString);
    }

    @Override
    public int pid() {
        return ProtocolInfo.PARTY_CHANGED_PACKET;
    }

    @Override
    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
