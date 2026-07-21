package org.powernukkitx.level.village;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.List;

public record VillagePoiGroup(long villagerId, List<VillagePoi> instances) {
    public VillagePoiGroup {
        instances = List.copyOf(instances);
    }

    public static VillagePoiGroup fromCompound(CompoundTag tag) {
        return new VillagePoiGroup(tag.getLong("VillagerID"),
                tag.getList("instances", CompoundTag.class).getAll().stream().map(VillagePoi::fromCompound).toList());
    }

    public CompoundTag toCompound() {
        ListTag<CompoundTag> instancesTag = new ListTag<>();
        instances.forEach(instance -> instancesTag.add(instance.toCompound()));
        return new CompoundTag().putLong("VillagerID", villagerId).putList("instances", instancesTag);
    }
}
