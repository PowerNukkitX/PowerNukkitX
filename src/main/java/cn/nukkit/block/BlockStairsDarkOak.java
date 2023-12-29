package cn.nukkit.block;

/**
 * @author xtypr
 * @since 2015/11/25
 */
public class BlockStairsDarkOak extends BlockStairsWood {

    public BlockStairsDarkOak() {
        this(0);
    }

    public BlockStairsDarkOak(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return DARK_OAK_WOOD_STAIRS;
    }

    @Override
    public String getName() {
        return "Dark Oak Wood Stairs";
    }

}
