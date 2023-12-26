package cn.nukkit.block;

import cn.nukkit.block.state.BlockProperties;
import cn.nukkit.block.state.BlockState;
import cn.nukkit.block.state.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockPolishedBlackstoneWall extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:polished_blackstone_wall", CommonBlockProperties.WALL_CONNECTION_TYPE_EAST, CommonBlockProperties.WALL_CONNECTION_TYPE_NORTH, CommonBlockProperties.WALL_CONNECTION_TYPE_SOUTH, CommonBlockProperties.WALL_CONNECTION_TYPE_WEST, CommonBlockProperties.WALL_POST_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPolishedBlackstoneWall() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPolishedBlackstoneWall(BlockState blockstate) {
        super(blockstate);
    }
}