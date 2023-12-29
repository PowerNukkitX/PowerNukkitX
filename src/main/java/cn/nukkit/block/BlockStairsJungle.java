package cn.nukkit.block;

/**
 * @author xtypr
 * @since 2015/11/25
 */
public class BlockStairsJungle extends BlockStairsWood {

    public BlockStairsJungle() {
        this(0);
    }

    public BlockStairsJungle(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return JUNGLE_STAIRS;
    }

    @Override
    public String getName() {
        return "Jungle Wood Stairs";
    }

}
