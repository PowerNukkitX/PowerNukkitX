package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemStainedGlassPane extends Item {
    public ItemStainedGlassPane() {
        this(0, 1);
    }

    public ItemStainedGlassPane(Integer meta) {
        this(meta, 1);
    }

    public ItemStainedGlassPane(Integer meta, int count) {
        super(STAINED_GLASS_PANE, meta, count);
    }

    public void internalAdjust() {
        switch (getDamage()) {
            case 0:
                this.name = "White Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.WHITE_STAINED_GLASS_PANE));
            case 1:
                this.name = "Orange Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.ORANGE_STAINED_GLASS_PANE));
            case 2:
                this.name = "Magenta Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.MAGENTA_STAINED_GLASS_PANE));
            case 3:
                this.name = "Light Blue Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.LIGHT_BLUE_STAINED_GLASS_PANE));
            case 4:
                this.name = "Yellow Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.YELLOW_STAINED_GLASS_PANE));
            case 5:
                this.name = "Lime Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.LIME_STAINED_GLASS_PANE));
            case 6:
                this.name = "Pink Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.PINK_STAINED_GLASS_PANE));
            case 7:
                this.name = "Gray Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.GRAY_STAINED_GLASS_PANE));
            case 8:
                this.name = "Light Gray Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.LIGHT_GRAY_STAINED_GLASS_PANE));
            case 9:
                this.name = "Cyan Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.CYAN_STAINED_GLASS_PANE));
            case 10:
                this.name = "Purple Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.PURPLE_STAINED_GLASS_PANE));
            case 11:
                this.name = "Blue Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.BLUE_STAINED_GLASS_PANE));
            case 12:
                this.name = "Brown Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.BROWN_STAINED_GLASS_PANE));
            case 13:
                this.name = "Green Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.GREEN_STAINED_GLASS_PANE));
            case 14:
                this.name = "Red Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.RED_STAINED_GLASS_PANE));
            case 15:
                this.name = "Black Stained Glass Pane";
                setBlockUnsafe(Block.get(BlockID.BLACK_STAINED_GLASS_PANE));
        }
        this.meta = 0;
    }
}