package cn.nukkit.block;

/**
 * @author xtypr
 * @since 2015/11/25
 */
public class BlockStairsBirch extends BlockStairsWood {

    public BlockStairsBirch() {
        this(0);
    }

    public BlockStairsBirch(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return BIRCH_STAIRS;
    }

    @Override
    public String getName() {
        return "Birch Wood Stairs";
    }

}
