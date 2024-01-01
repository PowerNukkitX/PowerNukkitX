package cn.nukkit.level.generator.task;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.Task;


public class TileSyncTask extends Task {

    public final String type;
    public final FullChunk chunk;
    public final CompoundTag nbt;

    public TileSyncTask(String type, IChunk chunk, CompoundTag nbt) {
        this.type = type;
        this.chunk = chunk;
        this.nbt = nbt;
    }

    @Override
    public void onRun(int currentTick) {
        BlockEntity.createBlockEntity(this.type, this.chunk, this.nbt);
    }
}
