package cn.nukkit.network.protocol.types;

public class EntityLink {

    public static final EntityLink[] EMPTY_ARRAY = new EntityLink[0];

    public long fromEntityUniquieId;
    public long toEntityUniquieId;
    public byte type;
    public boolean immediate;

    public boolean riderInitiated;
    /**
     * @deprecated 
     */
    

    public EntityLink(long fromEntityUniquieId, long toEntityUniquieId, Type type, boolean immediate, boolean riderInitiated) {
        this.fromEntityUniquieId = fromEntityUniquieId;
        this.toEntityUniquieId = toEntityUniquieId;
        this.type = (byte) type.ordinal();
        this.immediate = immediate;
        this.riderInitiated = riderInitiated;
    }

    public enum Type {
        REMOVE,
        RIDER,
        PASSENGER;
    }
}
