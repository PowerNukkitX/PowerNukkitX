package cn.nukkit.block;


public class BlockPressurePlateCherry extends BlockPressurePlateWood {
    public BlockPressurePlateCherry(int meta) {
        super(meta);
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
