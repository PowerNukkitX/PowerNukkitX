package cn.nukkit.block;


public class BlockStairsBrickBlackstonePolished extends BlockStairsBlackstonePolished {


    public BlockStairsBrickBlackstonePolished() {
        this(0);
    }


    public BlockStairsBrickBlackstonePolished(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_BRICK_STAIRS;
    }

    @Override
    public String getName() {
        return "Polished Blackstone Brick Stairs";
    }

    @Override
    public double getHardness() {
        return 1.5;
    }
}
