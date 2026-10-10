package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityDaylightDetector;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.MathHelper;
import org.powernukkitx.utils.RedstoneComponent;

import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 2015/11/22
 */
public class BlockDaylightDetector extends BlockTransparent implements RedstoneComponent, BlockEntityHolder<BlockEntityDaylightDetector> {

    public static final BlockProperties PROPERTIES = new BlockProperties(DAYLIGHT_DETECTOR, CommonBlockProperties.REDSTONE_SIGNAL);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockDaylightDetector() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDaylightDetector(BlockState state) {
        super(state);
    }

    @Override
    public String getName() {
        return "Daylight Detector";
    }

    @Override
    @NotNull public String getBlockEntityType() {
        return BlockEntity.DAYLIGHT_DETECTOR;
    }

    @Override
    @NotNull public Class<? extends BlockEntityDaylightDetector> getBlockEntityClass() {
        return BlockEntityDaylightDetector.class;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public int getLightFilter() {
        return 0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockDaylightDetector(), 0);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (!this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            return 0;
        }

        if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) {
            var be = getBlockEntity();
            if (be != null) {
                be.scheduleUpdate();
            }
        }
        return type;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        BlockEntityDaylightDetector detector = BlockEntityHolder.setBlockAndCreateEntity(this);
        if (detector == null) {
            return false;
        }
        if (getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
            if (this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
                updatePower();
            }
        }
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (isNotActivate(player)) return false;

        BlockDaylightDetectorInverted block = new BlockDaylightDetectorInverted();
        getLevel().setBlock(this, block, true, true);

        if (this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            block.updatePower();
        }

        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        if (super.onBreak(item)) {
            if (getLevel().getDimension() == Level.DIMENSION_OVERWORLD) {
                updateAroundRedstone();
            }
            return true;
        }
        return false;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return getLevel().getBlockStateAt(getFloorX(), getFloorY(), getFloorZ()).getPropertyValue(CommonBlockProperties.REDSTONE_SIGNAL);
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    public boolean isInverted() {
        return false;
    }

    public void updatePower() {
        int i;
        Level level = this.getLevel();

        if (level.getDimension() == Level.DIMENSION_OVERWORLD) {
            i = level.getBlockSkyLightAt(getFloorX(), getFloorY(), getFloorZ()) - level.skyLightSubtracted;

            if (this.isInverted()) {
                i = 15 - i;
            } else if (i > 0) {
                float f = level.getCelestialAngle(1.0F) * 6.2831855F;
                float f1 = f < (float) Math.PI ? 0.0F : ((float) Math.PI * 2F);
                f = f + (f1 - f) * 0.2F;
                i = Math.round((float) i * MathHelper.cos(f));
            }

            i = MathHelper.clamp(i, 0, 15);
        } else {
            i = 0;
        }

        int current = level.getBlockStateAt(getFloorX(), getFloorY(), getFloorZ())
                          .getPropertyValue(CommonBlockProperties.REDSTONE_SIGNAL);

        if (i != current) {
            this.setPropertyValue(CommonBlockProperties.REDSTONE_SIGNAL, i);
            level.setBlock(this, this, false, true);
            updateAroundRedstone();
        }
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.375;
    }
}
