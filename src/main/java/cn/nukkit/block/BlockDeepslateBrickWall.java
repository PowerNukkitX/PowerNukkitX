package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockDeepslateBrickWall extends Block {
    public static final BlockProperties PROPERTIES = new BlockProperties("minecraft:deepslate_brick_wall", CommonBlockProperties.WALL_CONNECTION_TYPE_EAST, CommonBlockProperties.WALL_CONNECTION_TYPE_NORTH, CommonBlockProperties.WALL_CONNECTION_TYPE_SOUTH, CommonBlockProperties.WALL_CONNECTION_TYPE_WEST, CommonBlockProperties.WALL_POST_BIT);

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDeepslateBrickWall() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDeepslateBrickWall(BlockState blockstate) {
        super(blockstate);
    }
}