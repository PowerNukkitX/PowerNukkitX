package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemConcretePowder extends Item {
    public ItemConcretePowder() {
        this(0, 1);
    }

    public ItemConcretePowder(Integer meta) {
        this(meta, 1);
    }

    public ItemConcretePowder(Integer meta, int count) {
        super(CONCRETE_POWDER, meta, count);
        adjustName();
        adjustBlock();
    }

    private void adjustBlock() {
        switch (getDamage()) {
            case 0:
                block = Block.get(BlockID.WHITE_CONCRETE_POWDER);
                return;
            case 1:
                block = Block.get(BlockID.ORANGE_CONCRETE_POWDER);
                return;
            case 2:
                block = Block.get(BlockID.MAGENTA_CONCRETE_POWDER);
                return;
            case 3:
                block = Block.get(BlockID.LIGHT_BLUE_CONCRETE_POWDER);
                return;
            case 4:
                block = Block.get(BlockID.YELLOW_CONCRETE_POWDER);
                return;
            case 5:
                block = Block.get(BlockID.LIME_CONCRETE_POWDER);
                return;
            case 6:
                block = Block.get(BlockID.PINK_CONCRETE_POWDER);
                return;
            case 7:
                block = Block.get(BlockID.GRAY_CONCRETE_POWDER);
                return;
            case 8:
                block = Block.get(BlockID.LIGHT_GRAY_CONCRETE_POWDER);
                return;
            case 9:
                block = Block.get(BlockID.CYAN_CONCRETE_POWDER);
                return;
            case 10:
                block = Block.get(BlockID.PURPLE_CONCRETE_POWDER);
                return;
            case 11:
                block = Block.get(BlockID.BLUE_CONCRETE_POWDER);
                return;
            case 12:
                block = Block.get(BlockID.BROWN_CONCRETE_POWDER);
                return;
            case 13:
                block = Block.get(BlockID.GREEN_CONCRETE_POWDER);
                return;
            case 14:
                block = Block.get(BlockID.RED_CONCRETE_POWDER);
                return;
            case 15:
                block = Block.get(BlockID.BLACK_CONCRETE_POWDER);
        }
    }

    private void adjustName() {
        switch (getDamage()) {
            case 0:
                name = "White Concrete Powder";
                return;
            case 1:
                name = "Orange Concrete Powder";
                return;
            case 2:
                name = "Magenta Concrete Powder";
                return;
            case 3:
                name = "Light Blue Concrete Powder";
                return;
            case 4:
                name = "Yellow Concrete Powder";
                return;
            case 5:
                name = "Lime Concrete Powder";
                return;
            case 6:
                name = "Pink Concrete Powder";
                return;
            case 7:
                name = "Gray Concrete Powder";
                return;
            case 8:
                name = "Light Gray Concrete Powder";
                return;
            case 9:
                name = "Cyan Concrete Powder";
                return;
            case 10:
                name = "Purple Concrete Powder";
                return;
            case 11:
                name = "Blue Concrete Powder";
                return;
            case 12:
                name = "Brown Concrete Powder";
                return;
            case 13:
                name = "Green Concrete Powder";
                return;
            case 14:
                name = "Red Concrete Powder";
                return;
            case 15:
                name = "Black Concrete Powder";
        }
    }
}