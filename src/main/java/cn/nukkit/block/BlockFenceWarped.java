package cn.nukkit.block;

/**
 * BlockFenceWarped.java was made by using BlockFence.java and BlockFenceNetherBrick.java
 */
/**
 * @author xtypr
 * @since 2015/12/7
 */


public class BlockFenceWarped extends BlockFenceBase {


    public BlockFenceWarped() {
        this(0);
    }


    public BlockFenceWarped(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Warped Fence";
    }

    @Override
    public int getId() {
        return WARPED_FENCE;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }

}
