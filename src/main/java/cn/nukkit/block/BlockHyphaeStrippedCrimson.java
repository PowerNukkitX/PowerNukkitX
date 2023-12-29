package cn.nukkit.block;


public class BlockHyphaeStrippedCrimson extends BlockStemStripped {


    public BlockHyphaeStrippedCrimson() {
        this(0);
    }


    public BlockHyphaeStrippedCrimson(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return STRIPPED_CRIMSON_HYPHAE;
    }
    
    @Override
    public String getName() {
        return "Crimson Stripped Hyphae";
    }

    @Override
    public double getHardness() {
        return 0.4;
    }

}
