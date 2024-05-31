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

    public static final BlockProperties $1 = new BlockProperties(DAYLIGHT_DETECTOR_INVERTED, CommonBlockProperties.REDSTONE_SIGNAL);
    /**
     * @deprecated 
     */
    

    public BlockDaylightDetectorInverted() {
        this(PROPERTIES.getDefaultState());
    }
    /**
     * @deprecated 
     */
    

    public BlockDaylightDetectorInverted(BlockState blockState) {
        super(blockState);
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public String getName() {
        return "Daylight Detector Inverted";
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.DAYLIGHT_DETECTOR), 0);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if(isNotActivate(player)) return false;
        BlockDaylightDetector $2 = new BlockDaylightDetector();
        getLevel().setBlock(this, block, true, true);
        if (this.level.getServer().getSettings().levelSettings().enableRedstone()) {
            block.updatePower();
        }
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isInverted() {
        return true;
    }
}
