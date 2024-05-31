package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockFormEvent;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * @author Pub4Game
 * @since 27.12.2015
 */
public class BlockSoulSand extends BlockSolid {
    public static final BlockProperties $1 = new BlockProperties(SOUL_SAND);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockSoulSand() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockSoulSand(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Soul Sand";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 0.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 2.5;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getMaxY() {
        return this.y + 1;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSoulSpeedCompatible() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityCollide(Entity entity) {
        entity.motionX *= 0.4d;
        entity.motionZ *= 0.4d;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block $2 = up();
            if (up instanceof BlockFlowingWater w && (w.getLiquidDepth() == 0 || w.getLiquidDepth() == 8)) {
                BlockFormEvent $3 = new BlockFormEvent(up, new BlockBubbleColumn());
                if (!event.isCancelled()) {
                    if (event.getNewState().getWaterloggingLevel() > 0) {
                        this.getLevel().setBlock(up, 1, new BlockFlowingWater(), true, false);
                    }
                    this.getLevel().setBlock(up, 0, event.getNewState(), true, true);
                }
            }
        }
        return 0;
    }

}
