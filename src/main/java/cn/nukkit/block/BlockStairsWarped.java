package cn.nukkit.block;


public class BlockStairsWarped extends BlockStairsWood {


    public BlockStairsWarped() {
        this(0);
    }


    public BlockStairsWarped(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return WARPED_STAIRS;
    }

    @Override
    public String getName() {
        return "Warped Wood Stairs";
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
