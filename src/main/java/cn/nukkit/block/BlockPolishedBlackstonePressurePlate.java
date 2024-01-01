package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstonePressurePlate extends BlockStonePressurePlate {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_blackstone_pressure_plate", CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    public @NotNull BlockProperties getProperties() {
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