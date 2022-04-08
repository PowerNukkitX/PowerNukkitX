package cn.nukkit.network.protocol;

public class SetObjectivePacket extends DataPacket {

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
