package org.powernukkitx.block;

import org.powernukkitx.Player;
import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.MathHelper;

import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 2015/11/22
 */
public class BlockDaylightDetectorInverted extends BlockDaylightDetector {

    public static final BlockProperties PROPERTIES = new BlockProperties(DAYLIGHT_DETECTOR_INVERTED, CommonBlockProperties.REDSTONE_SIGNAL);

    public BlockDaylightDetectorInverted() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockDaylightDetectorInverted(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Daylight Detector Inverted";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.DAYLIGHT_DETECTOR), 0);
    }

    @Override
    public void updatePower() {
        int i;

        Level level = this.getLevel();

        if (level.getDimension() == Level.DIMENSION_OVERWORLD) {
            int skylight = getEffectiveSkyLightSignalAround(level, getFloorX(), getFloorY(), getFloorZ());
            i = skylight - level.calculateSkylightSubtracted(1.0F);

            float f = level.getCelestialAngle(1.0F) * 6.2831855F;

            if (i > 0) {
                float f1 = f < (float) Math.PI ? 0.0F : ((float) Math.PI * 2F);
                f = f + (f1 - f) * 0.2F;
                i = Math.round((float) i * MathHelper.cos(f));
            }

            i = MathHelper.clamp(i, 0, 15) > 0 ? 0 : 15;
        } else {
            i = 0;
        }

        int current = level.getBlockStateAt(getFloorX(), getFloorY(), getFloorZ())
                          .getPropertyValue(CommonBlockProperties.REDSTONE_SIGNAL);

        if (i != current) {
            this.setPropertyValue(CommonBlockProperties.REDSTONE_SIGNAL, i);
            BlockState blockState = this.getBlockState();
            level.setBlockStateAt(getFloorX(), getFloorY(), getFloorZ(), blockState);
            updateAroundRedstone();
        }
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (isNotActivate(player)) return false;

        BlockDaylightDetector block = new BlockDaylightDetector();
        getLevel().setBlock(this, block, true, true);

        if (this.level.getServer().getSettings().gameplaySettings().enableRedstone()) {
            block.updatePower();
        }

        return true;
    }


    @Override
    public boolean isInverted() {
        return true;
    }
}