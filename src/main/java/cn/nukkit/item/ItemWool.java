package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemWool extends Item {
    public ItemWool() {
        this(0, 1);
    }

    public ItemWool(Integer aux) {
        this(aux, 1);
    }

    public ItemWool(Integer aux, int count) {
        super(WOOL, aux, count);
    }

    public void internalAdjust() {
        switch (getDamage()) {
            case 0:
                this.name = "White Wool";
                setBlockUnsafe(Block.get(BlockID.WHITE_WOOL));
            case 1:
                this.name = "Orange Wool";
                setBlockUnsafe(Block.get(BlockID.ORANGE_WOOL));
            case 2:
                this.name = "Magenta Wool";
                setBlockUnsafe(Block.get(BlockID.MAGENTA_WOOL));
            case 3:
                this.name = "Light Blue Wool";
                setBlockUnsafe(Block.get(BlockID.LIGHT_BLUE_WOOL));
            case 4:
                this.name = "Yellow Wool";
                setBlockUnsafe(Block.get(BlockID.YELLOW_WOOL));
            case 5:
                this.name = "Lime Wool";
                setBlockUnsafe(Block.get(BlockID.LIME_WOOL));
            case 6:
                this.name = "Pink Wool";
                setBlockUnsafe(Block.get(BlockID.PINK_WOOL));
            case 7:
                this.name = "Gray Wool";
                setBlockUnsafe(Block.get(BlockID.GRAY_WOOL));
            case 8:
                this.name = "Light Gray Wool";
                setBlockUnsafe(Block.get(BlockID.LIGHT_GRAY_WOOL));
            case 9:
                this.name = "Cyan Wool";
                setBlockUnsafe(Block.get(BlockID.CYAN_WOOL));
            case 10:
                this.name = "Purple Wool";
                setBlockUnsafe(Block.get(BlockID.PURPLE_WOOL));
            case 11:
                this.name = "Blue Wool";
                setBlockUnsafe(Block.get(BlockID.BLUE_WOOL));
            case 12:
                this.name = "Brown Wool";
                setBlockUnsafe(Block.get(BlockID.BROWN_WOOL));
            case 13:
                this.name = "Green Wool";
                setBlockUnsafe(Block.get(BlockID.GREEN_WOOL));
            case 14:
                this.name = "Red Wool";
                setBlockUnsafe(Block.get(BlockID.RED_WOOL));
            case 15:
                this.name = "Black Wool";
                setBlockUnsafe(Block.get(BlockID.BLACK_WOOL));
        }
        this.meta = 0;
    }
}