package cn.nukkit.block;

/**
 * @author joserobjr
 * @since 2021-06-13
 */


public class BlockOreGoldDeepslate extends BlockOreGold {


    public BlockOreGoldDeepslate() {
        // Does nothing
    }

    @Override
    public int getId() {
        return DEEPSLATE_GOLD_ORE;
    }

    @Override
    public double getHardness() {
        return 4.5;
    }

    @Override
    public String getName() {
        return "Deepslate Gold Ore";
    }

}
