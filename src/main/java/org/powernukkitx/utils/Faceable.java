package org.powernukkitx.utils;

import org.powernukkitx.math.BlockFace;

public interface Faceable {

    BlockFace getBlockFace();


    default void setBlockFace(BlockFace face) {
        // Does nothing by default
    }
}
