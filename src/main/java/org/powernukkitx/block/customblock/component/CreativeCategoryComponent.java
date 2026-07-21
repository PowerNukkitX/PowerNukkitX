package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.Locale;

/**
 * Creative category component for custom blocks.
 * Controls the category and group in the creative inventory.
 */
public class CreativeCategoryComponent implements BlockComponent {
    private String category = "construction";
    private String group = "";
    private boolean hiddenInCommands = false;

    public CreativeCategoryComponent() {
    }

    public CreativeCategoryComponent(String category) {
        this.category = (category != null && !category.isBlank())
                ? category.toLowerCase(Locale.ROOT) : "construction";
    }

    public CreativeCategoryComponent category(String category) {
        this.category = (category != null && !category.isBlank())
                ? category.toLowerCase(Locale.ROOT) : "construction";
        return this;
    }

    public CreativeCategoryComponent group(String group) {
        this.group = (group != null) ? group : "";
        return this;
    }

    public CreativeCategoryComponent hiddenInCommands(boolean hidden) {
        this.hiddenInCommands = hidden;
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.CREATIVE_CATEGORY;
    }

    @Override
    public CompoundTag toNBT() {
        return new CompoundTag()
                .putString("category", category)
                .putString("group", group)
                .putByte("is_hidden_in_commands", (byte) (hiddenInCommands ? 1 : 0));
    }
}