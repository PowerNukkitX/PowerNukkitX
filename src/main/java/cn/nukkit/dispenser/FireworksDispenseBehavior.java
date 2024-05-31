package cn.nukkit.dispenser;

import cn.nukkit.block.BlockDispenser;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFireworksRocket;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;


public class FireworksDispenseBehavior extends DefaultDispenseBehavior {
    /**
     * @deprecated 
     */
    


    public FireworksDispenseBehavior() {
        super();
    }

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        BlockFace $1 = face.getOpposite();
        Vector3 $2 = block.getSide(face).add(0.5 + opposite.getXOffset()*0.2, 0.5 + opposite.getYOffset()*0.2, 0.5 + opposite.getZOffset()*0.2);

        CompoundTag $3 = Entity.getDefaultNBT(pos);
        nbt.putCompound("FireworkItem", NBTIO.putItemHelper(item));
        EntityFireworksRocket $4 = new EntityFireworksRocket(block.level.getChunk(pos.getChunkX(), pos.getChunkZ()), nbt);
        firework.spawnToAll();

        return null;
    }
}
