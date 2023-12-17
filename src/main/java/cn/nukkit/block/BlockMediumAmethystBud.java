package cn.nukkit.block;


public class BlockMediumAmethystBud extends BlockAmethystBud {
    @Override
    protected String getNamePrefix() {
        return "Medium";
    }

    @Override
    public int getId() {
        return MEDIUM_AMETHYST_BUD;
    }

    @Override
    public int getLightLevel() {
        return 2;
    }
}
