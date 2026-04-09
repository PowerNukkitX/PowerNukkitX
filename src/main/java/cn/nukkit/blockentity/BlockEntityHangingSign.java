package cn.nukkit.blockentity;

import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;


public class BlockEntityHangingSign extends BlockEntitySign {
    public BlockEntityHangingSign(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public String getName() {
        return this.hasName() ? this.namedTag.getString("CustomName") : "Hanging Sign";
    }

    public boolean hasName() {
        return namedTag.containsKey("CustomName");
    }

    @Override
    public NbtMap getSpawnCompound() {
        return super.getSpawnCompound().toBuilder().putString("id", BlockEntity.HANGING_SIGN).build();
    }
}
