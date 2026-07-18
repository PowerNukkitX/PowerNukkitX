package org.powernukkitx.blockentity;

import org.powernukkitx.block.BlockID;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.utils.DyeColor;

/**
 * @author CreeperFace
 * @since 2.6.2017
 */
public class BlockEntityBed extends BlockEntitySpawnable {

    public int color;

    public BlockEntityBed(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!this.nbt.contains("color")) {
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
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound().putByte("color", this.color);
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(color);
    }
}
