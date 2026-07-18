package org.powernukkitx.block.dispenser;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityChestBoat;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemChestBoat;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.Vector3;


public class ChestBoatDispenseBehavior extends BoatDispenseBehavior{
    @Override
    protected void spawnBoatEntity(Level level, Vector3 pos, Item item) {
        EntityChestBoat boat = new EntityChestBoat(level.getChunk(pos.getChunkX(), pos.getChunkZ()),
                Entity.getDefaultNBT(pos)
                        .putInt("Variant", ((ItemChestBoat) item).getBoatId())
        );
        boat.spawnToAll();
    }
}
