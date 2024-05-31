package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockEntityMusic extends BlockEntity {
    /**
     * @deprecated 
     */
    

    public BlockEntityMusic(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        if (!this.namedTag.contains("note")) {
            this.namedTag.putByte("note", 0);
        }
        if (!this.namedTag.contains("powered")) {
            this.namedTag.putBoolean("powered", false);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Block.NOTEBLOCK;
    }
    /**
     * @deprecated 
     */
    

    public void changePitch() {
        this.namedTag.putByte("note", (this.namedTag.getByte("note") + 1) % 25);
    }
    /**
     * @deprecated 
     */
    

    public int getPitch() {
        return this.namedTag.getByte("note");
    }
    /**
     * @deprecated 
     */
    

    public void setPowered(boolean powered) {
        this.namedTag.putBoolean("powered", powered);
    }
    /**
     * @deprecated 
     */
    

    public boolean isPowered() {
        return this.namedTag.getBoolean("powered");
    }
}
