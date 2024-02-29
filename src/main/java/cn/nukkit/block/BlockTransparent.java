package cn.nukkit.block;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockTransparent extends Block {
    public BlockTransparent(BlockState blockState) {
        super(blockState);
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

}
