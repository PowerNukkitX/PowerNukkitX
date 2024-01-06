package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstonePressurePlate extends BlockStonePressurePlate {
    public static final BlockProperties PROPERTIES = new BlockProperties(POLISHED_BLACKSTONE_PRESSURE_PLATE, CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstonePressurePlate() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstonePressurePlate(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Polished Blackstone Pressure Plate";
    }
}