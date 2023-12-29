package cn.nukkit.block;


public class BlockPressurePlateCrimson extends BlockPressurePlateWood {


    public BlockPressurePlateCrimson() {
        this(0);
    }


    public BlockPressurePlateCrimson(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return CRIMSON_PRESSURE_PLATE;
    }
    
    @Override
    public String getName() {
        return "Crimson Pressure Plate";
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
