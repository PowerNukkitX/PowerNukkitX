package org.powernukkitx.level.village;

import com.google.common.base.Preconditions;
import org.powernukkitx.entity.data.profession.Profession;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.registry.mappings.MappingRegistries;

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
        this(type == PoiType.MEETING ? 20 : 1, "", "villager", 0, type == PoiType.MEETING ? 7 : 0.75f, false,
                "undefined", type, type == PoiType.HOME, 1, position);
        Preconditions.checkArgument(type != PoiType.ACQUIRABLE_JOB_SITE, "A job site POI requires a profession");
    }

    static VillagePoi jobSite(Profession profession, BlockVector3 position) {
        int index = profession.getIndex();
        Preconditions.checkArgument(index > 0 && index < 14, "No village POI definition for profession index %s", index);
        String name = switch (index) {
            case 1 -> "farmer";
            case 2 -> "fisherman";
            case 3 -> "shepherd";
            case 4 -> "fletcher";
            case 5 -> "librarian";
            case 6 -> "cartographer";
            case 7 -> "cleric";
            case 8 -> "armorer";
            case 9 -> "weaponsmith";
            case 10 -> "toolsmith";
            case 11 -> "butcher";
            case 12 -> "leatherworker";
            default -> "mason";
        };
        String initEvent = switch (index) {
            case 1 -> "minecraft:become_farmer";
            case 2 -> "minecraft:become_fisherman";
            case 3 -> "minecraft:become_sheperd";
            case 4 -> "minecraft:become_fletcher";
            case 5 -> "minecraft:become_librarian";
            case 6 -> "minecraft:become_cartographer";
            case 7 -> "minecraft:become_cleric";
            case 8 -> "minecraft:become_armorer";
            case 9 -> "minecraft:become_weaponsmith";
            case 10 -> "minecraft:become_toolsmith";
            case 11 -> "minecraft:become_butcher";
            case 12 -> "minecraft:become_leatherworker";
            default -> "minecraft:become_mason";
        };
        long weight = switch (index) {
            case 1 -> 1;
            case 2, 3, 13 -> 3;
            case 5, 7 -> 8;
            case 6 -> 6;
            default -> 5;
        };
        String soundEvent = MappingRegistries.LEVEL_SOUND_EVENT.get().get(profession.getWorkSoundEvent().ordinal());
        Preconditions.checkNotNull(soundEvent, "No Bedrock sound mapping for profession index %s", index);
        return new VillagePoi(1, initEvent, name, 0, 2, false, soundEvent,
                PoiType.ACQUIRABLE_JOB_SITE, false, weight, position);
    }

    public boolean hasSpace() {
        return !skip && ownerCount < capacity;
    }

    public static VillagePoi fromCompound(CompoundTag tag) {
        Preconditions.checkArgument(!tag.getBoolean("Skip"), "Skipped village POI entries do not contain a POI");
        PoiType type = PoiType.fromStorageId(tag.getInt("Type"));
        return new VillagePoi(tag.getLong("Capacity"), tag.getString("InitEvent"), tag.getString("Name"),
                tag.getLong("OwnerCount"), tag.getFloat("Radius"), false, tag.getString("SoundEvent"),
                type, tag.getBoolean("UseAABB"), tag.getLong("Weight"),
                new BlockVector3(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z")));
    }

    public CompoundTag toCompound() {
        if (skip) {
            return new CompoundTag().putBoolean("Skip", true);
        }
        return new CompoundTag()
                .putLong("Capacity", capacity)
                .putString("InitEvent", initEvent)
                .putString("Name", name)
                .putLong("OwnerCount", ownerCount)
                .putFloat("Radius", radius)
                .putBoolean("Skip", skip)
                .putString("SoundEvent", soundEvent)
                .putInt("Type", type.storageId())
                .putBoolean("UseAABB", useAabb)
                .putLong("Weight", weight)
                .putInt("X", position.x)
                .putInt("Y", position.y)
                .putInt("Z", position.z);
    }

}
