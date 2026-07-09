package org.powernukkitx.item.customitem.legacy;

import org.powernukkitx.item.customitem.ItemVersion;
import org.powernukkitx.item.customitem.legacy.components.ItemLegacyComponent;

class ItemCustomLegacyDefinition extends ItemCustomDefinition
{
    ItemCustomLegacyDefinition() {
        super(ItemVersion.LEGACY);
    }

    public void addComponent(ItemLegacyComponent component) {
        this.components.put(component.getIdentifier().getStringId(), component.getTag());
    }

    public void removeComponent(ItemLegacyComponent component) {
        this.components.remove(component.getIdentifier().getStringId());
    }
}
