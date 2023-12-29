package cn.nukkit.block;

/**
 * @author xtypr
 * @since 2015/11/25
 */
public class BlockStairsSpruce extends BlockStairsWood {

    public BlockStairsSpruce() {
        this(0);
    }

    public BlockStairsSpruce(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return SPRUCE_STAIRS;
    }

    @Override
    public String getName() {
        return "Spruce Wood Stairs";
    }

}
