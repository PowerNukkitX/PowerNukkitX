package cn.nukkit.dispenser;

import cn.nukkit.block.*;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBucket;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemLavaBucket;
import cn.nukkit.item.ItemPowderSnowBucket;
import cn.nukkit.level.Sound;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;

/**
 * @author CreeperFace
 */
public class BucketDispenseBehavior extends DefaultDispenseBehavior {
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        if (!(item instanceof ItemBucket bucket)) {
            return super.dispense(block, face, item);
        }

        Block target = block.getSide(face);

        if (!bucket.isEmpty()) {
            if (target.canBeFlowedInto() || target.getId() == BlockID.PORTAL) {
                Block replace = bucket.getTargetBlock();
                var fishEntityId = bucket.getFishEntityId();
                if (bucket instanceof ItemLavaBucket)
                    target.level.addSound(block, Sound.BUCKET_EMPTY_LAVA);
                else if(bucket instanceof ItemPowderSnowBucket)
                    target.level.addSound(block, Sound.BUCKET_EMPTY_POWDER_SNOW);
                else if (fishEntityId != null)
                    target.level.addSound(block, Sound.BUCKET_EMPTY_FISH);
                else
                    target.level.addSound(block, Sound.BUCKET_EMPTY_WATER);

                if (target.getId().equals(BlockID.PORTAL)) {
                    target.onBreak(null);
                    target.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, target.add(0.5, 0.5, 0.5), VibrationType.BLOCK_DESTROY));
                }

                if (replace instanceof BlockLiquid || replace.getId() == BlockID.POWDER_SNOW) {
                    block.level.setBlock(target, replace);
                    if (fishEntityId != null)
                        bucket.spawnFishEntity(target.add(0.5, 0.5, 0.5));
                    if (replace instanceof BlockLiquid)
                        target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(this, target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PLACE));
                    else
                        target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(this, target.add(0.5, 0.5, 0.5), VibrationType.BLOCK_PLACE));
                    return Item.get(ItemID.BUCKET, 0, 1, bucket.getCompoundTag());
                }

            }
        } else {
            if (target instanceof BlockFlowingWater flowingWater && flowingWater.getLiquidDepth() == 0) {
                target.level.setBlock(target, Block.get(BlockID.AIR));
                target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(this, target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PICKUP));
                target.level.addSound(block, Sound.BUCKET_FILL_WATER);
                return Item.get(ItemID.WATER_BUCKET, 0, 1, bucket.getCompoundTag());
            } else if (target instanceof BlockFlowingLava lava && lava.getLiquidDepth() == 0) {
                target.level.setBlock(target, Block.get(BlockID.AIR));
                target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(this, target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PICKUP));
                target.level.addSound(block, Sound.BUCKET_FILL_LAVA);
                return Item.get(ItemID.LAVA_BUCKET, 0, 1, bucket.getCompoundTag());
            } else if (target instanceof BlockPowderSnow) {
                target.level.setBlock(target, Block.get(BlockID.AIR));
                target.level.addSound(block, Sound.BUCKET_FILL_POWDER_SNOW);
                target.getLevel().getVibrationManager().callVibrationEvent(new VibrationEvent(this, target.add(0.5, 0.5, 0.5), VibrationType.FLUID_PICKUP));
                return Item.get(ItemID.POWDER_SNOW_BUCKET, 0, 1, bucket.getCompoundTag());
            }
        }

        return super.dispense(block, face, item);
    }
}
