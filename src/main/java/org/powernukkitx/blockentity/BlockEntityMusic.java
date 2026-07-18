package org.powernukkitx.blockentity;

import org.powernukkitx.block.Block;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;

public class BlockEntityMusic extends BlockEntity {

    public BlockEntityMusic(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!this.nbt.contains("note")) {
            this.nbt.putByte("note", (byte) 0);
        }
        if (!this.nbt.contains("powered")) {
            this.nbt.putBoolean("powered", false);
        }
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Block.NOTEBLOCK;
    }

    public void changePitch() {
        this.nbt.putByte("note", (byte) ((this.getNbt().getByte("note") + 1) % 25));
    }

    public int getPitch() {
        return this.getNbt().getByte("note");
    }

    public void setPowered(boolean powered) {
        this.nbt.putBoolean("powered", powered);
    }

    public boolean isPowered() {
        return this.getNbt().getBoolean("powered");
    }
}
