package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * @author Pub4Game
 * @since 03.01.2016
 */

public class BlockBarrier extends BlockSolid {


    public BlockBarrier() {
    }

    @Override
    public int getId() {
        return BARRIER;
    }

    @Override
    public String getName() {
        return "Barrier";
    }


    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public double getHardness() {
        return -1;
    }

    @Override
    public double getResistance() {
        return 18000000;
    }

    @Override
    public boolean isBreakable(Item item) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }
}
