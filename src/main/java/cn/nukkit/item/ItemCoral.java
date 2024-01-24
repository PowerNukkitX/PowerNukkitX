package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemCoral extends Item {
    public ItemCoral() {
        super(CORAL);
    }

    public ItemCoral(Integer meta) {
        this(meta, 1);
    }

    public ItemCoral(Integer meta, int count) {
        super(CONCRETE, meta, count);
        adjustName();
        adjustBlock();
    }

    private void adjustBlock() {
        switch (getDamage()) {
            case 0:
                this.block = Block.get(BlockID.TUBE_CORAL);
                return;
            case 1:
                this.block = Block.get(BlockID.BRAIN_CORAL);
                return;
            case 2:
                this.block = Block.get(BlockID.BUBBLE_CORAL);
                return;
            case 3:
                this.block = Block.get(BlockID.FIRE_CORAL);
                return;
            case 4:
                this.block = Block.get(BlockID.HORN_CORAL);
                return;
            case 8:
                this.block = Block.get(BlockID.DEAD_TUBE_CORAL);
                return;
            case 9:
                this.block = Block.get(BlockID.DEAD_BRAIN_CORAL);
                return;
            case 10:
                this.block = Block.get(BlockID.DEAD_BUBBLE_CORAL);
                return;
            case 11:
                this.block = Block.get(BlockID.DEAD_FIRE_CORAL);
                return;
            case 12:
                this.block = Block.get(BlockID.DEAD_HORN_CORAL);
                return;
            default:
                this.name = "Coral";
        }
    }

    private void adjustName() {
        switch (getDamage()) {
            case 0:
                this.name = "Tube Coral";
                return;
            case 1:
                this.name = "Brain Coral";
                return;
            case 2:
                this.name = "Bubble Coral";
                return;
            case 3:
                this.name = "Fire Coral";
                return;
            case 4:
                this.name = "Horn Coral";
                return;
            case 8:
                this.name = "Dead Tube Coral";
                return;
            case 9:
                this.name = "Dead Brain Coral";
                return;
            case 10:
                this.name = "Dead Bubble Coral";
                return;
            case 11:
                this.name = "Dead Fire Coral";
                return;
            case 12:
                this.name = "Dead Horn Coral";
                return;
            default:
                this.name = "Coral";
        }
    }
}