package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;

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
            boolean isDark = getIsFullyDarkAround(level, getFloorX(), getFloorY(), getFloorZ());
            i = isDark ? 15 : 0;
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

    public static boolean getIsFullyDarkAround(Level level, int x, int y, int z) {
        int skyReduction = level.skyLightSubtracted;

        int directSky = level.getBlockSkyLightAt(x, y + 1, z);
        int directSignal = directSky - skyReduction;
        if (directSignal >= 5) return false;

        final int radius = 10;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                int dist = Math.abs(dx) + Math.abs(dz);
                if (dist == 0 || dist > radius) continue;

                int cx = x + dx;
                int cz = z + dz;

                int sky = level.getBlockSkyLightAt(cx, y + 1, cz);
                int signal = sky - skyReduction - dist;

                if (signal >= 5) return false;
            }
        }

        return true;
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
