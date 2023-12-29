package cn.nukkit.block;


public class BlockStairsCrimson extends BlockStairsWood {


    public BlockStairsCrimson() {
        this(0);
    }


    public BlockStairsCrimson(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return CRIMSON_STAIRS;
    }

    @Override
    public String getName() {
        return "Crimson Wood Stairs";
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
