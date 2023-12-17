package cn.nukkit.block;

/**
 * @author joserobjr
 * @since 2021-06-13
 */


public class BlockOreLapisDeepslate extends BlockOreLapis {


    public BlockOreLapisDeepslate() {
        // Does nothing
    }

    @Override
    public int getId() {
        return DEEPSLATE_LAPIS_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public String getName() {
        return "Deepslate Lapis Lazuli Ore";
    }

}
