package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityDaylightDetector;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import cn.nukkit.utils.RedstoneComponent;

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
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
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
            int skylight = getEffectiveSkyLightSignalAround(level, getFloorX(), getFloorY(), getFloorZ());
            i = skylight - level.calculateSkylightSubtracted(1.0F);

            float f = level.getCelestialAngle(1.0F) * 6.2831855F;

            if (i > 0) {
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
            BlockState blockState = this.getBlockState();
            level.setBlockStateAt(getFloorX(), getFloorY(), getFloorZ(), blockState);
            updateAroundRedstone();
        }
    }

    public int getEffectiveSkyLightSignalAround(Level level, int x, int y, int z) {
        int skyReduction = level.skyLightSubtracted;

        int bestSignal = level.getBlockSkyLightAt(x, y + 1, z) - skyReduction;
        if (bestSignal >= 15) {
            return 15;
        }

        final int radius = 11;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int dist = Math.abs(dx) + Math.abs(dz);
                if (dist == 0 || dist > radius) continue;

                int cx = x + dx;
                int cz = z + dz;

                int skylight = level.getBlockSkyLightAt(cx, y + 1, cz);
                int signal = skylight - skyReduction - dist;

                if (signal > bestSignal) {
                    bestSignal = signal;
                    if (bestSignal >= 15) return 15;
                }
            }
        }

        return Math.max(0, bestSignal);
    }


    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.625;
    }
}
