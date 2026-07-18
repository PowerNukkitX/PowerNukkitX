package org.powernukkitx.item.customitem.legacy.components;

import org.powernukkitx.nbt.tag.ByteTag;
import org.powernukkitx.nbt.tag.IntTag;
import org.powernukkitx.nbt.tag.Tag;

public class LegacyMaxDamageComponent extends ItemLegacyComponent{
    private final int maxDamage;

    public LegacyMaxDamageComponent(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    @Override
    public LegacyComponentIds getIdentifier() {
        return LegacyComponentIds.MAX_DAMAGE;
    }

    @Override
    public Tag getTag() {
        return new IntTag(this.maxDamage);
    }
}
