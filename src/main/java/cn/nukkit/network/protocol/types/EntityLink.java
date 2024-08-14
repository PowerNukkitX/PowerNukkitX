package cn.nukkit.network.protocol.types;

public class EntityLink {

    public static final EntityLink[] EMPTY_ARRAY = new EntityLink[0];

    public long fromEntityUniqueId;
    public long toEntityUniqueId;
    public byte type;
    public boolean immediate;
    public boolean riderInitiated;
    public float vehicleAngularVelocity;

    @Deprecated
    public EntityLink(long fromEntityUniqueId, long toEntityUniqueId, Type type, boolean immediate, boolean riderInitiated) {
        this(fromEntityUniqueId, toEntityUniqueId, type, immediate, riderInitiated, 0f);
    }

    public EntityLink(long fromEntityUniqueId, long toEntityUniqueId, Type type, boolean immediate, boolean riderInitiated, float vehicleAngularVelocity) {
        this.fromEntityUniqueId = fromEntityUniqueId;
        this.toEntityUniqueId = toEntityUniqueId;
        this.type = (byte) type.ordinal();
        this.immediate = immediate;
        this.riderInitiated = riderInitiated;
        this.vehicleAngularVelocity = vehicleAngularVelocity;
    }

    public enum Type {
        REMOVE,
        RIDER,
        PASSENGER;
    }
}
