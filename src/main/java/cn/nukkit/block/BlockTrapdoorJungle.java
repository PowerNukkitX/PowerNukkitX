package cn.nukkit.block;


public class BlockTrapdoorJungle extends BlockTrapdoor {

    public BlockTrapdoorJungle() {
        this(0);
    }


    public BlockTrapdoorJungle(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return JUNGLE_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Jungle Trapdoor";
    }

}
