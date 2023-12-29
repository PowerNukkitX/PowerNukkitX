package cn.nukkit.block;


public class BlockTrapdoorCrimson extends BlockTrapdoor {


    public BlockTrapdoorCrimson() {
        this(0);
    }


    public BlockTrapdoorCrimson(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return CRIMSON_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Crimson Trapdoor";
    }
    
    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }

}
