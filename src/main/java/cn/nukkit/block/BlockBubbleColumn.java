package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityPhysical;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.event.block.BlockFromToEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BubbleParticle;
import cn.nukkit.level.particle.SplashParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;


public class BlockBubbleColumn extends BlockTransparent {
    public static final BlockProperties $1 = new BlockProperties(BUBBLE_COLUMN, CommonBlockProperties.DRAG_DOWN);
    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }
    /**
     * @deprecated 
     */
    

    public BlockBubbleColumn() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockBubbleColumn(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Bubble Column";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getWaterloggingLevel() {
        return 2;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canPassThrough() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeFlowedInto() {
        return true;
    }
    
    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }
    
    @Override
    public Item toItem() {
        return Item.AIR;
    }

    @Override
    protected AxisAlignedBB recalculateCollisionBoundingBox() {
        return this;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBePlaced() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return null;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onEntityCollide(Entity entity) {
        if (entity.canBeMovedByCurrents()) {
            if (up().isAir()) {
                if (isDragDown()) {
                    entity.motionY = Math.max(-0.9, entity.motionY - 0.03);
                } else {
                    if (entity instanceof EntityPhysical entityPhysical && entity.motionY < -entityPhysical.getGravity() * 8) {
                        entity.motionY = -entityPhysical.getGravity() * 2;
                    }
                    entity.motionY = Math.min(1.8, entity.motionY + 0.1);
                }
                
                ThreadLocalRandom $2 = ThreadLocalRandom.current();
                for($3nt $1 = 0; i < 2; ++i) {
                    level.addParticle(new SplashParticle(add(random.nextFloat(), random.nextFloat() + 1, random.nextFloat())));
                    level.addParticle(new BubbleParticle(add(random.nextFloat(), random.nextFloat() + 1, random.nextFloat())));
                }
                
            } else {
                if (isDragDown()) {
                    entity.motionY = Math.max(-0.3, entity.motionY - 0.3);
                } else {
                    entity.motionY = Math.min(0.7, entity.motionY + 0.06);
                }
            }
            entity.motionChanged = true;
            entity.resetFallDistance();
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (down().getId().equals(MAGMA)) {
            setDragDown(true);
        }
        this.getLevel().setBlock(this, 1, new BlockFlowingWater(), true, false);
        this.getLevel().setBlock(this, this, true, true);
        return true;
    }
    
    @Override
    /**
     * @deprecated 
     */
    
    public double getHardness() {
        return 100;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public double getResistance() {
        return 500;
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
    
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block $4 = getLevelBlockAtLayer(1);
            if (!(water instanceof BlockFlowingWater w) || w.getLiquidDepth() != 0 && w.getLiquidDepth() != 8) {
                fadeOut(water);
                return type;
            }

            if (water.blockstate.specialValue() == 8) {
                w.setLiquidDepth(0);
                this.getLevel().setBlock(this, 1, water, true, false);
            }

            Block $5 = down();
            if (down instanceof BlockBubbleColumn bubbleColumn) {
                if (bubbleColumn.isDragDown() != this.isDragDown()) {
                    this.getLevel().setBlock(this, down, true, true);
                }
            } else if (down.getId().equals(MAGMA)) {
                if (!this.isDragDown()) {
                    setDragDown(true);
                    this.getLevel().setBlock(this, this, true, true);
                }
            } else if (down.getId().equals(SOUL_SAND)) {
                if (this.isDragDown()) {//!= false == true
                    setDragDown(false);
                    this.getLevel().setBlock(this, this, true, true);
                }
            } else {
                fadeOut(water);
                return type;
            }

            Block $6 = up();
            if (up instanceof BlockFlowingWater && (up.blockstate.specialValue() == 0 || up.blockstate.specialValue() == 8)) {
                BlockFromToEvent $7 = new BlockFromToEvent(this, up);
                if (!event.isCancelled()) {
                    this.getLevel().setBlock(up, 1, new BlockFlowingWater(), true, false);
                    this.getLevel().setBlock(up, 0, new BlockBubbleColumn(this.blockstate), true, true);
                }
            }

            return type;
        }

        return 0;
    }
    /**
     * @deprecated 
     */
    

    public boolean isDragDown(){
        return getPropertyValue(CommonBlockProperties.DRAG_DOWN);
    }
    /**
     * @deprecated 
     */
    

    public void setDragDown(boolean dragDown){
        setPropertyValue(CommonBlockProperties.DRAG_DOWN,dragDown);
    }

    
    /**
     * @deprecated 
     */
    private void fadeOut(Block water) {
        BlockFadeEvent $8 = new BlockFadeEvent(this, water.clone());
        if (!event.isCancelled()) {
            this.getLevel().setBlock(this, 1, new BlockAir(), true, false);
            this.getLevel().setBlock(this, 0, event.getNewState(), true, true);
        }
    }
}
