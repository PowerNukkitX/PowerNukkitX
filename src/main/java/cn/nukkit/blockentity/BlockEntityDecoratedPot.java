package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.utils.ItemHelper;


public class BlockEntityDecoratedPot extends BlockEntitySpawnable {
    public BlockEntityDecoratedPot(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.DECORATED_POT;
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!nbt.contains("Item")) {
            this.nbt.putCompound("Item", ItemHelper.write(new ItemBlock(Block.get(BlockID.AIR)), null));
        }
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound()
                .putList("sherds", (ListTag<StringTag>) getNbt().getList("sherds", StringTag.class).copy());
    }

    public Item getItem() {
        return ItemHelper.read(this.getNbt().getCompound("Item"));
    }

    public void setItem(Item item) {
        this.setItem(item, true);
    }

    public void setItem(Item item, boolean setChanged) {
        this.nbt.putCompound("Item", ItemHelper.write(item, null));
        if (setChanged) {
            this.setDirty();
        } else this.level.updateComparatorOutputLevel(this);
    }

    @Override
    public void close() {
        if (isValid() && chunk.isLoaded() && level.isChunkInUse(chunk.getX(), chunk.getZ())) {
            //Those also drop when broken in creative mode
            level.dropItem(this.add(HALF), this.getItem());
        }
        super.close();
    }
}
