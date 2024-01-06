package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockFlowingWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;

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
