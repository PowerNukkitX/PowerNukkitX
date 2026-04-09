package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFireworksRocket;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.ItemHelper;
import org.cloudburstmc.nbt.NbtMapBuilder;


public class FireworksDispenseBehavior extends DefaultDispenseBehavior {


    public FireworksDispenseBehavior() {
        super();
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        BlockFace opposite = face.getOpposite();
        Vector3 pos = block.getSide(face).add(0.5 + opposite.getXOffset() * 0.2, 0.5 + opposite.getYOffset() * 0.2, 0.5 + opposite.getZOffset() * 0.2);

        NbtMapBuilder nbt = Entity.getDefaultNBT(pos).toBuilder();
        nbt.putCompound("FireworkItem", ItemHelper.write(item));
        EntityFireworksRocket firework = new EntityFireworksRocket(block.level.getChunk(pos.getChunkX(), pos.getChunkZ()), nbt.build());
        firework.spawnToAll();

        return null;
    }
}
