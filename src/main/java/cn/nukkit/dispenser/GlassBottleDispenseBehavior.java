package cn.nukkit.dispenser;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockWater;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

@PowerNukkitXOnly
@Since("1.19.60-r1")
public class GlassBottleDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);
        if (target instanceof BlockWater && target.getDamage() == 0)
            return Item.get(Item.POTION);
        return super.dispense(block, face, item);
    }
}
