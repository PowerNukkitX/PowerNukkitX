package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.CORAL_DIRECTION;
import static cn.nukkit.block.property.CommonBlockProperties.CORAL_HANG_TYPE_BIT;
import static cn.nukkit.block.property.CommonBlockProperties.DEAD_BIT;

public class BlockCoralFanHang extends BlockCoralFan {
    public static final BlockProperties PROPERTIES = new BlockProperties(CORAL_FAN_HANG, CORAL_DIRECTION, CORAL_HANG_TYPE_BIT, DEAD_BIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCoralFanHang() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCoralFanHang(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        String name = super.getName();
        name = name.substring(0, name.length() - 4);
        if (isDead()) {
            return "Dead " + name + " Wall Fan";
        } else {
            return name + " Wall Fan";
        }
    }

    @Override
    public boolean isDead() {
        return getPropertyValue(DEAD_BIT);
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
    public Block getDeadCoralFan() {
        if (getPropertyValue(CORAL_HANG_TYPE_BIT)) {
            return new BlockDeadTubeCoralFan();
        } else {
            return new BlockDeadBrainCoralFan();
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
