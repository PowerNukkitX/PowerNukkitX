package cn.nukkit.network.protocol;

public class SetObjectivePacket extends DataPacket {

    public static String DISPLAY_SLOT_SIDEBAR = "sidebar";
    public static String DISPLAY_SLOT_LIST = "list";
    public static String DISPLAY_SLOT_BELOW_NAME = "belowName";

    public String
            displaySlot,
            objectiveName,
            displayName,
            criteriaName;

    public int sortOrder;

    @Override
    public byte pid() {
        return ProtocolInfo.SET_OBJECTIVE_PACKET;
    }

    @Override
    public void decode() {
        //only server -> client
    }

    @Override
    public void encode() {
        this.reset();
        this.putString(this.displaySlot);
        this.putString(this.objectiveName);
        this.putString(this.displayName);
        this.putString(this.criteriaName);
        this.putVarInt(this.sortOrder);
    }
}
