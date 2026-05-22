package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.IChunk;
import org.cloudburstmc.nbt.NbtMap;

public class BlockEntityMusic extends BlockEntity {

    public BlockEntityMusic(IChunk chunk, NbtMap nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!this.nbt.containsKey("note")) {
            this.nbt.putByte("note", (byte) 0);
        }
        if (!this.nbt.containsKey("powered")) {
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
