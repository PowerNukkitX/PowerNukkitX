package org.powernukkitx.block.dispenser;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.entity.item.EntityFireworksRocket;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.ItemHelper;


public class FireworksDispenseBehavior extends DefaultDispenseBehavior {


    public FireworksDispenseBehavior() {
        super();
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        BlockFace opposite = face.getOpposite();
        Vector3 pos = block.getSide(face).add(0.5 + opposite.getXOffset() * 0.2, 0.5 + opposite.getYOffset() * 0.2, 0.5 + opposite.getZOffset() * 0.2);

        CompoundTag nbt = Entity.getDefaultNBT(pos);
        nbt.putCompound("FireworkItem", ItemHelper.write(item));
        EntityFireworksRocket firework = new EntityFireworksRocket(block.level.getChunk(pos.getChunkX(), pos.getChunkZ()), nbt);
        firework.spawnToAll();

        return null;
    }
}
