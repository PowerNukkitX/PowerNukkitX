package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

public class BlockOxidizedCutCopperStairs extends BlockCutCopperStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:oxidized_cut_copper_stairs", CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedCutCopperStairs() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedCutCopperStairs(BlockState blockstate) {
        super(blockstate);
    }

    @NotNull
    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}