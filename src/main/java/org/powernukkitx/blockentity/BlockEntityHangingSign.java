package org.powernukkitx.blockentity;

import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;


public class BlockEntityHangingSign extends BlockEntitySign {
    public BlockEntityHangingSign(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getName() {
        return this.hasName() ? this.getNbt().getString("CustomName") : "Hanging Sign";
    }

    public boolean hasName() {
        return nbt.contains("CustomName");
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound().putString("id", BlockEntity.HANGING_SIGN);
    }
}
