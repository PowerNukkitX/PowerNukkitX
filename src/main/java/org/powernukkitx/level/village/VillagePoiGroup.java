package org.powernukkitx.level.village;

import com.google.common.base.Preconditions;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.List;
import java.util.ArrayList;

public final class VillagePoiGroup {
    private final long villagerId;
    private final List<VillagePoi> instances;

    public VillagePoiGroup(long villagerId, List<VillagePoi> instances) {
        this.villagerId = villagerId;
        this.instances = new ArrayList<>(instances);
    }

    public long villagerId() { return villagerId; }
    public List<VillagePoi> instances() { return instances; }

    void set(VillagePoi instance) {
        instances.removeIf(existing -> existing.type() == instance.type());
        instances.add(instance);
    }

    public static VillagePoiGroup fromCompound(CompoundTag tag) {
        ListTag<CompoundTag> instancesTag = tag.getList("instances", CompoundTag.class);
        VillagePoi[] instances = new VillagePoi[PoiType.storageTypeCount()];
        for (int i = 0; i < instancesTag.size() && i < PoiType.storageTypeCount(); i++) {
            CompoundTag instanceTag = instancesTag.get(i);
            if (instanceTag.getBoolean("Skip")) continue;
            int typeId = instanceTag.getInt("Type");
            if (typeId < 0 || typeId >= PoiType.storageTypeCount()) continue;
            VillagePoi instance = VillagePoi.fromCompound(instanceTag);
            instances[instance.type().storageId()] = instance;
        }
        List<VillagePoi> parsed = new ArrayList<>(PoiType.storageTypeCount());
        for (VillagePoi instance : instances) if (instance != null) parsed.add(instance);
        return new VillagePoiGroup(tag.getLong("VillagerID"), parsed);
    }

    public CompoundTag toCompound() {
        ListTag<CompoundTag> instancesTag = new ListTag<>();
        for (int storageId = 0; storageId < PoiType.storageTypeCount(); storageId++) {
            PoiType type = PoiType.fromStorageId(storageId);
            VillagePoi selected = null;
            for (VillagePoi instance : instances) {
                if (instance.skip() || instance.type() != type) continue;
                Preconditions.checkState(selected == null, "Village POI group %s contains duplicate %s slots", villagerId, type);
                selected = instance;
            }
            instancesTag.add(selected == null ? new CompoundTag().putBoolean("Skip", true) : selected.toCompound());
        }
        return new CompoundTag().putLong("VillagerID", villagerId).putList("instances", instancesTag);
    }
}
