package cn.nukkit.dispenser;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityChestBoat;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemChestBoat;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;


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
