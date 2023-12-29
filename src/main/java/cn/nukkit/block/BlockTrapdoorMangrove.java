package cn.nukkit.block;


public class BlockTrapdoorMangrove extends BlockTrapdoor {

    public BlockTrapdoorMangrove() {
        this(0);
    }


    public BlockTrapdoorMangrove(BlockState blockstate) {
        super(blockstate);
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
