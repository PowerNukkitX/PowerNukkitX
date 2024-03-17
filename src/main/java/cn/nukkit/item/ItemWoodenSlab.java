package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAcaciaSlab;
import cn.nukkit.block.BlockBirchSlab;
import cn.nukkit.block.BlockDarkOakSlab;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockJungleSlab;
import cn.nukkit.block.BlockOakSlab;
import cn.nukkit.block.BlockSpruceSlab;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.MinecraftVerticalHalf;

public class ItemWoodenSlab extends Item {
    public ItemWoodenSlab() {
        this(0, 1);
    }

    public ItemWoodenSlab(Integer meta) {
        this(meta, 1);
    }

    public ItemWoodenSlab(Integer meta, int count) {
        super(WOODEN_SLAB, meta, count);
        adjust();
    }

    public void adjust() {
        switch (getDamage()) {
            case 0, 6, 7 -> {
                name = "Oak Slab";
                block = Block.get(BlockID.OAK_SLAB);
            }
            case 1 -> {
                name = "Spruce Slab";
                block = Block.get(BlockID.SPRUCE_SLAB);
            }
            case 2 -> {
                name = "Birch Slab";
                block = Block.get(BlockID.BIRCH_SLAB);
            }
            case 3 -> {
                name = "Jungle Slab";
                block = Block.get(BlockID.JUNGLE_SLAB);
            }
            case 4 -> {
                name = "Acacia Slab";
                block = Block.get(BlockID.ACACIA_SLAB);
            }
            case 5 -> {
                name = "Dark Oak Slab";
                block = Block.get(BlockID.DARK_OAK_SLAB);
            }
            case 8, 14, 15 -> {
                name = "Oak Slab (Top)";
                block = BlockOakSlab.PROPERTIES.getBlockState(CommonBlockProperties.MINECRAFT_VERTICAL_HALF, MinecraftVerticalHalf.TOP).toBlock();
            }
            case 9 -> {
                name = "Spruce Slab (Top)";
                block = BlockSpruceSlab.PROPERTIES.getBlockState(CommonBlockProperties.MINECRAFT_VERTICAL_HALF, MinecraftVerticalHalf.TOP).toBlock();
            }
            case 10 -> {
                name = "Birch Slab (Top)";
                block = BlockBirchSlab.PROPERTIES.getBlockState(CommonBlockProperties.MINECRAFT_VERTICAL_HALF, MinecraftVerticalHalf.TOP).toBlock();
            }
            case 11 -> {
                name = "Jungle Slab (Top)";
                block = BlockJungleSlab.PROPERTIES.getBlockState(CommonBlockProperties.MINECRAFT_VERTICAL_HALF, MinecraftVerticalHalf.TOP).toBlock();
            }
            case 12 -> {
                name = "Acacia Slab (Top)";
                block = BlockAcaciaSlab.PROPERTIES.getBlockState(CommonBlockProperties.MINECRAFT_VERTICAL_HALF, MinecraftVerticalHalf.TOP).toBlock();
            }
            case 13 -> {
                name = "Dark Oak Slab (Top)";
                block = BlockDarkOakSlab.PROPERTIES.getBlockState(CommonBlockProperties.MINECRAFT_VERTICAL_HALF, MinecraftVerticalHalf.TOP).toBlock();
            }
            default -> throw new IllegalArgumentException("Invalid damage: " + getDamage());
        }
    }
}