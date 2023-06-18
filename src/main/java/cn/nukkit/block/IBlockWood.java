package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockstate.BlockState;

/**
 * 语义接口，实现了此接口的方块都是木头方块，可以使得自然生成的树叶不凋零。<br>
 * Semantic interface, blocks that implement this interface are wood blocks,
 * which can make naturally generated leaves not decay.
 */
@Since("1.20.0-r2")
@PowerNukkitXOnly
public interface IBlockWood {
    BlockState getStrippedState();
}
