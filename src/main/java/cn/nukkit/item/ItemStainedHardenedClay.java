package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemStainedHardenedClay extends Item {
    public ItemStainedHardenedClay() {
        this(0, 1);
    }

    public ItemStainedHardenedClay(Integer meta) {
        this(meta, 1);
    }

    public ItemStainedHardenedClay(Integer meta, int count) {
        super(STAINED_HARDENED_CLAY, meta, count);
    }

    public void internalAdjust() {
        switch (getDamage()) {
            case 0:
                this.name = "White Terracotta";
                setBlockUnsafe(Block.get(BlockID.WHITE_TERRACOTTA));
            case 1:
                this.name = "Orange Terracotta";
                setBlockUnsafe(Block.get(BlockID.ORANGE_TERRACOTTA));
            case 2:
                this.name = "Magenta Terracotta";
                setBlockUnsafe(Block.get(BlockID.MAGENTA_TERRACOTTA));
            case 3:
                this.name = "Light Blue Terracotta";
                setBlockUnsafe(Block.get(BlockID.LIGHT_BLUE_TERRACOTTA));
            case 4:
                this.name = "Yellow Terracotta";
                setBlockUnsafe(Block.get(BlockID.YELLOW_TERRACOTTA));
            case 5:
                this.name = "Lime Terracotta";
                setBlockUnsafe(Block.get(BlockID.LIME_TERRACOTTA));
            case 6:
                this.name = "Pink Terracotta";
                setBlockUnsafe(Block.get(BlockID.PINK_TERRACOTTA));
            case 7:
                this.name = "Gray Terracotta";
                setBlockUnsafe(Block.get(BlockID.GRAY_TERRACOTTA));
            case 8:
                this.name = "Light Gray Terracotta";
                setBlockUnsafe(Block.get(BlockID.LIGHT_GRAY_TERRACOTTA));
            case 9:
                this.name = "Cyan Terracotta";
                setBlockUnsafe(Block.get(BlockID.CYAN_TERRACOTTA));
            case 10:
                this.name = "Purple Terracotta";
                setBlockUnsafe(Block.get(BlockID.PURPLE_TERRACOTTA));
            case 11:
                this.name = "Blue Terracotta";
                setBlockUnsafe(Block.get(BlockID.BLUE_TERRACOTTA));
            case 12:
                this.name = "Brown Terracotta";
                setBlockUnsafe(Block.get(BlockID.BROWN_TERRACOTTA));
            case 13:
                this.name = "Green Terracotta";
                setBlockUnsafe(Block.get(BlockID.GREEN_TERRACOTTA));
            case 14:
                this.name = "Red Terracotta";
                setBlockUnsafe(Block.get(BlockID.RED_TERRACOTTA));
            case 15:
                this.name = "Black Terracotta";
                setBlockUnsafe(Block.get(BlockID.BLACK_TERRACOTTA));
        }
        this.meta = 0;
    }
}