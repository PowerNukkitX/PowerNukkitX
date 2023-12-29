package cn.nukkit.block;


public class BlockTrapdoorDarkOak extends BlockTrapdoor {

    public BlockTrapdoorDarkOak() {
        this(0);
    }


    public BlockTrapdoorDarkOak(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return DARK_OAK_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Dark Oak Trapdoor";
    }

}
