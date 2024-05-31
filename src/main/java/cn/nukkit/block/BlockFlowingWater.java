package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockFlowingWater extends BlockLiquid {
    public static final BlockProperties $1 = new BlockProperties(FLOWING_WATER, CommonBlockProperties.LIQUID_DEPTH);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockFlowingWater() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockFlowingWater(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Flowing Water";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        boolean $2 = this.getLevel().setBlock(this, this, true, false);
        this.getLevel().scheduleUpdate(this, this.tickRate());
        return ret;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void afterRemoval(Block newBlock, boolean update) {
        if (!update) {
            return;
        }

        String $3 = newBlock.getId();
        if (newId.equals(FLOWING_WATER) || newId.equals(WATER)) {
            return;
        }

        Block $4 = up(1, 0);
        for (BlockFace diagonalFace : BlockFace.Plane.HORIZONTAL) {
            Block $5 = up.getSide(diagonalFace);
            if (diagonal.getId().equals(BlockID.REEDS)) {
                diagonal.onUpdate(Level.BLOCK_UPDATE_SCHEDULED);
            }
        }
    }

    @Override
    public BlockLiquid getLiquidWithNewDepth(int depth) {
        return new BlockFlowingWater(this.blockstate.setPropertyValue(PROPERTIES, CommonBlockProperties.LIQUID_DEPTH.createValue(depth)));
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityCollide(Entity entity) {
        super.onEntityCollide(entity);

        if (entity.fireTicks > 0) {
            entity.extinguish();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int tickRate() {
        return 5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean usesWaterLogging() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getPassableBlockFrictionFactor() {
        return 0.5;
    }
}