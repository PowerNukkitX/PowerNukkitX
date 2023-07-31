package cn.nukkit.dispenser;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.block.impl.BlockDispenser;
import cn.nukkit.block.impl.BlockUndyedShulkerBox;
import cn.nukkit.item.Item;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;

@PowerNukkitOnly
public class ShulkerBoxDispenseBehavior extends DefaultDispenseBehavior {

    @PowerNukkitOnly
    public ShulkerBoxDispenseBehavior() {
        super();
    }

    @PowerNukkitOnly
    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (!target.canBeReplaced()) {
            success = false;
            return null;
        }

        BlockUndyedShulkerBox shulkerBox =
                (BlockUndyedShulkerBox) item.getBlock().clone();
        shulkerBox.setLevel(block.getLevel());
        shulkerBox.layer = 0;
        shulkerBox.setX(target.x());
        shulkerBox.setY(target.y());
        shulkerBox.setZ(target.z());

        BlockFace shulkerBoxFace = shulkerBox.down().isTransparent() ? face : BlockFace.UP;

        if (success = shulkerBox.place(
                item, target, target.getSide(shulkerBoxFace.getOpposite()), shulkerBoxFace, 0, 0, 0, null)) {
            block.getLevel().updateComparatorOutputLevel(target);
            block.getLevel()
                    .getVibrationManager()
                    .callVibrationEvent(new VibrationEvent(this, target.add(0.5, 0.5, 0.5), VibrationType.BLOCK_PLACE));
        }

        return null;
    }
}
