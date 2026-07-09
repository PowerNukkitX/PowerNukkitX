package org.powernukkitx.item.customitem.legacy.components;

import org.powernukkitx.nbt.tag.ByteArrayTag;
import org.powernukkitx.nbt.tag.ByteTag;
import org.powernukkitx.nbt.tag.Tag;

public class LegacyFoilComponent extends ItemLegacyComponent{
    @Override
    public LegacyComponentIds getIdentifier() {
        return LegacyComponentIds.FOIL;
    }

    @Override
    public Tag getTag() {
        return new ByteTag((byte) 1);
    }
}
