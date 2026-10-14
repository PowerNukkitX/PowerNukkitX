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
 * weighted entry from those whose conditions hold.
 * <p>
 * This models the subset of the Bedrock loot table format that can be resolved from the
 * tool in hand alone, without a world context. Entry counts, damage values, display names
 * and lore are applied; {@link Condition}s gate pools and entries; and an entry may point at
 * another table, resolved through {@link LootTableRegistry} at generation time so tables may
 * reference each other regardless of load order. Anything else a pack declares is left to
 * the caller.
 * <p>
 * Instances are immutable and may be shared between threads; generation keeps no state
 * beyond the random source it is handed.
 *
 * @param pools the pools to roll, in order; an empty list generates nothing
 */
@Slf4j
public record LootTable(List<Pool> pools) {

    /**
     * Guards against a cycle between tables that reference each other. Bedrock has no such
     * limit; this simply stops generation instead of overflowing the stack.
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
         * @return a value in {@code [min, max]}, or {@code min} when the range is empty or
         * inverted
         */
        public int sample(Random random) {
            if (max <= min) {
                return min;
            }
            return min + random.nextInt(max - min + 1);
        }
    }

    /**
     * A requirement a pool or an entry must meet to take part in a roll.
     */
    public sealed interface Condition permits RandomChance, MatchTool {

        /**
         * @param random the generation's random source
         * @param tool   the item the loot is generated for, or null for an empty hand
         * @return true when the condition holds for this roll
         */
        boolean test(@NotNull Random random, @Nullable Item tool);
    }

    /**
     * Holds with the given probability, drawn fresh for every test.
     *
     * @param chance probability in {@code [0, 1]}
     */
    public record RandomChance(float chance) implements Condition {

        @Override
        public boolean test(@NotNull Random random, @Nullable Item tool) {
            return chance >= 1.0f || random.nextFloat() < chance;
        }
    }

    /**
     * Holds when the tool in hand matches every given requirement.
     *
     * @param item          the exact item identifier required, or null for any item
     * @param allTags       tags the tool must all carry
     * @param anyTags       tags of which the tool must carry at least one; empty for no requirement
     * @param noneTags      tags the tool must not carry
     * @param unsatisfiable true when the pack declared a requirement this engine cannot check,
     *                      such as enchantments; the condition then never holds, so loot that
     *                      needs such a tool is not handed out to any tool
     */
    public record MatchTool(@Nullable String item, List<String> allTags, List<String> anyTags,
                            List<String> noneTags, boolean unsatisfiable) implements Condition {

        public MatchTool {
            allTags = List.copyOf(allTags);
            anyTags = List.copyOf(anyTags);
            noneTags = List.copyOf(noneTags);
        }

        @Override
        public boolean test(@NotNull Random random, @Nullable Item tool) {
            if (unsatisfiable) {
                return false;
            }
            boolean empty = tool == null || tool.isNull();
            if (item != null && (empty || !item.equals(tool.getId()))) {
                return false;
            }
            for (String tag : allTags) {
                if (empty || !tool.hasTag(tag)) {
                    return false;
                }
            }
            if (!anyTags.isEmpty()) {
                if (empty) {
                    return false;
                }
                boolean found = false;
                for (String tag : anyTags) {
                    if (tool.hasTag(tag)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }
            if (!empty) {
                for (String tag : noneTags) {
                    if (tool.hasTag(tag)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    /**
     * @param rolls      how many entries to pick per generation
     * @param conditions requirements for the pool to be rolled at all; empty when it always rolls
     * @param entries    the candidates; an empty list makes the pool generate nothing
     */
    public record Pool(NumberRange rolls, List<Condition> conditions, List<Entry> entries) {

        public Pool {
            conditions = List.copyOf(conditions);
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
     * @param weight      relative weight against the other candidates in the roll; values
     *                    below {@code 1} are treated as {@code 1}
     * @param count       how many items to yield, for item entries
     * @param data        damage/meta value to apply, or null to leave the item's default
     * @param displayName custom name to apply, or null for none
     * @param lore        lore lines to apply, or null for none
     * @param conditions  requirements for the entry to be a candidate in a roll; an entry
     *                    whose conditions fail is left out before the weighted pick, as
     *                    Bedrock does, rather than picked and then discarded
     */
    public record Entry(
            EntryType type,
            String name,
            int weight,
            NumberRange count,
            @Nullable Integer data,
            @Nullable String displayName,
            @Nullable List<String> lore,
            List<Condition> conditions
    ) {

        public Entry {
            lore = lore == null ? null : List.copyOf(lore);
            conditions = List.copyOf(conditions);
        }

        /** An entry that yields nothing. */
        public static Entry empty() {
            return new Entry(EntryType.EMPTY, "", 1, NumberRange.ONE, null, null, null, List.of());
        }
    }

    /**
     * Rolls every pool of this table for an empty hand.
     *
     * @see #generate(Random, Item)
     */
    public @NotNull List<Item> generate(@NotNull Random random) {
        return generate(random, null);
    }

    /**
     * Rolls every pool of this table.
     * <p>
     * Entries naming an item that is not registered, and references to tables that are not
     * registered, are skipped rather than failing the whole roll, because a behaviour pack may
     * legitimately reference content another pack provides.
     *
     * @param random the random source to draw from
     * @param tool   the item the loot is generated for, as tool conditions see it; null for an
     *               empty hand
     * @return the generated stacks, empty when nothing was rolled; never null
     */
    public @NotNull List<Item> generate(@NotNull Random random, @Nullable Item tool) {
        List<Item> result = new ArrayList<>();
        generateInto(random, tool, result, 0);
        return result;
    }

    private void generateInto(Random random, @Nullable Item tool, List<Item> result, int depth) {
        if (depth > MAX_NESTING_DEPTH) {
            log.warn("Loot table nesting deeper than {}, aborting generation", MAX_NESTING_DEPTH);
            return;
        }
        for (Pool pool : pools) {
            if (!allHold(pool.conditions(), random, tool)) {
                continue;
            }
            int rolls = pool.rolls().sample(random);
            for (int i = 0; i < rolls; i++) {
                Entry entry = pickWeighted(pool.entries(), random, tool);
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
                            nested.generateInto(random, tool, result, depth + 1);
                        } else {
                            log.debug("Referenced loot table {} is not registered", entry.name());
                        }
                    }
                }
            }
        }
    }

    private static boolean allHold(List<Condition> conditions, Random random, @Nullable Item tool) {
        for (Condition condition : conditions) {
            if (!condition.test(random, tool)) {
                return false;
            }
        }
        return true;
    }

    private static @Nullable Entry pickWeighted(List<Entry> entries, Random random, @Nullable Item tool) {
        if (entries.isEmpty()) {
            return null;
        }
        List<Entry> candidates = new ArrayList<>(entries.size());
        int totalWeight = 0;
        for (Entry entry : entries) {
            if (allHold(entry.conditions(), random, tool)) {
                candidates.add(entry);
                totalWeight += Math.max(1, entry.weight());
            }
        }
        if (candidates.isEmpty()) {
            return null;
        }
        int roll = random.nextInt(totalWeight);
        for (Entry entry : candidates) {
            roll -= Math.max(1, entry.weight());
            if (roll < 0) {
                return entry;
            }
        }
        return candidates.getLast();
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
