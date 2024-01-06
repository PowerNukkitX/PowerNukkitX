package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemConcrete extends Item {
    public ItemConcrete() {
        this(0, 1);
    }

    public ItemConcrete(Integer meta) {
        this(meta, 1);
    }

    public ItemConcrete(Integer meta, int count) {
        super(CONCRETE, meta, count);
        adjustName();
        adjustBlock();
    }

    private void adjustBlock() {
        switch (getDamage()) {
            case 0:
                block = Block.get(BlockID.WHITE_CONCRETE);
                return;
            case 1:
                block = Block.get(BlockID.ORANGE_CONCRETE);
                return;
            case 2:
                block = Block.get(BlockID.MAGENTA_CONCRETE);
                return;
            case 3:
                block = Block.get(BlockID.LIGHT_BLUE_CONCRETE);
                return;
            case 4:
                block = Block.get(BlockID.YELLOW_CONCRETE);
                return;
            case 5:
                block = Block.get(BlockID.LIME_CONCRETE);
                return;
            case 6:
                block = Block.get(BlockID.PINK_CONCRETE);
                return;
            case 7:
                block = Block.get(BlockID.GRAY_CONCRETE);
                return;
            case 8:
                block = Block.get(BlockID.LIGHT_GRAY_CONCRETE);
                return;
            case 9:
                block = Block.get(BlockID.CYAN_CONCRETE);
                return;
            case 10:
                block = Block.get(BlockID.PURPLE_CONCRETE);
                return;
            case 11:
                block = Block.get(BlockID.BLUE_CONCRETE);
                return;
            case 12:
                block = Block.get(BlockID.BROWN_CONCRETE);
                return;
            case 13:
                block = Block.get(BlockID.GREEN_CONCRETE);
                return;
            case 14:
                block = Block.get(BlockID.RED_CONCRETE);
                return;
            case 15:
                block = Block.get(BlockID.BLACK_CONCRETE);
        }
    }

    private void adjustName() {
        switch (getDamage()) {
            case 0:
                name = "White Concrete";
                return;
            case 1:
                name = "Orange Concrete";
                return;
            case 2:
                name = "Magenta Concrete";
                return;
            case 3:
                name = "Light Blue Concrete";
                return;
            case 4:
                name = "Yellow Concrete";
                return;
            case 5:
                name = "Lime Concrete";
                return;
            case 6:
                name = "Pink Concrete";
                return;
            case 7:
                name = "Gray Concrete";
                return;
            case 8:
                name = "Light Gray Concrete";
                return;
            case 9:
                name = "Cyan Concrete";
                return;
            case 10:
                name = "Purple Concrete";
                return;
            case 11:
                name = "Blue Concrete";
                return;
            case 12:
                name = "Brown Concrete";
                return;
            case 13:
                name = "Green Concrete";
                return;
            case 14:
                name = "Red Concrete";
                return;
            case 15:
                name = "Black Concrete";
        }
    }
}