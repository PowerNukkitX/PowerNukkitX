package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntityCommandBlock;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockCommandBlockRepeating extends BlockCommandBlock {

    public BlockCommandBlockRepeating() {
        this(0);
    }

    public BlockCommandBlockRepeating(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BlockID.REPEATING_COMMAND_BLOCK;
    }

    @Override
    public String getName() {
        return "Repeating Command Block";
    }

    @Override
    protected CompoundTag createCompoundTag(CompoundTag nbt) {
        nbt.putBoolean(BlockEntityCommandBlock.TAG_EXECUTE_ON_FIRST_TICK, true);
        return nbt;
    }
}
