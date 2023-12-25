package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemShulkerBox extends Item {
    public ItemShulkerBox() {
        this(0, 1);
    }

    public ItemShulkerBox(Integer meta) {
        this(meta, 1);
    }

    public ItemShulkerBox(Integer meta, int count) {
        super(SHULKER_BOX, meta, count);
        adjust();
    }

    public void adjust() {
        switch (getAux()) {
            case 0:
                this.name = "White Shulker Box";
                this.block = Block.get(BlockID.WHITE_SHULKER_BOX);
                return;
            case 1:
                this.name = "Orange Shulker Box";
                this.block = Block.get(BlockID.ORANGE_SHULKER_BOX);
                return;
            case 2:
                this.name = "Magenta Shulker Box";
                this.block = Block.get(BlockID.MAGENTA_SHULKER_BOX);
                return;
            case 3:
                this.name = "Light Blue Shulker Box";
                this.block = Block.get(BlockID.LIGHT_BLUE_SHULKER_BOX);
                return;
            case 4:
                this.name = "Yellow Shulker Box";
                this.block = Block.get(BlockID.YELLOW_SHULKER_BOX);
                return;
            case 5:
                this.name = "Lime Shulker Box";
                this.block = Block.get(BlockID.LIME_SHULKER_BOX);
                return;
            case 6:
                this.name = "Pink Shulker Box";
                this.block = Block.get(BlockID.PINK_SHULKER_BOX);
                return;
            case 7:
                this.name = "Gray Shulker Box";
                this.block = Block.get(BlockID.GRAY_SHULKER_BOX);
                return;
            case 8:
                this.name = "Light Gray Shulker Box";
                this.block = Block.get(BlockID.LIGHT_GRAY_SHULKER_BOX);
                return;
            case 9:
                this.name = "Cyan Shulker Box";
                this.block = Block.get(BlockID.CYAN_SHULKER_BOX);
                return;
            case 10:
                this.name = "Purple Shulker Box";
                this.block = Block.get(BlockID.PURPLE_SHULKER_BOX);
                return;
            case 11:
                this.name = "Blue Shulker Box";
                this.block = Block.get(BlockID.BLUE_SHULKER_BOX);
                return;
            case 12:
                this.name = "Brown Shulker Box";
                this.block = Block.get(BlockID.BROWN_SHULKER_BOX);
                return;
            case 13:
                this.name = "Green Shulker Box";
                this.block = Block.get(BlockID.GREEN_SHULKER_BOX);
                return;
            case 14:
                this.name = "Red Shulker Box";
                this.block = Block.get(BlockID.RED_SHULKER_BOX);
                return;
            case 15:
                this.name = "Black Shulker Box";
                this.block = Block.get(BlockID.BLACK_SHULKER_BOX);
        }
    }
}