package org.powernukkitx.block.dispenser;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityTnt;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;


public class TNTDispenseBehavior extends DefaultDispenseBehavior {


    public TNTDispenseBehavior() {
        super();
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 pos = block.getSide(face).add(0.5, 0, 0.5);

        EntityTnt tnt = new EntityTnt(block.level.getChunk(pos.getChunkX(), pos.getChunkZ()),
                Entity.getDefaultNBT(pos));
        tnt.spawnToAll();

        return null;
    }

}
