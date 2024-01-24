package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;


public class BlockCherryLog extends BlockLog {
    public static final BlockProperties PROPERTIES = new BlockProperties(CHERRY_LOG, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCherryLog() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockCherryLog(BlockState blockState) {
        super(blockState);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 5;
    }

    @Override
    public String getName() {
        return "Cherry log";
    }

    @Override
    public BlockState getStrippedState() {
        return BlockStrippedCherryLog.PROPERTIES.getDefaultState();
    }
}

