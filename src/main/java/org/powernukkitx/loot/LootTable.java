package org.powernukkitx.loot;

import org.powernukkitx.item.Item;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A loot table: a list of pools, each rolled independently, where every roll picks one
 * weighted entry.
 * <p>
 * This models the subset of the Bedrock loot table format that can be resolved without a
 * world context. Entry counts, damage values, display names and lore are applied, and an
 * entry may point at another table, which is resolved through {@link LootTableRegistry}
 * at generation time so tables may reference each other regardless of load order.
 * Conditions other than a pool-level random chance, and item functions beyond the fields
 * modelled on {@link Entry}, are not evaluated - a caller that needs them has to apply
 * them itself.
 * <p>
 * Instances are immutable and may be shared between threads; {@link #generate(Random)}
 * keeps no state beyond the random source it is handed.
 *
 * @param pools the pools to roll, in order; an empty list generates nothing
 */
@Slf4j
public record LootTable(List<Pool> pools) {

    /**
     * Guards against a cycle between tables that reference each other. Bedrock has no
     * such limit; this simply stops generation instead of overflowing the stack.
     */
    private static final int MAX_NESTING_DEPTH = 16;

    public LootTable {
        pools = List.copyOf(pools);
    }

    /**
     * An inclusive integer range. A fixed value is expressed as {@code min == max}.
     */
    public record NumberRange(int min, int max) {

        public static final NumberRange ONE = new NumberRange(1, 1);

        /**
         * @return a value in {@code [min, max]}, or {@code min} when the range is empty
         * or inverted
         */
        public int sample(Random random) {
            if (max <= min) {
                return min;
            }
            return min + random.nextInt(max - min + 1);
        }
    }

    /**
     * @param rolls        how many entries to pick per generation
     * @param randomChance chance in {@code [0, 1]} that the pool is rolled at all; use
     *                     {@code 1.0f} for a pool that always rolls
     * @param entries      the candidates; an empty list makes the pool generate nothing
     */
    public record Pool(NumberRange rolls, float randomChance, List<Entry> entries) {

        public Pool {
            entries = List.copyOf(entries);
        }
    }

    public enum EntryType {
        /** Yields the item named by {@link Entry#name()}. */
        ITEM,
        /** Yields whatever the table named by {@link Entry#name()} generates. */
        LOOT_TABLE,
        /** Yields nothing; used to give a pool a chance of dropping nothing at all. */
        EMPTY
    }

    /**
     * @param name        item identifier for {@link EntryType#ITEM}, table path for
     *                    {@link EntryType#LOOT_TABLE}, ignored for {@link EntryType#EMPTY}
     * @param weight      relative weight against the other entries in the pool; values
     *                    below {@code 1} are treated as {@code 1}
     * @param count       how many items to yield, for item entries
     * @param data        damage/meta value to apply, or null to leave the item's default
     * @param displayName custom name to apply, or null for none
     * @param lore        lore lines to apply, or null for none
     */
    public record Entry(
            EntryType type,
            String name,
            int weight,
            NumberRange count,
            @Nullable Integer data,
            @Nullable String displayName,
            @Nullable List<String> lore
    ) {

        public Entry {
            lore = lore == null ? null : List.copyOf(lore);
        }

        /** An entry that yields nothing. */
        public static Entry empty() {
            return new Entry(EntryType.EMPTY, "", 1, NumberRange.ONE, null, null, null);
        }
    }

    /**
     * Rolls every pool of this table.
     * <p>
     * Entries naming an item that is not registered, and references to tables that are
     * not registered, are skipped rather than failing the whole roll, because a behavior
     * pack may legitimately reference content another pack provides.
     *
     * @param random the random source to draw from
     * @return the generated stacks, empty when nothing was rolled; never null
     */
    public @NotNull List<Item> generate(@NotNull Random random) {
        List<Item> result = new ArrayList<>();
        generateInto(random, result, 0);
        return result;
    }

    private void generateInto(Random random, List<Item> result, int depth) {
        if (depth > MAX_NESTING_DEPTH) {
            log.warn("Loot table nesting deeper than {}, aborting generation", MAX_NESTING_DEPTH);
            return;
        }
        for (Pool pool : pools) {
            if (pool.randomChance() < 1.0f && random.nextFloat() >= pool.randomChance()) {
                continue;
            }
            int rolls = pool.rolls().sample(random);
            for (int i = 0; i < rolls; i++) {
                Entry entry = pickWeighted(pool.entries(), random);
                if (entry == null) {
                    continue;
                }
                switch (entry.type()) {
                    case EMPTY -> {
                    }
                    case ITEM -> {
                        Item item = createItem(entry, random);
                        if (item != null && !item.isNull() && item.getCount() > 0) {
                            result.add(item);
                        }
                    }
                    case LOOT_TABLE -> {
                        LootTable nested = LootTableRegistry.get(entry.name());
                        if (nested != null) {
                            nested.generateInto(random, result, depth + 1);
                        } else {
                            log.debug("Referenced loot table {} is not registered", entry.name());
                        }
                    }
                }
            }
        }
    }

    private static @Nullable Entry pickWeighted(List<Entry> entries, Random random) {
        if (entries.isEmpty()) {
            return null;
        }
        int totalWeight = 0;
        for (Entry entry : entries) {
            totalWeight += Math.max(1, entry.weight());
        }
        int roll = random.nextInt(totalWeight);
        for (Entry entry : entries) {
            roll -= Math.max(1, entry.weight());
            if (roll < 0) {
                return entry;
            }
        }
        return entries.getLast();
    }

    private static @Nullable Item createItem(Entry entry, Random random) {
        Item item = Item.get(entry.name());
        if (item.isNull()) {
            log.debug("Loot table references unknown item {}", entry.name());
            return null;
        }
        item.setCount(entry.count().sample(random));
        if (entry.data() != null) {
            item.setDamage(entry.data());
        }
        if (entry.displayName() != null && !entry.displayName().isEmpty()) {
            item.setCustomName(entry.displayName());
        }
        if (entry.lore() != null && !entry.lore().isEmpty()) {
            item.setLore(entry.lore().toArray(new String[0]));
        }
        return item;
    }
}
