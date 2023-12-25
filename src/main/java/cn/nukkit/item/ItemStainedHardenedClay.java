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
        adjust();
    }

    private void adjust() {
        switch (getAux()) {
            case 0:
                this.name = "White Terracotta";
                this.block = Block.get(BlockID.WHITE_TERRACOTTA);
                return;
            case 1:
                this.name = "Orange Terracotta";
                this.block = Block.get(BlockID.ORANGE_TERRACOTTA);
                return;
            case 2:
                this.name = "Magenta Terracotta";
                this.block = Block.get(BlockID.MAGENTA_TERRACOTTA);
                return;
            case 3:
                this.name = "Light Blue Terracotta";
                this.block = Block.get(BlockID.LIGHT_BLUE_TERRACOTTA);
                return;
            case 4:
                this.name = "Yellow Terracotta";
                this.block = Block.get(BlockID.YELLOW_TERRACOTTA);
                return;
            case 5:
                this.name = "Lime Terracotta";
                this.block = Block.get(BlockID.LIME_TERRACOTTA);
                return;
            case 6:
                this.name = "Pink Terracotta";
                this.block = Block.get(BlockID.PINK_TERRACOTTA);
                return;
            case 7:
                this.name = "Gray Terracotta";
                this.block = Block.get(BlockID.GRAY_TERRACOTTA);
                return;
            case 8:
                this.name = "Light Gray Terracotta";
                this.block = Block.get(BlockID.LIGHT_GRAY_TERRACOTTA);
                return;
            case 9:
                this.name = "Cyan Terracotta";
                this.block = Block.get(BlockID.CYAN_TERRACOTTA);
                return;
            case 10:
                this.name = "Purple Terracotta";
                this.block = Block.get(BlockID.PURPLE_TERRACOTTA);
                return;
            case 11:
                this.name = "Blue Terracotta";
                this.block = Block.get(BlockID.BLUE_TERRACOTTA);
                return;
            case 12:
                this.name = "Brown Terracotta";
                this.block = Block.get(BlockID.BROWN_TERRACOTTA);
                return;
            case 13:
                this.name = "Green Terracotta";
                this.block = Block.get(BlockID.GREEN_TERRACOTTA);
                return;
            case 14:
                this.name = "Red Terracotta";
                this.block = Block.get(BlockID.RED_TERRACOTTA);
                return;
            case 15:
                this.name = "Black Terracotta";
                this.block = Block.get(BlockID.BLACK_TERRACOTTA);
        }
    }
}