package org.powernukkitx.block.dispenser;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.EntityLiving;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemSpawnEgg;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;


public class SpawnEggDispenseBehavior extends DefaultDispenseBehavior {


    public SpawnEggDispenseBehavior() {
        super();
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 pos = block.getSide(face).add(0.5, 0.7, 0.5);

        Entity entity = Entity.createEntity(((ItemSpawnEgg)item).getEntityNetworkId(), block.level.getChunk(pos.getChunkX(), pos.getChunkZ()),
                Entity.getDefaultNBT(pos));

        this.success = entity != null;

        if (this.success) {
            if (item.hasCustomName() && entity instanceof EntityLiving) {
                entity.setNameTag(item.getCustomName());
            }

            entity.spawnToAll();

            block.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, pos.clone(), VibrationType.ENTITY_PLACE));
            return null;
        }

        return super.dispense(block, face, item);
    }
}
