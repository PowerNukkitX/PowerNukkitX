package cn.nukkit.block;

import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.CORAL_COLOR;
import static cn.nukkit.block.property.CommonBlockProperties.CORAL_FAN_DIRECTION;


public class BlockCoralFanDead extends BlockCoralFan {
    public static final BlockProperties PROPERTIES = new BlockProperties(CORAL_FAN_DEAD, CORAL_COLOR, CORAL_FAN_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCoralFanDead() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCoralFanDead(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Dead " + super.getName();
    }

    @Override
    public boolean isDead() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!getSide(getRootsFace()).isSolid()) {
                this.getLevel().useBreakOn(this);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            return super.onUpdate(type);
        }
        return 0;
    }
}
