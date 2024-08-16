package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.CORAL_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.CORAL_HANG_TYPE_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.DEAD_BIT;

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