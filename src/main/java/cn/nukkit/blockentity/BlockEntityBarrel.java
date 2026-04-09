package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.inventory.BarrelInventory;
import cn.nukkit.level.format.IChunk;
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
        return this.hasName() ? this.namedTag.getString("CustomName") : "Barrel";
    }

    @Override
    public boolean hasName() {
        return this.namedTag.containsKey("CustomName");
    }

    @Override
    public void setName(String name) {
        if (name == null || name.equals("")) {
            this.namedTag.remove("CustomName");
            return;
        }

        this.namedTag = this.namedTag.toBuilder().putString("CustomName", name).build();
    }
}
