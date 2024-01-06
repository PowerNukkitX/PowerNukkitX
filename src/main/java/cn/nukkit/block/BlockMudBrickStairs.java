package cn.nukkit.block;


import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMudBrickStairs extends BlockStairs {
    public static final BlockProperties PROPERTIES = new BlockProperties(MUD_BRICK_STAIRS, CommonBlockProperties.UPSIDE_DOWN_BIT, CommonBlockProperties.WEIRDO_DIRECTION);

    public BlockMudBrickStairs() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockMudBrickStairs(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Mud Bricks Stair";
    }

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
}
