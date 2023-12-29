package cn.nukkit.block;

import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.blockstate.BlockState;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.blockproperty.CommonBlockProperties.PILLAR_AXIS;


public class BlockWoodCherry extends BlockLog {
    public static final String STRIPPED_BIT = "stripped_bit";

    public static final BlockProperties PROPERTIES = new BlockProperties(
            new BooleanBlockProperty(STRIPPED_BIT, true),
            PILLAR_AXIS
    );

    public BlockWoodCherry() {
        super(0);
    }

    public BlockWoodCherry(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return CHERRY_WOOD;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return (isStripped() ? "Stripped " : "") + "Cherry Wood";
    }


    public boolean isStripped() {
        return getBooleanValue(STRIPPED_BIT);
    }


    public void setStripped(boolean stripped) {
        setBooleanValue(STRIPPED_BIT, stripped);
    }


    @Override
    public BlockState getStrippedState() {
        return BlockState.of(STRIPPED_CHERRY_WOOD).withProperty(PILLAR_AXIS, getPillarAxis());
    }
}