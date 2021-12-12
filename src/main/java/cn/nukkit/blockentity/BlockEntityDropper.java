package cn.nukkit.blockentity;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.DropperInventory;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

@PowerNukkitOnly
public class BlockEntityDropper extends BlockEntityEjectable {

    @PowerNukkitOnly
    public BlockEntityDropper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @PowerNukkitOnly
    @Override
    protected DropperInventory createInventory() {
        inventory = new DropperInventory(this);
        return getInventory();
    }

    @PowerNukkitOnly
    @Override
    protected String getBlockEntityName() {
        return BlockEntity.DISPENSER;
    }

    @Override
    public DropperInventory getInventory() {
        return (DropperInventory) inventory;
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevelBlock().getId() == BlockID.DROPPER;
    }
}
