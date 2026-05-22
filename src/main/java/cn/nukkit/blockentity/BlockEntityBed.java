package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.utils.DyeColor;
import org.cloudburstmc.nbt.NbtMap;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public class BlockEntityBed extends BlockEntitySpawnable {

    public int color;

    public BlockEntityBed(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!this.nbt.containsKey("color")) {
            this.nbt.putByte("color", (byte) 0);
        }

        this.color = this.getNbt().getByte("color");
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.level.getBlockIdAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()) == BlockID.BED;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putByte("color", (byte) this.color);
    }

    @Override
    public NbtMap getSpawnCompound() {
        return super.getSpawnCompound().toBuilder().putByte("color", (byte) this.color).build();
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(color);
    }
}
