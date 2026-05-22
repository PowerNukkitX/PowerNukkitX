package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.BarrelInventory;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.NbtHelper;
import org.cloudburstmc.nbt.NbtMap;


public class BlockEntityBarrel extends BlockEntitySpawnableContainer {


    public BlockEntityBarrel(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
        movable = true;
    }

    @Override
    protected BarrelInventory requireContainerInventory() {
        return new BarrelInventory(this);
    }

    @Override
    public NbtMap getSpawnCompound() {
        return super.getSpawnCompound().toBuilder()
                .putBoolean("isMovable", this.isMovable())
                .putBoolean("Findable", false)
                .build();
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == BlockID.BARREL;
    }

    @Override
    public BarrelInventory getInventory() {
        return (BarrelInventory) inventory;
    }

    @Override
    public String getName() {
        return this.hasName() ? this.getNbt().getString("CustomName") : "Barrel";
    }

    @Override
    public boolean hasName() {
        return this.nbt.containsKey("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            this.nbt.remove("CustomName");
            return;
        }
        this.nbt.putString("CustomName", name);
    }
}
