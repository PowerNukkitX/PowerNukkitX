package cn.nukkit.entity.mob;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityWalkable;
import cn.nukkit.entity.passive.EntityVillagerV2;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.format.IChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author PikyCZ
 */
public abstract class EntityIllager extends EntityMob implements EntityWalkable {
    public EntityIllager(IChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean attackTarget(Entity entity) {
        return switch (entity.getIdentifier()) {
            case VILLAGER ->
                    entity instanceof EntityVillagerV2 villager && !villager.isBaby();
            case IRON_GOLEM, WANDERING_TRADER -> true;
            default -> super.attackTarget(entity);
        };
    }

    private static final int EXTRA_DROP_CHANCE_HARD = 80;
    private static final int EXTRA_DROP_CHANCE_DEFAULT = 65;

    /**
     * @return {@code true} if this illager was spawned as part of a village raid wave.
     */
    protected boolean isRaider() {
        return this.nbt.getBoolean("IsRaider");
    }

    /**
     * Vanilla MCBE raid drop table shared by pillagers and vindicators.
     * Called only when the mob has the IsRaider NBT tag.
     *
     * Drop table (78 slots):
     *  20/78 — 0-1 emerald
     *  10/78 — 2-3 emerald
     *   4/78 — 4-5 emerald
     *   4/78 — enchanted book (high-level, any enchantment)
     *   5/78 each — iron axe/shovel/pickaxe/sword/helmet/chestplate/leggings/boots
     *               (always damaged, 50% chance low-medium random enchantment)
     *
     * Base emerald (0-1) always drops separately.
     * Extra table has 65% chance on Easy/Normal, 80% on Hard.
     * Only emerald quantities are affected by Looting (+1 cap per level).
     */
    protected static void addRaidDrops(List<Item> drops, @NotNull Item weapon) {
        int looting = weapon.getEnchantmentLevel(Enchantment.ID_LOOTING);
        int difficulty = Server.getInstance().getDifficulty();

        int base = Utils.rand(0, 1 + looting);
        if (base > 0) drops.add(Item.get(Item.EMERALD, 0, base));

        int extraDropChance = (difficulty >= 3) ? EXTRA_DROP_CHANCE_HARD : EXTRA_DROP_CHANCE_DEFAULT;
        if (Utils.rand(0, 99) < extraDropChance) {
            int roll = ThreadLocalRandom.current().nextInt(78);
            if (roll < 20) {
                int qty = Utils.rand(0, 1 + looting);
                if (qty > 0) drops.add(Item.get(Item.EMERALD, 0, qty));
            } else if (roll < 30) {
                drops.add(Item.get(Item.EMERALD, 0, Utils.rand(2, 3) + looting));
            } else if (roll < 34) {
                drops.add(Item.get(Item.EMERALD, 0, Utils.rand(4, 5) + looting));
            } else if (roll < 38) {
                drops.add(makeRaidBook());
            } else {
                String[] ironIds = {
                    Item.IRON_AXE, Item.IRON_SHOVEL, Item.IRON_PICKAXE, Item.IRON_SWORD,
                    Item.IRON_HELMET, Item.IRON_CHESTPLATE, Item.IRON_LEGGINGS, Item.IRON_BOOTS
                };
                int idx = (roll - 38) / 5;
                drops.add(makeRaidIronItem(ironIds[idx]));
            }
        }
    }

    private static Item makeRaidIronItem(String itemId) {
        Item item = Item.get(itemId);
        int maxDur = item.getMaxDurability();
        if (maxDur > 1) {
            item.setDamage(ThreadLocalRandom.current().nextInt(1, maxDur));
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            Collection<Enchantment> pool = Enchantment.getRegisteredEnchantments();
            List<Enchantment> valid = new ArrayList<>();
            for (Enchantment e : pool) {
                if (e != null && e.canEnchant(item)) valid.add(e);
            }
            if (!valid.isEmpty()) {
                Enchantment src = valid.get(ThreadLocalRandom.current().nextInt(valid.size()));
                Enchantment enc = Enchantment.getEnchantment(src.getId());
                int maxLevel = enc.getMaxLevel();
                int level = ThreadLocalRandom.current().nextInt(1, Math.max(2, maxLevel / 2 + 1));
                enc.setLevel(level);
                item.addEnchantment(enc);
            }
        }
        return item;
    }

    private static Item makeRaidBook() {
        Item book = Item.get(Item.ENCHANTED_BOOK);
        Collection<Enchantment> all = Enchantment.getRegisteredEnchantments();
        if (!all.isEmpty()) {
            List<Enchantment> pool = new ArrayList<>(all);
            Enchantment src = pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
            Enchantment enc = Enchantment.getEnchantment(src.getId());
            enc.setLevel(enc.getMaxLevel());
            book.addEnchantment(enc);
        }
        return book;
    }
}
