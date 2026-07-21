package org.powernukkitx.level.village;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.List;

public record VillagePois(List<VillagePoiGroup> poi) {
    public VillagePois {
        poi = List.copyOf(poi);
    }

    public static VillagePois fromCompound(CompoundTag tag) {
        return new VillagePois(tag.getList("POI", CompoundTag.class).getAll().stream()
                .map(VillagePoiGroup::fromCompound).toList());
    }

    public CompoundTag toCompound() {
        ListTag<CompoundTag> poiTag = new ListTag<>();
        poi.forEach(entry -> poiTag.add(entry.toCompound()));
        return new CompoundTag().putList("POI", poiTag);
    }
}
