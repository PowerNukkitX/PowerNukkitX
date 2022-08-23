package cn.nukkit.level.generator.task;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.Task;

@PowerNukkitXOnly
@Since("1.19.20-r6")
public class BlockActorSpawnTask extends Task {

    private final Level level;
    private final CompoundTag nbt;

    public BlockActorSpawnTask(Level level, CompoundTag nbt) {
        this.level = level;
        this.nbt = nbt;
    }

    @Override
    public void onRun(int currentTick) {
        BlockEntity.createBlockEntity(this.nbt.getString("id"),
                this.level.getChunk(this.nbt.getInt("x") >> 4, this.nbt.getInt("z") >> 4), this.nbt);
    }
}
