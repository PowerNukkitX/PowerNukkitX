package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityBoat;
import org.powernukkitx.event.block.BlockFadeEvent;
import org.powernukkitx.event.block.BlockFromToEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.level.particle.BubbleParticle;
import org.powernukkitx.level.particle.WaterSplashParticle;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

public class BlockBubbleColumn extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(BUBBLE_COLUMN, CommonBlockProperties.DRAG_DOWN);
    public static final BlockDefinition DEFINITION = TRANSPARENT.toBuilder()
            .hardness(100)
            .resistance(500)
            .canPassThrough(true)
            .canBeReplaced(true)
            .build();

    private static final double DOWNWARD_MAX_MOTION = -0.3;
    private static final double DOWNWARD_MAX_EXIT_MOTION = -0.9;
    private static final double DOWNWARD_ACCELERATION = 0.03;
    private static final double UPWARD_MIN_MOTION = 0.7;
    private static final double UPWARD_MAX_EXIT_MOTION = 1.8;
    private static final double UPWARD_ACCELERATION = 0.08;
    private static final double UPWARD_EXIT_ACCELERATION = 0.1;

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockBubbleColumn() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockBubbleColumn(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Bubble Column";
    }

    @Override
    public int getWaterloggingLevel() {
        return 2;
    }

    
    @Override
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
    public boolean isBreakable(@NotNull Vector3 vector, int layer, @Nullable BlockFace face, @Nullable Item item, @Nullable Player player) {
        return false;
    }

    @Override
    public boolean canBePlaced() {
        return false;
    }

    
    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
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
    public double getPassableBlockFrictionFactor() {
        final Block water = getTickCachedLevelBlockAtLayer(1);
        return water instanceof BlockLiquid ? water.getPassableBlockFrictionFactor() : super.getPassableBlockFrictionFactor();
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!entity.canBeMovedByCurrents()) return;

        final boolean surface = up().isAir();

        if (surface) {
            spawnColumnParticles();
        }

        if (entity instanceof final EntityBoat boat) {
            boat.onBubbleColumn(isDragDown());
            if (!boat.isDraggedUnder()) return;
        }

        if (entity instanceof Player) return;

        entity.inBubbleColumn = true;

        final double motionY = Math.max(entity.motionY, DOWNWARD_MAX_MOTION);

        if (surface) {
            if (isDragDown()) {
                entity.motionY = Math.max(DOWNWARD_MAX_EXIT_MOTION, motionY - DOWNWARD_ACCELERATION);
            } else {
                entity.motionY = Math.min(UPWARD_MAX_EXIT_MOTION, motionY + UPWARD_EXIT_ACCELERATION);
            }
        } else {
            if (isDragDown()) {
                entity.motionY = Math.max(DOWNWARD_MAX_MOTION, motionY - DOWNWARD_ACCELERATION);
            } else {
                entity.motionY = Math.min(UPWARD_MIN_MOTION, motionY + UPWARD_ACCELERATION);
            }
        }

        entity.motionChanged = true;
        entity.resetFallDistance();
    }

    private void spawnColumnParticles() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 2; ++i) {
            level.addParticle(new WaterSplashParticle(add(random.nextFloat(), random.nextFloat() + 1, random.nextFloat())));
            level.addParticle(new BubbleParticle(add(random.nextFloat(), random.nextFloat() + 1, random.nextFloat())));
        }
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (down().getId().equals(MAGMA)) {
            setDragDown(true);
        }
        this.getLevel().setBlock(this, 1, new BlockFlowingWater(), true, false);
        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block water = getLevelBlockAtLayer(1);
            if (!(water instanceof BlockFlowingWater w) || w.getLiquidDepth() != 0 && w.getLiquidDepth() != 8) {
                fadeOut(water);
                return type;
            }

            if (water.blockstate.specialValue() == 8) {
                w.setLiquidDepth(0);
                this.getLevel().setBlock(this, 1, water, true, false);
            }

            Block down = down();
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

            Block up = up();
            if (up instanceof BlockFlowingWater && (up.blockstate.specialValue() == 0 || up.blockstate.specialValue() == 8)) {
                BlockFromToEvent event = new BlockFromToEvent(this, up);
                if (!event.isCancelled()) {
                    this.getLevel().setBlock(up, 1, new BlockFlowingWater(), true, false);
                    this.getLevel().setBlock(up, 0, new BlockBubbleColumn(this.blockstate), true, true);
                }
            }

            return type;
        }

        return 0;
    }

    public boolean isDragDown(){
        return getPropertyValue(CommonBlockProperties.DRAG_DOWN);
    }

    public void setDragDown(boolean dragDown){
        setPropertyValue(CommonBlockProperties.DRAG_DOWN,dragDown);
    }

    private void fadeOut(Block water) {
        BlockFadeEvent event = new BlockFadeEvent(this, water.clone());
        if (!event.isCancelled()) {
            this.getLevel().setBlock(this, 1, new BlockAir(), true, false);
            this.getLevel().setBlock(this, 0, event.getNewState(), true, true);
        }
    }
}
