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
        adjust();
    }

    public void adjust() {
        switch (getDamage()) {
            case 0:
                this.name = "White Stained Glass";
                this.block = Block.get(BlockID.WHITE_STAINED_GLASS);
                return;
            case 1:
                this.name = "Orange Stained Glass";
                this.block = Block.get(BlockID.ORANGE_STAINED_GLASS);
                return;
            case 2:
                this.name = "Magenta Stained Glass";
                this.block = Block.get(BlockID.MAGENTA_STAINED_GLASS);
                return;
            case 3:
                this.name = "Light Blue Stained Glass";
                this.block = Block.get(BlockID.LIGHT_BLUE_STAINED_GLASS);
                return;
            case 4:
                this.name = "Yellow Stained Glass";
                this.block = Block.get(BlockID.YELLOW_STAINED_GLASS);
                return;
            case 5:
                this.name = "Lime Stained Glass";
                this.block = Block.get(BlockID.LIME_STAINED_GLASS);
                return;
            case 6:
                this.name = "Pink Stained Glass";
                this.block = Block.get(BlockID.PINK_STAINED_GLASS);
                return;
            case 7:
                this.name = "Gray Stained Glass";
                this.block = Block.get(BlockID.GRAY_STAINED_GLASS);
                return;
            case 8:
                this.name = "Light Gray Stained Glass";
                this.block = Block.get(BlockID.LIGHT_GRAY_STAINED_GLASS);
                return;
            case 9:
                this.name = "Cyan Stained Glass";
                this.block = Block.get(BlockID.CYAN_STAINED_GLASS);
                return;
            case 10:
                this.name = "Purple Stained Glass";
                this.block = Block.get(BlockID.PURPLE_STAINED_GLASS);
                return;
            case 11:
                this.name = "Blue Stained Glass";
                this.block = Block.get(BlockID.BLUE_STAINED_GLASS);
                return;
            case 12:
                this.name = "Brown Stained Glass";
                this.block = Block.get(BlockID.BROWN_STAINED_GLASS);
                return;
            case 13:
                this.name = "Green Stained Glass";
                this.block = Block.get(BlockID.GREEN_STAINED_GLASS);
                return;
            case 14:
                this.name = "Red Stained Glass";
                this.block = Block.get(BlockID.RED_STAINED_GLASS);
                return;
            case 15:
                this.name = "Black Stained Glass";
                this.block = Block.get(BlockID.BLACK_STAINED_GLASS);
        }
    }
}