package cn.nukkit.block.property;

import cn.nukkit.block.property.enums.MinecraftCardinalDirection;
import cn.nukkit.math.BlockFace;
import com.google.common.collect.ImmutableBiMap;

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
}
