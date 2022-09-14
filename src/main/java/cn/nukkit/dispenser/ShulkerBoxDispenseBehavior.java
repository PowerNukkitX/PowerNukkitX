package cn.nukkit.dispenser;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockUndyedShulkerBox;
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

        BlockUndyedShulkerBox shulkerBox = (BlockUndyedShulkerBox) item.getBlock().clone();
        shulkerBox.level = block.level;
        shulkerBox.layer = 0;
        shulkerBox.x = target.x;
        shulkerBox.y = target.y;
        shulkerBox.z = target.z;

        BlockFace shulkerBoxFace = shulkerBox.down().isTransparent() ? face : BlockFace.UP;
        
        if (success = shulkerBox.place(item, target, target.getSide(shulkerBoxFace.getOpposite()), shulkerBoxFace, 0, 0, 0, null)) {
            block.level.updateComparatorOutputLevel(target);
            block.level.getVibrationManager().callVibrationEvent(new VibrationEvent(this, target.add(0.5, 0.5 , 0.5), VibrationType.BLOCK_PLACE));
        }

        return null;
    }
}
