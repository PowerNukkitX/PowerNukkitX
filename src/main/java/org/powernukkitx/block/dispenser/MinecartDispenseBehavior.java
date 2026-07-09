package org.powernukkitx.block.dispenser;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockRail;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;


public class MinecartDispenseBehavior extends DefaultDispenseBehavior {

    private final String entityType;

    public MinecartDispenseBehavior(String entity) {
        this.entityType = entity;
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);
        if (target instanceof BlockRail) {
            target.x += 0.5;
            target.y += 0.125;
            target.z += 0.5;
        } else return super.dispense(block, face, item);
        Entity minecart = Entity.createEntity(this.getEntityType(), target);
        if (minecart != null)
            minecart.spawnToAll();
        return null;
    }

    protected String getEntityType() {
        return this.entityType;
    }
}
