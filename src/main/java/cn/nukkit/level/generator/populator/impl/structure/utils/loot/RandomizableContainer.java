package cn.nukkit.level.generator.populator.impl.structure.utils.loot;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.19.21-r2")
public class RandomizableContainer {

    protected final Map<List<ItemEntry>, RollEntry> pools;
    protected final int size;

    public RandomizableContainer(Map<List<ItemEntry>, RollEntry> pools, int size) {
        Preconditions.checkNotNull(pools);
        this.pools = pools;
        this.size = size;
    }

    public void create(ListTag<CompoundTag> list, NukkitRandom random) {
        CompoundTag[] tags = new CompoundTag[this.size];

        this.pools.forEach((pool, roll) -> {
            for (int i = roll.getMin() == -1 ? roll.getMax() : random.nextRange(roll.getMin(), roll.getMax()); i > 0; --i) {
                int result = random.nextBoundedInt(roll.getTotalWeight());
                for (ItemEntry entry : pool) {
                    result -=  entry.getWeight();
                    if (result < 0) {
                        int index = random.nextBoundedInt(tags.length);
                        tags[index] = NBTIO.putItemHelper(Item.get(entry.getId(), entry.getMeta(), random.nextRange(entry.getMinCount(), entry.getMaxCount())), index);
                        break;
                    }
                }
            }
        });

        for (int i = 0; i < tags.length; i++) {
            if (tags[i] == null) {
                list.add(i, NBTIO.putItemHelper(Item.get(Item.AIR), i));
            } else {
                list.add(i, tags[i]);
            }
        }
    }

    protected static class RollEntry {

        private final int max;
        private final int min;
        private final int totalWeight;

        public RollEntry(int max, int totalWeight) {
            this(max, -1, totalWeight);
        }

        public RollEntry(int max, int min, int totalWeight) {
            this.max = max;
            this.min = min;
            this.totalWeight = totalWeight;
        }

        public int getMax() {
            return this.max;
        }

        public int getMin() {
            return this.min;
        }

        public int getTotalWeight() {
            return this.totalWeight;
        }
    }

    protected static class ItemEntry {

        private final int id;
        private final int meta;
        private final int maxCount;
        private final int minCount;
        private final int weight;

        public ItemEntry(int id, int weight) {
            this(id, 0, weight);
        }

        public ItemEntry(int id, int meta, int weight) {
            this(id, meta, 1, weight);
        }

        public ItemEntry(int id, int meta, int maxCount, int weight) {
            this(id, meta, maxCount, 1, weight);
        }

        public ItemEntry(int id, int meta, int maxCount, int minCount, int weight) {
            this.id = id;
            this.meta = meta;
            this.maxCount = maxCount;
            this.minCount = minCount;
            this.weight = weight;
        }

        public int getId() {
            return this.id;
        }

        public int getMeta() {
            return this.meta;
        }

        public int getMaxCount() {
            return this.maxCount;
        }

        public int getMinCount() {
            return this.minCount;
        }

        public int getWeight() {
            return this.weight;
        }
    }

    public static class PoolBuilder {

        private final List<ItemEntry> pool = Lists.newArrayList();
        private int totalWeight = 0;

        public PoolBuilder register(ItemEntry entry) {
            this.pool.add(entry);
            this.totalWeight += entry.getWeight();
            return this;
        }

        public List<ItemEntry> build() {
            return this.pool;
        }

        public int getTotalWeight() {
            return this.totalWeight;
        }
    }
}
