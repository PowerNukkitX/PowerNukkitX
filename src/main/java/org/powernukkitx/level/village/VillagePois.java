package org.powernukkitx.level.village;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;

import java.util.List;
import java.util.ArrayList;

public final class VillagePois {
    private final List<VillagePoiGroup> poi;

    public VillagePois(List<VillagePoiGroup> poi) {
        this.poi = new ArrayList<>(poi);
    }

    public List<VillagePoiGroup> poi() { return poi; }

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
