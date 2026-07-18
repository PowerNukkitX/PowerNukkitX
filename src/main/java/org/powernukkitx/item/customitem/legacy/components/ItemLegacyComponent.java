package org.powernukkitx.item.customitem.legacy.components;

import org.powernukkitx.nbt.tag.Tag;

abstract public class ItemLegacyComponent {
    abstract public LegacyComponentIds getIdentifier();
    abstract public Tag getTag();
}
