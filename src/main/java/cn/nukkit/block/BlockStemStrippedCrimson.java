package cn.nukkit.block;


public class BlockStemStrippedCrimson extends BlockStemStripped {


    public BlockStemStrippedCrimson() {
        this(0);
    }


    public BlockStemStrippedCrimson(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return STRIPPED_CRIMSON_STEM;
    }
    
    @Override
    public String getName() {
        return "Stripped Crimson Stem";
    }

}
