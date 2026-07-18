package org.powernukkitx.block;

import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;

import static org.powernukkitx.block.property.CommonBlockProperties.CORAL_DIRECTION;

public abstract class BlockCoralWallFan extends BlockCoralFan {

    public BlockCoralWallFan(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return super.getName() + " Wall Fan";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            return type;
        } else {
            return super.onUpdate(type);
        }
    }

    @Override
    public BlockFace getBlockFace() {
        int face = getPropertyValue(CORAL_DIRECTION);
        return switch (face) {
            case 0 -> BlockFace.WEST;
            case 1 -> BlockFace.EAST;
            case 2 -> BlockFace.NORTH;
            default -> BlockFace.SOUTH;
        };
    }

    @Override
    public BlockFace getRootsFace() {
        return getBlockFace().getOpposite();
    }

    @Override
    public Item toItem() {
        return new ItemBlock(isDead() ? getDeadCoralFan() : this);
    }
}