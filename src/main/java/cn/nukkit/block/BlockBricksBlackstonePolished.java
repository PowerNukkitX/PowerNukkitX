package cn.nukkit.block;


public class BlockBricksBlackstonePolished extends BlockBlackstonePolished {


    public BlockBricksBlackstonePolished() {
        // Does nothing
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_BRICKS;
    }

    @Override
    public String getName() {
        return "Polished Blackstone Bricks";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }
}
