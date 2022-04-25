package cn.nukkit.network.protocol;

/**
 * Server-bound packet to change the properties of a mob.
 *
 * @since v503
 */
public class ChangeMobPropertyPacket extends DataPacket {
    private long uniqueEntityId;
    private String property;
    private boolean boolValue;
    private String stringValue;
    private int intValue;
    private float floatValue;

    @Override
    public byte pid() {
        return ProtocolInfo.CHANGE_MOB_PROPERTY_PACKET;
    }

    @Override
    public void decode() {
        this.uniqueEntityId = getLong();
        this.property = getString();
        this.boolValue = getBoolean();
        this.stringValue = getString();
        this.intValue = getVarInt();
        this.floatValue = getLFloat();
    }

    @Override
    public void encode() {
        this.putLong(this.uniqueEntityId);
        this.putString(this.property);
        this.putBoolean(this.boolValue);
        this.putString(this.stringValue);
        this.putVarInt(this.intValue);
        this.putLFloat(this.floatValue);
    }
}
