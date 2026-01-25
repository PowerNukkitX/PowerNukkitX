package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;


public class FlintAndSteelDispenseBehavior extends DefaultDispenseBehavior {

    public FlintAndSteelDispenseBehavior() {
        super();
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);
        item = item.clone();

        var down = target.down();
        if (down.getId().equals(BlockID.OBSIDIAN)
                && down.level.getDimension() != Level.DIMENSION_THE_END
                && down.level.createPortal(down)) {
            item.useOn(target);
            return item.getDamage() >= item.getMaxDurability() ? null : item;
        }

        return switch (target.getId()) {
            case BlockID.AIR -> {
                block.level.addSound(block, Sound.RANDOM_CLICK, 1.0f, 1.0f);
                block.level.setBlock(target, Block.get(BlockID.FIRE));
                item.useOn(target);
                yield item.getDamage() >= item.getMaxDurability() ? null : item;
            }
            case BlockID.TNT -> {
                block.level.addSound(block, Sound.RANDOM_CLICK, 1.0f, 1.0f);
                target.onActivate(item, null, face, 0, 0, 0);
                item.useOn(target);
                yield item.getDamage() >= item.getMaxDurability() ? null : item;
            }
            default -> {
                this.success = false;
                block.level.addSound(block, Sound.RANDOM_CLICK, 1.0f, 1.2f);
                yield item;
            }
        };
    }
}
