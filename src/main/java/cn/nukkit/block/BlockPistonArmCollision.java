package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPistonArmCollision extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:piston_arm_collision", CommonBlockProperties.FACING_DIRECTION);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPistonArmCollision() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPistonArmCollision(BlockState blockstate) {
        super(blockstate);
    }
}