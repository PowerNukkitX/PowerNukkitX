package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemStainedGlass extends Item {
    public ItemStainedGlass() {
        this(0, 1);
    }

    public ItemStainedGlass(Integer meta) {
        this(meta, 1);
    }

    public ItemStainedGlass(Integer meta, int count) {
        super(STAINED_GLASS, meta, count);
    }

    public void internalAdjust() {
        switch (getDamage()) {
            case 0:
                this.name = "White Stained Glass";
                setBlockUnsafe(Block.get(BlockID.WHITE_STAINED_GLASS));
            case 1:
                this.name = "Orange Stained Glass";
                setBlockUnsafe(Block.get(BlockID.ORANGE_STAINED_GLASS));
            case 2:
                this.name = "Magenta Stained Glass";
                setBlockUnsafe(Block.get(BlockID.MAGENTA_STAINED_GLASS));
            case 3:
                this.name = "Light Blue Stained Glass";
                setBlockUnsafe(Block.get(BlockID.LIGHT_BLUE_STAINED_GLASS));
            case 4:
                this.name = "Yellow Stained Glass";
                setBlockUnsafe(Block.get(BlockID.YELLOW_STAINED_GLASS));
            case 5:
                this.name = "Lime Stained Glass";
                setBlockUnsafe(Block.get(BlockID.LIME_STAINED_GLASS));
            case 6:
                this.name = "Pink Stained Glass";
                setBlockUnsafe(Block.get(BlockID.PINK_STAINED_GLASS));
            case 7:
                this.name = "Gray Stained Glass";
                setBlockUnsafe(Block.get(BlockID.GRAY_STAINED_GLASS));
            case 8:
                this.name = "Light Gray Stained Glass";
                setBlockUnsafe(Block.get(BlockID.LIGHT_GRAY_STAINED_GLASS));
            case 9:
                this.name = "Cyan Stained Glass";
                setBlockUnsafe(Block.get(BlockID.CYAN_STAINED_GLASS));
            case 10:
                this.name = "Purple Stained Glass";
                setBlockUnsafe(Block.get(BlockID.PURPLE_STAINED_GLASS));
            case 11:
                this.name = "Blue Stained Glass";
                setBlockUnsafe(Block.get(BlockID.BLUE_STAINED_GLASS));
            case 12:
                this.name = "Brown Stained Glass";
                setBlockUnsafe(Block.get(BlockID.BROWN_STAINED_GLASS));
            case 13:
                this.name = "Green Stained Glass";
                setBlockUnsafe(Block.get(BlockID.GREEN_STAINED_GLASS));
            case 14:
                this.name = "Red Stained Glass";
                setBlockUnsafe(Block.get(BlockID.RED_STAINED_GLASS));
            case 15:
                this.name = "Black Stained Glass";
                setBlockUnsafe(Block.get(BlockID.BLACK_STAINED_GLASS));
        }
        this.meta = 0;
    }
}