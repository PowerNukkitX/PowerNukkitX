package cn.nukkit.dispenser;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.MinecraftItemID;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public class BucketDispenseBehavior extends DefaultDispenseBehavior {

    @PowerNukkitOnly
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        if (!(item instanceof ItemBucket)) {
            return super.dispense(block, face, item);
        }
        
        ItemBucket bucket = (ItemBucket) item; 
        Block target = block.getSide(face);
        
        if (!bucket.isEmpty()) {
            if (target.canBeFlowedInto() || target.getId() == BlockID.NETHER_PORTAL) {
                Block replace = bucket.getTargetBlock();

                if (target.getId() == BlockID.NETHER_PORTAL) {
                    target.onBreak(null);
                    target.level.getVibrationManager().callVibrationEvent(new VibrationEvent(target.add(0.5, 0.5, 0.5), VibrationType.BLOCK_DESTROY));
                }

                if (replace instanceof BlockLiquid || replace.getId() == BlockID.POWDER_SNOW) {
                    block.level.setBlock(target, replace);
                    if (replace instanceof BlockLiquid) {
                        target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PLACE));
                    } else {
                        target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(target.add(0.5, 0.5, 0.5), VibrationType.BLOCK_PLACE));
                    }
                    return MinecraftItemID.BUCKET.get(1, bucket.getCompoundTag());
                }
            }
        } else if (target instanceof BlockWater && target.getDamage() == 0) {
            target.level.setBlock(target, Block.get(BlockID.AIR));
            target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PICKUP));
            return MinecraftItemID.WATER_BUCKET.get(1, bucket.getCompoundTag());
        } else if (target instanceof BlockLava && target.getDamage() == 0) {
            target.level.setBlock(target, Block.get(BlockID.AIR));
            target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PICKUP));
            return MinecraftItemID.LAVA_BUCKET.get(1, bucket.getCompoundTag());
        } else if (target instanceof BlockPowderSnow) {
            target.level.setBlock(target, Block.get(BlockID.AIR));
            target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PICKUP));
            return MinecraftItemID.POWDER_SNOW_BUCKET.get(1, bucket.getCompoundTag());
        }
        //todo: 炼药锅行为

        return super.dispense(block, face, item);
    }
}
