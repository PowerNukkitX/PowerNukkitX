package org.powernukkitx.level.village;

import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.nbt.tag.CompoundTag;

public record VillagePoi(long capacity, String initEvent, String name, long ownerCount, float radius,
                         boolean skip, String soundEvent, PoiType type, boolean useAabb, long weight,
                         BlockVector3 position) {

    public VillagePoi(PoiType type, BlockVector3 position) {
        this(type == PoiType.MEETING ? 32 : 1, "", "", 0, 0, false, "", type, false, 0, position);
    }

    public boolean hasSpace() {
        return ownerCount < capacity;
    }

    public VillagePoi withOwnerCount(long ownerCount) {
        return new VillagePoi(capacity, initEvent, name, ownerCount, radius, skip, soundEvent, type,
                useAabb, weight, position);
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
