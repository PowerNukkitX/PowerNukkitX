package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.BlockFace;

public interface Faceable {

    BlockFace getBlockFace();


    default void setBlockFace(BlockFace face) {
        // Does nothing by default
    }
}
