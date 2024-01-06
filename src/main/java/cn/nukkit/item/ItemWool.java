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
        adjust();
    }

    public void adjust() {
        switch (getDamage()) {
            case 0:
                this.name = "White Wool";
                this.block = Block.get(BlockID.WHITE_WOOL);
                return;
            case 1:
                this.name = "Orange Wool";
                this.block = Block.get(BlockID.ORANGE_WOOL);
                return;
            case 2:
                this.name = "Magenta Wool";
                this.block = Block.get(BlockID.MAGENTA_WOOL);
                return;
            case 3:
                this.name = "Light Blue Wool";
                this.block = Block.get(BlockID.LIGHT_BLUE_WOOL);
                return;
            case 4:
                this.name = "Yellow Wool";
                this.block = Block.get(BlockID.YELLOW_WOOL);
                return;
            case 5:
                this.name = "Lime Wool";
                this.block = Block.get(BlockID.LIME_WOOL);
                return;
            case 6:
                this.name = "Pink Wool";
                this.block = Block.get(BlockID.PINK_WOOL);
                return;
            case 7:
                this.name = "Gray Wool";
                this.block = Block.get(BlockID.GRAY_WOOL);
                return;
            case 8:
                this.name = "Light Gray Wool";
                this.block = Block.get(BlockID.LIGHT_GRAY_WOOL);
                return;
            case 9:
                this.name = "Cyan Wool";
                this.block = Block.get(BlockID.CYAN_WOOL);
                return;
            case 10:
                this.name = "Purple Wool";
                this.block = Block.get(BlockID.PURPLE_WOOL);
                return;
            case 11:
                this.name = "Blue Wool";
                this.block = Block.get(BlockID.BLUE_WOOL);
                return;
            case 12:
                this.name = "Brown Wool";
                this.block = Block.get(BlockID.BROWN_WOOL);
                return;
            case 13:
                this.name = "Green Wool";
                this.block = Block.get(BlockID.GREEN_WOOL);
                return;
            case 14:
                this.name = "Red Wool";
                this.block = Block.get(BlockID.RED_WOOL);
                return;
            case 15:
                this.name = "Black Wool";
                this.block = Block.get(BlockID.BLACK_WOOL);
        }
    }
}