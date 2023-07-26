package cn.nukkit.block;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockTransparentMeta extends BlockMeta {

    protected BlockTransparentMeta() {
        this(0);
    }

    protected BlockTransparentMeta(int meta) {
        super(meta);
    }

    @Override
    public boolean isTransparent() {
        return true;
    }
}
