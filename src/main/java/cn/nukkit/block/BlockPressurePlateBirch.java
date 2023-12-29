package cn.nukkit.block;


public class BlockPressurePlateBirch extends BlockPressurePlateWood {


    public BlockPressurePlateBirch() {
        this(0);
    }


    public BlockPressurePlateBirch(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return BIRCH_PRESSURE_PLATE;
    }
    
    @Override
    public String getName() {
        return "Birch Pressure Plate";
    }
}
