package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockRail;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;


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
