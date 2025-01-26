package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockOxidizedCopperTrapdoor extends BlockCopperTrapdoor {
    public static final BlockProperties PROPERTIES = new BlockProperties(OXIDIZED_COPPER_TRAPDOOR, CommonBlockProperties.DIRECTION, CommonBlockProperties.OPEN_BIT, CommonBlockProperties.UPSIDE_DOWN_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedCopperTrapdoor() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedCopperTrapdoor(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Oxidized Copper Trapdoor";
    }
}