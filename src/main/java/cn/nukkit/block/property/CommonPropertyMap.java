package cn.nukkit.block.property;

import cn.nukkit.math.BlockFace;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;

public final class CommonPropertyMap {
    private CommonPropertyMap(){}
    public static final ImmutableBiMap<BlockFace, Integer> EWSN_DIRECTION = ImmutableBiMap
            .<BlockFace, Integer> builderWithExpectedSize(4)
            .put(BlockFace.EAST, 0)
            .put(BlockFace.WEST, 1)
            .put(BlockFace.SOUTH, 2)
            .put(BlockFace.NORTH, 3)
            .build();
}
