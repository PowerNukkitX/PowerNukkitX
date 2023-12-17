package cn.nukkit.block;


public class BlockFenceCherry extends BlockFenceBase {
    public BlockFenceCherry() {
        this(0);
    }

    public BlockFenceCherry(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cherry Fence";
    }

    @Override
    public int getId() {
        return CHERRY_FENCE;
    }
}
