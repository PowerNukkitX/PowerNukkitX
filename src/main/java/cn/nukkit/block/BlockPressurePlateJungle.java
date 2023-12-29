package cn.nukkit.block;


public class BlockPressurePlateJungle extends BlockPressurePlateWood {


    public BlockPressurePlateJungle() {
        this(0);
    }


    public BlockPressurePlateJungle(BlockState blockstate) {
        super(blockstate);
    }
    
    @Override
    public int getId() {
        return JUNGLE_PRESSURE_PLATE;
    }
    
    @Override
    public String getName() {
        return "Jungle Pressure Plate";
    }
}
