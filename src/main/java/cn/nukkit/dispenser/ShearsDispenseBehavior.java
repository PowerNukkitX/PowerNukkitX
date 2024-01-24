package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.EntityShearable;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;


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
