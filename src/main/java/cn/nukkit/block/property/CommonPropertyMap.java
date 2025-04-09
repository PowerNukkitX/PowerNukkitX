package cn.nukkit.block.property;

import cn.nukkit.block.Block;
import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.block.property.enums.Orientation;
import cn.nukkit.math.BlockFace;
import com.google.common.collect.ImmutableBiMap;

import java.util.HashMap;

public final class CommonPropertyMap {
    private CommonPropertyMap() {
    }

    public static final ImmutableBiMap<BlockFace, Integer> EWSN_DIRECTION = ImmutableBiMap
            .<BlockFace, Integer>builderWithExpectedSize(4)
            .put(BlockFace.EAST, 0)
            .put(BlockFace.WEST, 1)
            .put(BlockFace.SOUTH, 2)
            .put(BlockFace.NORTH, 3)
            .build();

    public static final ImmutableBiMap<MinecraftCardinalDirection, BlockFace> CARDINAL_BLOCKFACE = ImmutableBiMap
            .<MinecraftCardinalDirection, BlockFace>builderWithExpectedSize(4)
            .put(MinecraftCardinalDirection.EAST, BlockFace.EAST)
            .put(MinecraftCardinalDirection.WEST, BlockFace.WEST)
            .put(MinecraftCardinalDirection.SOUTH, BlockFace.SOUTH)
            .put(MinecraftCardinalDirection.NORTH, BlockFace.NORTH)
            .build();

    public static final ImmutableBiMap<BlockFace, Orientation> BLOCKFACE_ORIENTATION_SIMPLIFIED = ImmutableBiMap
            .<BlockFace, Orientation>builderWithExpectedSize(4)
            .put(BlockFace.UP, Orientation.UP_EAST)
            .put(BlockFace.DOWN, Orientation.DOWN_EAST)
            .put(BlockFace.NORTH, Orientation.NORTH_UP)
            .put(BlockFace.SOUTH, Orientation.SOUTH_UP)
            .put(BlockFace.WEST, Orientation.WEST_UP)
            .put(BlockFace.EAST, Orientation.EAST_UP)
            .build();

    public static final HashMap<Orientation, BlockFace> ORIENTATION_BLOCKFACE = new HashMap<Orientation, BlockFace>() {{
        put(Orientation.DOWN_EAST, BlockFace.DOWN);
        put(Orientation.DOWN_NORTH, BlockFace.DOWN);
        put(Orientation.DOWN_SOUTH, BlockFace.DOWN);
        put(Orientation.DOWN_EAST, BlockFace.DOWN);
        put(Orientation.UP_EAST, BlockFace.UP);
        put(Orientation.UP_NORTH, BlockFace.UP);
        put(Orientation.UP_SOUTH, BlockFace.UP);
        put(Orientation.UP_WEST, BlockFace.UP);
        put(Orientation.EAST_UP, BlockFace.EAST);
        put(Orientation.WEST_UP, BlockFace.WEST);
        put(Orientation.SOUTH_UP, BlockFace.SOUTH);
        put(Orientation.NORTH_UP, BlockFace.NORTH);
    }};
}
