package org.powernukkitx.block.dispenser;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockFlowingWater;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityBoat;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;

public class BoatDispenseBehavior extends DefaultDispenseBehavior {

    public BoatDispenseBehavior() {
        super();
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Vector3 pos = block.getSide(face).multiply(1.125);

        Block target = block.getSide(face);

        if (target instanceof BlockFlowingWater) {
            pos.y += 1;
        } else if (!target.isAir() || !(target.down() instanceof BlockFlowingWater)) {
            return super.dispense(block, face, item);
        }

        spawnBoatEntity(block.level, target.getLocation().add(face.getXOffset() * 0.75, face.getYOffset() * 0.75, face.getZOffset() * 0.75).setYaw(face.getHorizontalAngle()), item);

        return null;
    }

    protected void spawnBoatEntity(Level level, Vector3 pos, Item item) {
        EntityBoat boat = new EntityBoat(level.getChunk(pos.getChunkX(), pos.getChunkZ()),
                Entity.getDefaultNBT(pos)
                        .putInt("Variant", item.getDamage())
        );
        boat.spawnToAll();
    }

}
