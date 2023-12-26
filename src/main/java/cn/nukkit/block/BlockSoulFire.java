package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockSoulFire extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:soul_fire", CommonBlockProperties.AGE_16);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSoulFire() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSoulFire(BlockState blockstate) {
        super(blockstate);
    }
}