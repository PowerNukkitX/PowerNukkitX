package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
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
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;
        BlockDaylightDetector block = new BlockDaylightDetector();
        getLevel().setBlock(this, block, true, true);
        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            block.updatePower();
        }
        return true;
    }

    @Override
    public boolean isInverted() {
        return true;
    }
}
