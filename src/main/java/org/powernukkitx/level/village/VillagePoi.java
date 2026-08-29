package org.powernukkitx.level.village;

import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.nbt.tag.CompoundTag;

public final class VillagePoi {
    private final long capacity;
    private final String initEvent;
    private final String name;
    private long ownerCount;
    private final float radius;
    private final boolean skip;
    private final String soundEvent;
    private final PoiType type;
    private final boolean useAabb;
    private final long weight;
    private final BlockVector3 position;

    public VillagePoi(long capacity, String initEvent, String name, long ownerCount, float radius,
                      boolean skip, String soundEvent, PoiType type, boolean useAabb, long weight,
                      BlockVector3 position) {
        this.capacity = capacity;
        this.initEvent = initEvent;
        this.name = name;
        this.ownerCount = ownerCount;
        this.radius = radius;
        this.skip = skip;
        this.soundEvent = soundEvent;
        this.type = type;
        this.useAabb = useAabb;
        this.weight = weight;
        this.position = position;
    }

    public long capacity() { return capacity; }
    public String initEvent() { return initEvent; }
    public String name() { return name; }
    public long ownerCount() { return ownerCount; }
    public float radius() { return radius; }
    public boolean skip() { return skip; }
    public String soundEvent() { return soundEvent; }
    public PoiType type() { return type; }
    public boolean useAabb() { return useAabb; }
    public long weight() { return weight; }
    public BlockVector3 position() { return position; }

    public void setOwnerCount(long ownerCount) { this.ownerCount = ownerCount; }

    public VillagePoi(PoiType type, BlockVector3 position) {
        this(type == PoiType.MEETING ? 32 : 1, "", "", 0, 0, false, "", type, false, 0, position);
    }

    public boolean hasSpace() {
        return ownerCount < capacity;
    }

    public static VillagePoi fromCompound(CompoundTag tag) {
        int type = tag.getInt("Type");
        PoiType[] values = PoiType.values();
        if (type < 0 || type >= values.length) {
            throw new IllegalArgumentException("Unknown village POI type: " + type);
        }
        return new VillagePoi(tag.getLong("Capacity"), tag.getString("InitEvent"), tag.getString("Name"),
                tag.getLong("OwnerCount"), tag.getFloat("Radius"), tag.getBoolean("Skip"),
                tag.getString("SoundEvent"), values[type], tag.getBoolean("UseAABB"), tag.getLong("Weight"),
                new BlockVector3(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z")));
    }

    public CompoundTag toCompound() {
        return new CompoundTag()
                .putLong("Capacity", capacity)
                .putString("InitEvent", initEvent)
                .putString("Name", name)
                .putLong("OwnerCount", ownerCount)
                .putFloat("Radius", radius)
                .putBoolean("Skip", skip)
                .putString("SoundEvent", soundEvent)
                .putInt("Type", type.ordinal())
                .putBoolean("UseAABB", useAabb)
                .putLong("Weight", weight)
                .putInt("X", position.x)
                .putInt("Y", position.y)
                .putInt("Z", position.z);
    }

}
