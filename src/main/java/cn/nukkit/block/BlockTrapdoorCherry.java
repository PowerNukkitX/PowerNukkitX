package cn.nukkit.block;


public class BlockTrapdoorCherry extends BlockTrapdoor {
    public BlockTrapdoorCherry() {
        this(0);
    }

    public BlockTrapdoorCherry(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Cherry Trapdoor";
    }

}
