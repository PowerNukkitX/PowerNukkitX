package cn.nukkit.utils;

import cn.nukkit.math.BlockFace;

public interface Faceable {

    BlockFace getBlockFace();


    default void setBlockFace(BlockFace face) {
        // Does nothing by default
    }
}
