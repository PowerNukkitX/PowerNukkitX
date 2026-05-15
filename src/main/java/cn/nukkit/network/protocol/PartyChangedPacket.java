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
    private boolean isPartyLeader;

    @Override
    public void decode(HandleByteBuf byteBuf) {
        final boolean present = byteBuf.readBoolean();
        if (present) {
            setPartyId(byteBuf.readString());
            setPartyLeader(byteBuf.readBoolean());
        }
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {

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
