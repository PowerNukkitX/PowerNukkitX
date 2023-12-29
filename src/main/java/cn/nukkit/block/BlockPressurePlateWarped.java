package cn.nukkit.block;


public class BlockPressurePlateWarped extends BlockPressurePlateWood {


    public BlockPressurePlateWarped() {
        this(0);
    }


    public BlockPressurePlateWarped(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return WARPED_PRESSURE_PLATE;
    }
    
    @Override
    public String getName() {
        return "Warped Pressure Plate";
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
