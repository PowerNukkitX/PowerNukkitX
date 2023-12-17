package cn.nukkit.block;


public class BlockPressurePlateBlackstonePolished extends BlockPressurePlateStone {


    public BlockPressurePlateBlackstonePolished() {
        // Does nothing
    }


    public BlockPressurePlateBlackstonePolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_PRESSURE_PLATE;
    }

    @Override
    public String getName() {
        return "Polished Blackstone Pressure Plate";
    }

}
