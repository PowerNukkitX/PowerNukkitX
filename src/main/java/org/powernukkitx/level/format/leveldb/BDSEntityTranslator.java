package org.powernukkitx.level.format.leveldb;

import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.DoubleTag;
import org.powernukkitx.nbt.tag.FloatTag;
import org.powernukkitx.nbt.tag.LinkedCompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.registry.Registries;

public final class BDSEntityTranslator {
    public static CompoundTag translate(CompoundTag from) {
        LinkedCompoundTag linkedCompoundTag = new LinkedCompoundTag();
        if (from.contains("identifier")) {
            String identifier = from.getString("identifier");
            int entityNetworkId = Registries.ENTITY.getEntityNetworkId(identifier);
            if (entityNetworkId == 0) return null;
            linkedCompoundTag.putString("identifier", identifier);
        }
        if (from.containsList("Pos", Tag.TAG_Float)) {
            ListTag<FloatTag> pos = from.getList("Pos", FloatTag.class);
            ListTag<DoubleTag> target = new ListTag<>();
            for (var v : pos.getAll()) {
                target.add(new DoubleTag(v.data));
            }
            linkedCompoundTag.putList("Pos", target);
        } else {
            ListTag<DoubleTag> target = new ListTag<>();
            target.add(new DoubleTag(0));
            target.add(new DoubleTag(0));
            target.add(new DoubleTag(0));
            linkedCompoundTag.putList("Pos", target);
        }
        if (from.containsList("Motion", Tag.TAG_Float)) {
            ListTag<FloatTag> pos = from.getList("Motion", FloatTag.class);
            ListTag<DoubleTag> target = new ListTag<>();
            for (var v : pos.getAll()) {
                target.add(new DoubleTag(v.data));
            }
            from.putList("Motion", target);
            linkedCompoundTag.putList("Pos", target);
        } else {
            ListTag<DoubleTag> target = new ListTag<>();
            target.add(new DoubleTag(0));
            target.add(new DoubleTag(0));
            target.add(new DoubleTag(0));
            linkedCompoundTag.putList("Motion", target);
        }
        linkedCompoundTag.putList("Rotation", from.getList("Rotation"));
        return linkedCompoundTag;
    }
}
