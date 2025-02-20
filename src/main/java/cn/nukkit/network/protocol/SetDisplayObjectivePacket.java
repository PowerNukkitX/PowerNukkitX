package cn.nukkit.network.protocol;

import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SetDisplayObjectivePacket extends DataPacket {
    public DisplaySlot displaySlot;
    public String
            objectiveName,
            displayName,
            criteriaName;
    public SortOrder sortOrder;

    @Override
    public void decode(HandleByteBuf byteBuf) {

    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
        byteBuf.writeString(this.displaySlot.getSlotName());
        byteBuf.writeString(this.objectiveName);
        byteBuf.writeString(this.displayName);
        byteBuf.writeString(this.criteriaName);
        byteBuf.writeVarInt(this.sortOrder.ordinal());
    }

    @Override
    public int pid() {
        return ProtocolInfo.SET_DISPLAY_OBJECTIVE_PACKET;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
