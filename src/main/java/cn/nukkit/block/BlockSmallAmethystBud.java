package cn.nukkit.block;


public class BlockSmallAmethystBud extends BlockAmethystBud {
    @Override
    protected String getNamePrefix() {
        return "Small";
    }

    @Override
    public int getId() {
        return SMALL_AMETHYST_BUD;
    }

    @Override
    public int getLightLevel() {
        return 1;
    }

}
