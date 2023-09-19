package cn.nukkit.dispenser;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.impl.BlockDispenser;
import cn.nukkit.block.impl.BlockRail;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

@PowerNukkitXOnly
@Since("1.19.60-r1")
public class MinecartDispenseBehavior extends DefaultDispenseBehavior {

    private final String entityType;

    public MinecartDispenseBehavior(String entity) {
        this.entityType = entity;
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);
        if (target instanceof BlockRail) {
            target.setX(target.x() + 0.5);
            target.setY(target.y() + 0.125);
            target.setZ(target.z() + 0.5);
        } else return super.dispense(block, face, item);
        Entity minecart = Entity.createEntity(this.getEntityType(), target);
        if (minecart != null) minecart.spawnToAll();
        return null;
    }

    protected String getEntityType() {
        return this.entityType;
    }
}
