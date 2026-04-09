package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtMapBuilder;

public class BlockEntityMusic extends BlockEntity {

    public BlockEntityMusic(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        final NbtMapBuilder builder = NbtMap.builder();
        if (!this.namedTag.containsKey("note")) {
            builder.putByte("note", (byte) 0);
        }
        if (!this.namedTag.containsKey("powered")) {
            builder.putBoolean("powered", false);
        }
        this.namedTag = builder.build();
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Block.NOTEBLOCK;
    }

    public void changePitch() {
        this.namedTag = this.namedTag.toBuilder().putByte("note", (byte) ((this.namedTag.getByte("note") + 1) % 25)).build();
    }

    public int getPitch() {
        return this.namedTag.getByte("note");
    }

    public void setPowered(boolean powered) {
        this.namedTag = this.namedTag.toBuilder().putBoolean("powered", powered).build();
    }

    public boolean isPowered() {
        return this.namedTag.getBoolean("powered");
    }
}
