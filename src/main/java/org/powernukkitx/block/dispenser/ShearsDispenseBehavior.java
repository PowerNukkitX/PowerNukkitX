package org.powernukkitx.block.dispenser;

import org.powernukkitx.entity.EntityShearable;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;


public class ShearsDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        item = item.clone();
        var target = block.getSide(face);
        var bb = new SimpleAxisAlignedBB(
                0, 0, 0,
                1, 1, 1)
                .offset(target.x, target.y, target.z);
        for (var entity : block.level.getCollidingEntities(bb)) {
            if (!(entity instanceof EntityShearable shearable)) { continue; }
            if (!shearable.shear()) { continue; }
            item.useOn(entity);
            return item.getDamage() >= item.getMaxDurability() ? null : item;
        }
        return item;
    }
}
