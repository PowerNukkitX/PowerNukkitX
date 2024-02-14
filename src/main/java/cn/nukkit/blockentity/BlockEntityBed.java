package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.DyeColor;

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
        if (!this.namedTag.contains("color")) {
            this.namedTag.putByte("color", 0);
        }

        this.color = this.namedTag.getByte("color");
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.level.getBlockIdAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()) == BlockID.BED;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putByte("color", this.color);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound().putByte("color", this.color);
    }

    public DyeColor getDyeColor() {
        return DyeColor.getByWoolData(color);
    }
}
