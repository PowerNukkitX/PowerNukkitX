package cn.nukkit.block;

import cn.nukkit.level.Level;


public abstract class BlockCoralFanDead extends BlockCoralFan {
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

    @Override
    public Block getDeadCoralFan() {
        return this;
    }
}
