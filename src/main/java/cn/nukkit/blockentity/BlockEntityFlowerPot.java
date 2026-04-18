package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;
import org.cloudburstmc.nbt.NbtType;

/**
 * @author Snake1999
 * @since 2016/2/4
 */
public class BlockEntityFlowerPot extends BlockEntitySpawnable {
    public BlockEntityFlowerPot(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    public boolean isBlockEntityValid() {
        String blockId = getBlock().getId();
        return blockId == Block.FLOWER_POT;
    }

    @Override
    public NbtMap getSpawnCompound() {
        NbtMapBuilder tag = super.getSpawnCompound().toBuilder()
                .putBoolean("isMovable", this.isMovable());
        if (namedTag.containsKey("PlantBlock", NbtType.COMPOUND))
            tag.putCompound("PlantBlock", namedTag.getCompound("PlantBlock"));
        return tag.build();
    }
}
