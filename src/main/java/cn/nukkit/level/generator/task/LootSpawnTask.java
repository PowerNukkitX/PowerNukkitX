package cn.nukkit.level.generator.task;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.scheduler.Task;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class LootSpawnTask extends Task {

    private final Level level;
    private final BlockVector3 pos;
    private final ListTag<CompoundTag> list;

    public LootSpawnTask(Level level, BlockVector3 pos, ListTag<CompoundTag> list) {
        this.level = level;
        this.pos = pos;
        this.list = list;
    }

    @Override
    public void onRun(int currentTick) {
        BlockEntity tile = this.level.getBlockEntity(this.pos.asVector3());
        if (tile instanceof InventoryHolder) {
            tile.namedTag.putList(this.list);
            Inventory inventory = ((InventoryHolder) tile).getInventory();
            for (int i = 0; i < this.list.size(); i ++) {
                inventory.setItem(i, NBTIO.getItemHelper(this.list.get(i)), false);
            }
        }
    }
}
