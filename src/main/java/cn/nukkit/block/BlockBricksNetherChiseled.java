package cn.nukkit.block;

/**
 * @author joserobjr
 */


public class BlockBricksNetherChiseled extends BlockNetherBrick {


    public BlockBricksNetherChiseled() {
        // Does nothing
    }

    @Override
    public int getId() {
        return CHISELED_NETHER_BRICKS;
    }

    @Override
    public String getName() {
        return "Chiseled Nether Bricks";
    }
}
