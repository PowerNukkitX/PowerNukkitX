package cn.nukkit.block;


public class BlockPressurePlateCherry extends BlockPressurePlateWood {
    public BlockPressurePlateCherry(BlockState blockstate) {
        super(blockstate);
    }

    public BlockPressurePlateCherry() {
        this(0);
    }

    @Override
    public String getName() {
        return "Mangrove Pressure Plate";
    }

    @Override
    public int getId() {
        return CHERRY_PRESSURE_PLATE;
    }
}
