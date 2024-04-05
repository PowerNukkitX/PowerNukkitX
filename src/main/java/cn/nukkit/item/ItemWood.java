package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemWood extends Item {
    public ItemWood() {
        this(0, 1);
    }

    public ItemWood(Integer meta) {
        this(meta, 1);
    }

    public ItemWood(Integer meta, int count) {
        super(WOOD, meta, count);
    }

    public void internalAdjust() {
        switch (getDamage()) {
            case 0, 6, 7, 14, 15 -> {
                name = "Oak Wood";
                setBlockUnsafe(Block.get(BlockID.OAK_WOOD));
            }
            case 1 -> {
                name = "Spruce Wood";
                setBlockUnsafe(Block.get(BlockID.SPRUCE_WOOD));
            }
            case 2 -> {
                name = "Birch Wood";
                setBlockUnsafe(Block.get(BlockID.BIRCH_WOOD));
            }
            case 3 -> {
                name = "Jungle Wood";
                setBlockUnsafe(Block.get(BlockID.JUNGLE_WOOD));
            }
            case 4 -> {
                name = "Acacia Wood";
                setBlockUnsafe(Block.get(BlockID.ACACIA_WOOD));
            }
            case 5 -> {
                name = "Dark Oak Wood";
                setBlockUnsafe(Block.get(BlockID.DARK_OAK_WOOD));
            }
            case 8 -> {
                name = "Stripped Oak Wood";
                setBlockUnsafe(Block.get(BlockID.STRIPPED_OAK_WOOD));
            }
            case 9 -> {
                name = "Stripped Spruce Wood";
                setBlockUnsafe(Block.get(BlockID.STRIPPED_SPRUCE_WOOD));
            }
            case 10 -> {
                name = "Stripped Birch Wood";
                setBlockUnsafe(Block.get(BlockID.STRIPPED_BIRCH_WOOD));
            }
            case 11 -> {
                name = "Stripped Jungle Wood";
                setBlockUnsafe(Block.get(BlockID.STRIPPED_JUNGLE_WOOD));
            }
            case 12 -> {
                name = "Stripped Acacia Wood";
                setBlockUnsafe(Block.get(BlockID.STRIPPED_ACACIA_WOOD));
            }
            case 13 -> {
                name = "Stripped Dark Oak Wood";
                setBlockUnsafe(Block.get(BlockID.STRIPPED_DARK_OAK_WOOD));
            }
            default -> throw new IllegalArgumentException("Invalid damage: " + getDamage());
        }
        this.meta = 0;
    }

    @Override
    public boolean equalItemBlock(Item item) {
        if (this.isBlock() && item.isBlock()) {
            return  this.getBlockUnsafe().getProperties() == item.getBlockUnsafe().getProperties();
        }
        return true;
    }
}