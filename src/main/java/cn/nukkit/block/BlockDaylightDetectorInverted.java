package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import org.jetbrains.annotations.NotNull;

/**
 * @author CreeperFace
 * @since 2015/11/22
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
public class BlockDaylightDetectorInverted extends BlockDaylightDetector {

    public BlockDaylightDetectorInverted() {}

    @Override
    public int getId() {
        return DAYLIGHT_DETECTOR_INVERTED;
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
    public boolean onActivate(@NotNull Item item, Player player) {
        BlockDaylightDetector block = new BlockDaylightDetector();
        getLevel().setBlock(this, block, true, true);
        if (this.level.getServer().isRedstoneEnabled()) {
            block.updatePower();
        }
        return true;
    }

    @PowerNukkitOnly
    @Override
    public boolean isInverted() {
        return true;
    }

}
