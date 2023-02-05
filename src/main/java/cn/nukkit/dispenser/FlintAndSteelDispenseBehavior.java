package cn.nukkit.dispenser;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;

@PowerNukkitOnly
public class FlintAndSteelDispenseBehavior extends DefaultDispenseBehavior {

    @PowerNukkitOnly
    public FlintAndSteelDispenseBehavior() {
        super();
    }

    @Override
    @PowerNukkitDifference(info = "Reduce flint and steel usage instead of clearing.", since = "1.4.0.0-PN")
    public @PowerNukkitOnly Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);
        item = item.clone();

        var down = target.down();
        if (down.getId() == BlockID.OBSIDIAN) {
            if (down.level.getDimension() != Level.DIMENSION_THE_END) {
                if (down.level.createPortal(down)) {
                    item.useOn(target);
                    return item.getDamage() >= item.getMaxDurability() ? null : item;
                }
            }
        }

        if (target.getId() == BlockID.AIR) {
            block.level.addSound(block, Sound.RANDOM_CLICK, 1.0f, 1.0f);
            block.level.setBlock(target, Block.get(BlockID.FIRE));
            item.useOn(target);
            return item.getDamage() >= item.getMaxDurability() ? null : item;
        } else if (target.getId() == BlockID.TNT) {
            block.level.addSound(block, Sound.RANDOM_CLICK, 1.0f, 1.0f);
            target.onActivate(item);
            item.useOn(target);
            return item.getDamage() >= item.getMaxDurability() ? null : item;
        } else {
            this.success = false;
        }

        block.level.addSound(block, Sound.RANDOM_CLICK, 1.0f, 1.2f);
        return item;
    }
}
