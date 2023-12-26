package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockStickyPistonArmCollision extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:sticky_piston_arm_collision", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStickyPistonArmCollision() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStickyPistonArmCollision(BlockState blockstate) {
        super(blockstate);
    }
}