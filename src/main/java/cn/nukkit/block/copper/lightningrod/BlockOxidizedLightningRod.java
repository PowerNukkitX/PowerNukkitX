package cn.nukkit.block.copper.lightningrod;

import cn.nukkit.block.BlockProperties;
import cn.nukkit.block.BlockState;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.OxidizationLevel;
import org.jetbrains.annotations.NotNull;

/**
 * @author keksdev
 * @since 1.21.110
 */
public class BlockOxidizedLightningRod extends BlockLightningRod {
    public static final BlockProperties PROPERTIES = new BlockProperties(OXIDIZED_LIGHTNING_ROD, CommonBlockProperties.FACING_DIRECTION, CommonBlockProperties.POWERED_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockOxidizedLightningRod() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockOxidizedLightningRod(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Oxidized Lightning Rod";
    }

    @Override
    public @NotNull OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}
