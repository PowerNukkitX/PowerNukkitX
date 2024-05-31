package cn.nukkit.blockentity;

import cn.nukkit.block.BlockRedstoneComparator;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author CreeperFace
 */
public class BlockEntityComparator extends BlockEntity {

    private int outputSignal;
    /**
     * @deprecated 
     */
    

    public BlockEntityComparator(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void loadNBT() {
        super.loadNBT();
        if (!namedTag.contains("OutputSignal")) {
            namedTag.putInt("OutputSignal", 0);
        }

        this.outputSignal = namedTag.getInt("OutputSignal");
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isBlockEntityValid() {
        return this.getLevelBlock() instanceof BlockRedstoneComparator;
    }
    /**
     * @deprecated 
     */
    

    public int getOutputSignal() {
        return outputSignal;
    }
    /**
     * @deprecated 
     */
    

    public void setOutputSignal(int outputSignal) {
        this.outputSignal = outputSignal;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void saveNBT() {
        super.saveNBT();
        this.namedTag.putInt("OutputSignal", this.outputSignal);
    }
}
