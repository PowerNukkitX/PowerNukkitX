package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockstate.BlockState;
import org.jetbrains.annotations.NotNull;


public class BlockCherryLog extends BlockLog {

    public BlockCherryLog() {
        this(0);
    }

    public BlockCherryLog(int meta) {
        super(meta);
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
    public int getId() {
        return CHERRY_LOG;
    }

    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PILLAR_PROPERTIES;
    }

    @Override
    public BlockState getStrippedState() {
        return getBlockState().withBlockId(STRIPPED_CHERRY_LOG);
    }
}

