package cn.nukkit.block;


public class BlockBlackstonePolished extends BlockBlackstone {


    public BlockBlackstonePolished() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Polished Blackstone";
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }
}
