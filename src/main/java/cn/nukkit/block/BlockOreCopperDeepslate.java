package cn.nukkit.block;

/**
 * @author LoboMetalurgico
 * @since 11/06/2021
 */


public class BlockOreCopperDeepslate extends BlockOreCopper {


    public BlockOreCopperDeepslate() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Deepslate Copper Ore";
    }

    @Override
    public int getId() {
        return DEEPSLATE_COPPER_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

}
