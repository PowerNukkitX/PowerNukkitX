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
    }

    public void internalAdjust() {
        switch (getDamage()) {
            case 0->{
                this.name = "White Shulker Box";
                setBlockUnsafe(Block.get(BlockID.WHITE_SHULKER_BOX));
            }
            case 1->{
                this.name = "Orange Shulker Box";
                setBlockUnsafe(Block.get(BlockID.ORANGE_SHULKER_BOX));
            }
            case 2->{
                this.name = "Magenta Shulker Box";
                setBlockUnsafe(Block.get(BlockID.MAGENTA_SHULKER_BOX));
            }
            case 3->{
                this.name = "Light Blue Shulker Box";
                setBlockUnsafe(Block.get(BlockID.LIGHT_BLUE_SHULKER_BOX));
            }
            case 4->{
                this.name = "Yellow Shulker Box";
                setBlockUnsafe(Block.get(BlockID.YELLOW_SHULKER_BOX));
            }
            case 5->{
                this.name = "Lime Shulker Box";
                setBlockUnsafe(Block.get(BlockID.LIME_SHULKER_BOX));
            }
            case 6->{
                this.name = "Pink Shulker Box";
                setBlockUnsafe(Block.get(BlockID.PINK_SHULKER_BOX));
            }
            case 7->{
                this.name = "Gray Shulker Box";
                setBlockUnsafe(Block.get(BlockID.GRAY_SHULKER_BOX));
            }
            case 8->{
                this.name = "Light Gray Shulker Box";
                setBlockUnsafe(Block.get(BlockID.LIGHT_GRAY_SHULKER_BOX));
            }
            case 9->{
                this.name = "Cyan Shulker Box";
                setBlockUnsafe(Block.get(BlockID.CYAN_SHULKER_BOX));
            }
            case 10->{
                this.name = "Purple Shulker Box";
                setBlockUnsafe(Block.get(BlockID.PURPLE_SHULKER_BOX));
            }
            case 11->{
                this.name = "Blue Shulker Box";
                setBlockUnsafe(Block.get(BlockID.BLUE_SHULKER_BOX));
            }
            case 12->{
                this.name = "Brown Shulker Box";
                setBlockUnsafe(Block.get(BlockID.BROWN_SHULKER_BOX));
            }
            case 13->{
                this.name = "Green Shulker Box";
                setBlockUnsafe(Block.get(BlockID.GREEN_SHULKER_BOX));
            }
            case 14->{
                this.name = "Red Shulker Box";
                setBlockUnsafe(Block.get(BlockID.RED_SHULKER_BOX));
            }
            case 15->{
                this.name = "Black Shulker Box";
                setBlockUnsafe(Block.get(BlockID.BLACK_SHULKER_BOX));
            }
        }
        this.meta = 0;
    }
}