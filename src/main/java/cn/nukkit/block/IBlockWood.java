package cn.nukkit.block;

/**
 * 语义接口，实现了此接口的方块都是木头方块，可以使得自然生成的树叶不凋零。<br>
 * Semantic interface, blocks that implement this interface are wood blocks,
 * which can make naturally generated leaves not decay.
 */
public interface IBlockWood {
    BlockState getStrippedState();
}
