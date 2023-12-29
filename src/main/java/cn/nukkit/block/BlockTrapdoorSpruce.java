package cn.nukkit.block;


public class BlockTrapdoorSpruce extends BlockTrapdoor {

    public BlockTrapdoorSpruce() {
        this(0);
    }


    public BlockTrapdoorSpruce(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return SPRUCE_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Spruce Trapdoor";
    }

}
