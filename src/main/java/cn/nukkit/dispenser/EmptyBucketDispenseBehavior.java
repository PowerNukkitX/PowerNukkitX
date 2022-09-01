package cn.nukkit.dispenser;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
@Deprecated
@DeprecationDetails(reason = "implement it in BucketDispenseBehavior.java", since = "1.19.21-r3")
public class EmptyBucketDispenseBehavior extends DefaultDispenseBehavior {

    @PowerNukkitOnly
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target instanceof BlockWater && target.getDamage() == 0) {
            target.level.setBlock(target, new BlockAir());
            target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PICKUP));
            return MinecraftItemID.WATER_BUCKET.get(1, item.getCompoundTag());
        } else if (target instanceof BlockLava && target.getDamage() == 0) {
            target.level.setBlock(target, new BlockAir());
            target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PICKUP));
            return MinecraftItemID.LAVA_BUCKET.get(1, item.getCompoundTag());
        }

        super.dispense(block, face, item);
        return null;
    }
}
