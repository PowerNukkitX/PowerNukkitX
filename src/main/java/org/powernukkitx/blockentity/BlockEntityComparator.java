package org.powernukkitx.blockentity;

import org.powernukkitx.block.BlockRedstoneComparator;
import org.powernukkitx.level.format.IChunk;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * @author CreeperFace
 */
public class BlockEntityComparator extends BlockEntity {

    private int outputSignal;

    public BlockEntityComparator(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public void loadNBT() {
        super.loadNBT();
        if (!nbt.contains("OutputSignal")) {
            this.nbt.putInt("OutputSignal", 0);
        }

        this.outputSignal = getNbt().getInt("OutputSignal");
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getLevelBlock() instanceof BlockRedstoneComparator;
    }

    public int getOutputSignal() {
        return outputSignal;
    }

    public void setOutputSignal(int outputSignal) {
        this.outputSignal = outputSignal;
    }

    @Override
    public void saveNBT() {
        super.saveNBT();
        this.nbt.putInt("OutputSignal", this.outputSignal);
    }
}
