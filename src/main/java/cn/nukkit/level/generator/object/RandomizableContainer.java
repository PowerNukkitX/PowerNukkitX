package cn.nukkit.level.generator.object;

import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.random.RandomSourceProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class RandomizableContainer {

    protected final Map<List<ItemEntry>, RollEntry> pools;

    public RandomizableContainer() {
        this.pools = Maps.newHashMap();
    }

    public void create(Inventory inventory, RandomSourceProvider random) {
        try {
            this.pools.forEach((pool, roll) -> {
                for (int i = roll.getMin() == -1 ? roll.getMax() : NukkitMath.randomRange(random, roll.getMin(), roll.getMax()); i > 0; --i) {
                    int result = random.nextBoundedInt(roll.getTotalWeight());
                    for (ItemEntry entry : pool) {
                        result -= entry.getWeight();
                        if (result < 0) {
                            int index = random.nextBoundedInt(inventory.getSize());
                            Item item = Item.get(entry.getId(), entry.getMeta(), NukkitMath.randomRange(random, entry.getMinCount(), entry.getMaxCount()));
                            if(entry.enchantments.length != 0) {
                                Enchantment enchantment = Enchantment.getEnchantment(entry.enchantments[random.nextInt(entry.enchantments.length)].id);
                                enchantment.setLevel(random.nextInt(enchantment.getMaxLevel()) + 1);
                                item.addEnchantment(enchantment);
                            }
                            inventory.setItem(index, item);
                            break;
                        }
                    }
                }
            });
        } catch (Exception ignored) {}
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

        private final String id;
        private final int meta;
        private final int maxCount;
        private final int minCount;
        private final int weight;
        private final Enchantment[] enchantments;

        public ItemEntry(String id, int weight) {
            this(id, 0, weight);
        }

        public ItemEntry(String id, int meta, int weight) {
            this(id, meta, 1, weight);
        }

        public ItemEntry(String id, int meta, int maxCount, int weight) {
            this(id, meta, maxCount, 1, weight);
        }

        public ItemEntry(String id, int meta, int maxCount, int minCount, int weight) {
            this(id, meta, maxCount, minCount, weight, Enchantment.EMPTY_ARRAY);
        }

        public ItemEntry(String id, int meta, int maxCount, int minCount, int weight, Enchantment[] enchantments) {
            this.id = id;
            this.meta = meta;
            this.maxCount = maxCount;
            this.minCount = minCount;
            this.weight = weight;
            this.enchantments = enchantments;
        }

        public String getId() {
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

    private static final Enchantment[] DEFAULT_ENCHANTMENTS;

    protected Enchantment[] getDefaultEnchantments() {
        return DEFAULT_ENCHANTMENTS;
    }

    static {
        List<Enchantment> enchantments = new ArrayList<>();
        for(Enchantment enchantment : Enchantment.getEnchantments()) {
            switch (enchantment.getId()) {
                case Enchantment.ID_SOUL_SPEED,
                     Enchantment.ID_SWIFT_SNEAK,
                     Enchantment.ID_WIND_BURST -> {
                }
                default -> enchantments.add(enchantment);
            }
        }
        DEFAULT_ENCHANTMENTS = enchantments.toArray(Enchantment[]::new);
    }
}
