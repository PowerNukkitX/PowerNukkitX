package cn.nukkit.network.protocol;

import cn.nukkit.scoreboard.data.DisplaySlot;
import cn.nukkit.scoreboard.data.SortOrder;

public class SetDisplayObjectivePacket extends DataPacket {

    public DisplaySlot displaySlot;

    public String
            objectiveName,
            displayName,
            criteriaName;

    public SortOrder sortOrder;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_DISPLAY_OBJECTIVE_PACKET;
    }

    @Override
    public void decode() {
        //only server -> client
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.displaySlot.getSlotName());
        this.putString(this.objectiveName);
        this.putString(this.displayName);
        this.putString(this.criteriaName);
        this.putVarInt(this.sortOrder.ordinal());
    }

}
