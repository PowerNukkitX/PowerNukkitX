package cn.nukkit.dispenser;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.EntityShearable;
import cn.nukkit.entity.passive.EntitySheep;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;

@PowerNukkitXOnly
@Since("1.19.60-r1")
public class ShearsDispenseBehavior extends DefaultDispenseBehavior {
    @PowerNukkitOnly
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        item = item.clone();
        var target = block.getSide(face);
        var bb = new SimpleAxisAlignedBB(
                0, 0, 0,
                1, 1, 1)
                .offset(target.x, target.y, target.z);
        for (var entity : block.level.getCollidingEntities(bb)) {
            if (entity instanceof EntityShearable shearable) {
                shearable.shear();
                item.useOn(entity);
                return item.getDamage() >= item.getMaxDurability() ? null : item;
            }
        }
        return item;
    }
}
