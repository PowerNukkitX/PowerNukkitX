package cn.nukkit.block;


public class BlockTrapdoorMangrove extends BlockTrapdoor {

    public BlockTrapdoorMangrove() {
        this(0);
    }


    public BlockTrapdoorMangrove(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return MANGROVE_TRAPDOOR;
    }

    @Override
    public String getName() {
        return "Mangrove Trapdoor";
    }

}
