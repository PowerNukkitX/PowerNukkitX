package org.powernukkitx.block.customblock.component;

import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Crafting table component for custom blocks.
 */
public class CraftingTableComponent implements BlockComponent {
    private final List<CraftingTableRecipe> recipes = new ArrayList<>();

    public CraftingTableComponent() {
    }

    public CraftingTableComponent recipe(String name, CraftingTableRecipe recipe) {
        recipes.add(recipe);
        return this;
    }

    @Override
    public BlockComponentIds getId() {
        return BlockComponentIds.CRAFTING_TABLE;
    }

    @Override
    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("crafting_tags", recipes.size());
        return tag;
    }

    public static class CraftingTableRecipe {
        private String name;
        private String uuid;
        private int width;
        private int height;

        public CraftingTableRecipe name(String name) {
            this.name = name;
            return this;
        }

        public CraftingTableRecipe uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public CraftingTableRecipe size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }
    }
}