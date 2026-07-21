package org.powernukkitx.item.customitem.component;

import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Icon component for custom items.
 * Defines the texture icon used to render the item.
 */
public class IconComponent implements ItemComponent {
    private String textureName = "";

    public IconComponent() {
    }

    public IconComponent(String textureName) {
        this.textureName = textureName != null ? textureName : "";
    }

    public IconComponent texture(String textureName) {
        this.textureName = textureName != null ? textureName : "";
        return this;
    }

    @Override
    public ItemComponentIds getId() {
        return ItemComponentIds.ICON;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("texture", textureName);
        return tag;
    }
}