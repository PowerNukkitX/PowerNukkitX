package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.ItemHelper;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;


public class BlockEntityDecoratedPot extends BlockEntitySpawnable {
    public BlockEntityDecoratedPot(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.DECORATED_POT;
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!namedTag.containsKey("Item")) {
            this.namedTag = namedTag.toBuilder().putCompound("Item", ItemHelper.write(new ItemBlock(Block.get(BlockID.AIR)), null)).build();
        }
    }

    @Override
    public NbtMap getSpawnCompound() {
        return super.getSpawnCompound().toBuilder()
                .putList("sherds", NbtType.STRING, namedTag.getList("sherds", NbtType.STRING))
                .build();
    }

    public Item getItem() {
        return ItemHelper.read(this.namedTag.getCompound("Item"));
    }

    public void setItem(Item item) {
        this.setItem(item, true);
    }

    public void setItem(Item item, boolean setChanged) {
        this.namedTag = this.namedTag.toBuilder().putCompound("Item", ItemHelper.write(item, null)).build();
        if (setChanged) {
            this.setDirty();
        } else this.level.updateComparatorOutputLevel(this);
    }

    @Override
    public void close() {
        //Those also drop when broken in creative mode
        level.dropItem(this.add(HALF), this.getItem());
        super.close();
    }
}
