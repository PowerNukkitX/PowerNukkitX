package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.DropperInventory;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;


public class BlockEntityDropper extends BlockEntityEjectable {


    public BlockEntityDropper(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    protected DropperInventory createInventory() {
        inventory = new DropperInventory(this);
        return getInventory();
    }

    @Override
    protected String getBlockEntityName() {
        return BlockEntity.DROPPER;
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
