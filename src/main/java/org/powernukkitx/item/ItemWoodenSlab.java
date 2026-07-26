package org.powernukkitx.item;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockAcaciaSlab;
import org.powernukkitx.block.BlockBirchSlab;
import org.powernukkitx.block.BlockDarkOakSlab;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockJungleSlab;
import org.powernukkitx.block.BlockOakSlab;
import org.powernukkitx.block.BlockSpruceSlab;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.block.property.enums.MinecraftVerticalHalf;

public class ItemWoodenSlab extends Item {
    public ItemWoodenSlab() {
        this(0, 1);
    }

    public ItemWoodenSlab(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenSlab(Integer meta, int count) {
        super(WOODEN_SLAB, meta, count);
    }

    public void internalAdjust() {
        switch (getDamage()) {
            case 0, 6, 7 -> {
                name = "Oak Slab";
                setBlockUnsafe(Block.get(BlockID.OAK_SLAB));
            }
            case 1 -> {
                name = "Spruce Slab";
                setBlockUnsafe(Block.get(BlockID.SPRUCE_SLAB));
            }
            case 2 -> {
                name = "Birch Slab";
                setBlockUnsafe(Block.get(BlockID.BIRCH_SLAB));
            }
            case 3 -> {
                name = "Jungle Slab";
                setBlockUnsafe(Block.get(BlockID.JUNGLE_SLAB));
            }
            case 4 -> {
                name = "Acacia Slab";
                setBlockUnsafe(Block.get(BlockID.ACACIA_SLAB));
            }
            case 5 -> {
                name = "Dark Oak Slab";
                setBlockUnsafe(Block.get(BlockID.DARK_OAK_SLAB));
            }
            case 8, 14, 15 -> {
                name = "Oak Slab (Top)";
                setBlockUnsafe(BlockOakSlab.PROPERTIES.getBlockState(CommonBlockProperties.MINECRAFT_VERTICAL_HALF, MinecraftVerticalHalf.TOP).toBlock());
            }
            case 9 -> {
                name = "Spruce Slab (Top)";
                setBlockUnsafe(BlockSpruceSlab.PROPERTIES.getBlockState(CommonBlockProperties.MINECRAFT_VERTICAL_HALF, MinecraftVerticalHalf.TOP).toBlock());
            }
            case 10 -> {
                name = "Birch Slab (Top)";
                setBlockUnsafe(BlockBirchSlab.PROPERTIES.getBlockState(CommonBlockProperties.MINECRAFT_VERTICAL_HALF, MinecraftVerticalHalf.TOP).toBlock());
            }
            case 11 -> {
                name = "Jungle Slab (Top)";
                setBlockUnsafe(BlockJungleSlab.PROPERTIES.getBlockState(CommonBlockProperties.MINECRAFT_VERTICAL_HALF, MinecraftVerticalHalf.TOP).toBlock());
            }
            case 12 -> {
                name = "Acacia Slab (Top)";
                setBlockUnsafe(BlockAcaciaSlab.PROPERTIES.getBlockState(CommonBlockProperties.MINECRAFT_VERTICAL_HALF, MinecraftVerticalHalf.TOP).toBlock());
            }
            case 13 -> {
                name = "Dark Oak Slab (Top)";
                setBlockUnsafe(BlockDarkOakSlab.PROPERTIES.getBlockState(CommonBlockProperties.MINECRAFT_VERTICAL_HALF, MinecraftVerticalHalf.TOP).toBlock());
            }
            default -> throw new IllegalArgumentException("Invalid damage: " + getDamage());
        }
        this.meta = 0;
    }
}