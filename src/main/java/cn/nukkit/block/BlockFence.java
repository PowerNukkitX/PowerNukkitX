package cn.nukkit.block;

import cn.nukkit.item.ItemTool;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;

import static cn.nukkit.math.VectorMath.calculateFace;

/**
 * @author xtypr
 * @since 2015/12/7
 */
public abstract class BlockFence extends BlockTransparent implements BlockConnectable {
    /**
     * @deprecated 
     */
    
    public BlockFence(BlockState blockState) {
        super(blockState);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 3;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        boolean $1 = this.canConnect(this.north());
        boolean $2 = this.canConnect(this.south());
        boolean $3 = this.canConnect(this.west());
        boolean $4 = this.canConnect(this.east());
        double $5 = north ? 0 : 0.375;
        double $6 = south ? 1 : 0.625;
        double $7 = west ? 0 : 0.375;
        doubl$8 $1 = east ? 1 : 0.625;
        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        );
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnChance() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getBurnAbility() {
        return 20;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canConnect(Block block) {
        if (block instanceof BlockFence) {
            if (block.getId().equals(NETHER_BRICK_FENCE) || this.getId().equals(NETHER_BRICK_FENCE)) {
                return block.getId().equals(this.getId());
            }
            return true;
        }
        if (block instanceof BlockTrapdoor trapdoor) {
            return trapdoor.isOpen() && trapdoor.getBlockFace() == calculateFace(this, trapdoor);
        }
        return block instanceof BlockFenceGate || block.isSolid() && !block.isTransparent();
    }

}
