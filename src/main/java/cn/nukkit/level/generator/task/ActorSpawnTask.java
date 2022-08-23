package cn.nukkit.level.generator.task;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.scheduler.Task;

@PowerNukkitXOnly
@Since("1.19.20-r6")
public class ActorSpawnTask extends Task {

    private final Level level;
    private final CompoundTag nbt;

    public ActorSpawnTask(Level level, CompoundTag nbt) {
        this.level = level;
        this.nbt = nbt;
    }

    @Override
    public void onRun(int currentTick) {
        ListTag<DoubleTag> pos = this.nbt.getList("Pos", DoubleTag.class);
        Entity entity = Entity.createEntity(this.nbt.getString("id"),
                this.level.getChunk((int) pos.get(0).data >> 4, (int) pos.get(2).data >> 4), this.nbt);
        if (entity != null) {
            entity.spawnToAll();
        }
    }
}
