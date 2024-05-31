package cn.nukkit.utils;

import cn.nukkit.math.BlockFace;

public interface Faceable {

    BlockFace getBlockFace();


    default 
    /**
     * @deprecated 
     */
    void setBlockFace(BlockFace face) {
        // Does nothing by default
    }
}
