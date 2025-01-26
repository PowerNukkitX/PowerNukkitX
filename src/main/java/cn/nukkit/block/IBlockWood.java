package cn.nukkit.block;

/**
 * Semantic interface, blocks that implement this interface are wood blocks,
 * which can make naturally generated leaves not decay.
 */
public interface IBlockWood {
    BlockState getStrippedState();
}
