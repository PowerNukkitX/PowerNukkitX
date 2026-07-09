package org.powernukkitx.item.customitem.legacy.components;

import org.powernukkitx.nbt.tag.ByteTag;
import org.powernukkitx.nbt.tag.Tag;

public class LegacyHandEquippedComponent extends ItemLegacyComponent{
    @Override
    public LegacyComponentIds getIdentifier() {
        return LegacyComponentIds.HAND_EQUIPPED;
    }

    @Override
    public Tag getTag() {
        return new ByteTag((byte) 1);
    }
}
