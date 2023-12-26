package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstonePressurePlate extends Block {
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
}