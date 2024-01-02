package cn.nukkit.blockentity;

import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;


public class BlockEntityHangingSign extends BlockEntitySign {
    public BlockEntityHangingSign(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Hanging Sign";
    }

    public boolean hasName() {
        return namedTag.contains("CustomName");
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return super.getSpawnCompound().putString("id", BlockEntity.HANGING_SIGN);
    }
}
