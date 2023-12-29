package cn.nukkit.block;


public class BlockPressurePlateSpruce extends BlockPressurePlateWood {


    public BlockPressurePlateSpruce() {
        this(0);
    }


    public BlockPressurePlateSpruce(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return SPRUCE_PRESSURE_PLATE;
    }
    
    @Override
    public String getName() {
        return "Spruce Pressure Plate";
    }
}
