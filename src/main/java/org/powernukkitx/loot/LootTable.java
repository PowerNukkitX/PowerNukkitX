package org.powernukkitx.loot;

import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.entity.effect.PotionType;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBanner;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.item.ItemSuspiciousStew;
import org.powernukkitx.item.ItemWrittenBook;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.item.enchantment.EnchantmentHelper;
import org.powernukkitx.item.ItemColorArmor;
import org.powernukkitx.network.protocol.types.ArmorTrim;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.network.protocol.types.BannerPattern;
import org.powernukkitx.utils.DyeColor;
import org.powernukkitx.utils.ItemHelper;
import org.powernukkitx.utils.random.NukkitRandom;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A loot table: a list of pools, each rolled independently, where every roll picks one
 * weighted entry from those whose conditions hold.
 * <p>
 * This models the subset of the Bedrock loot table format that can be resolved from the
 * tool in hand alone, without a world context. Entry counts, damage values, display names
 * and lore are applied, then the entry's {@link ItemFunction}s; {@link Condition}s gate pools
 * and entries; and an entry may point at another table, resolved through
 * {@link LootTableRegistry} at generation time so tables may reference each other regardless of
 * load order. The tool is also what looting is read from, so a table rolled for a kill should be
 * handed the killer's weapon. Anything else a pack declares is left to the caller.
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
    public sealed interface Condition permits RandomChance, MatchTool, RandomDifficultyChance {

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
     * @param chance            probability in {@code [0, 1]}
     * @param lootingMultiplier added to {@code chance} once per level of looting on the tool
     */
    public record RandomChance(float chance, float lootingMultiplier) implements Condition {

        public RandomChance(float chance) {
            this(chance, 0f);
        }

        @Override
        public boolean test(@NotNull Random random, @Nullable Item tool) {
            float effective = chance + lootingMultiplier * lootingLevel(tool);
            return effective >= 1.0f || random.nextFloat() < effective;
        }
    }

    /**
     * Holds with a probability that depends on the server's difficulty.
     *
     * @param defaultChance the probability for difficulties without their own
     * @param byDifficulty  the probability for each difficulty, 0 (peaceful) to 3 (hard), or NaN where the
     *                      default applies
     */
    public record RandomDifficultyChance(float defaultChance, float[] byDifficulty) implements Condition {

        @Override
        public boolean test(@NotNull Random random, @Nullable Item tool) {
            int difficulty = Server.getInstance().getDifficulty();
            float chance = difficulty >= 0 && difficulty < byDifficulty.length && !Float.isNaN(byDifficulty[difficulty])
                    ? byDifficulty[difficulty] : defaultChance;
            return chance >= 1.0f || random.nextFloat() < chance;
        }
    }

    /**
     * An enchantment the tool must carry.
     *
     * @param enchantment the enchantment identifier, such as {@code minecraft:silk_touch}
     * @param minLevel    the lowest level accepted
     * @param maxLevel    the highest level accepted
     */
    public record EnchantmentRequirement(@NotNull String enchantment, int minLevel, int maxLevel) {

        boolean heldBy(@NotNull Item tool) {
            Enchantment registered = findEnchantment(enchantment);
            if (registered == null) {
                return false;
            }
            int level = tool.getEnchantmentLevel(registered.getId());
            return level >= Math.max(1, minLevel) && level <= maxLevel;
        }
    }

    /**
     * Holds when the tool in hand matches every given requirement.
     *
     * @param item          the exact item identifier required, or null for any item
     * @param allTags       tags the tool must all carry
     * @param anyTags       tags of which the tool must carry at least one; empty for no requirement
     * @param noneTags      tags the tool must not carry
     * @param unsatisfiable true when the pack declared a requirement this engine cannot check;
     *                      the condition then never holds, so loot that needs such a tool is not
     *                      handed out to any tool
     * @param enchantments  enchantments the tool must all carry
     * @param count         the range the tool's stack size must fall in, or null for any
     * @param durability    the range the tool's remaining durability must fall in, or null for any
     */
    public record MatchTool(@Nullable String item, List<String> allTags, List<String> anyTags,
                            List<String> noneTags, boolean unsatisfiable,
                            List<EnchantmentRequirement> enchantments, @Nullable NumberRange count,
                            @Nullable NumberRange durability) implements Condition {

        public MatchTool {
            allTags = List.copyOf(allTags);
            anyTags = List.copyOf(anyTags);
            noneTags = List.copyOf(noneTags);
            enchantments = List.copyOf(enchantments);
        }

        public MatchTool(@Nullable String item, List<String> allTags, List<String> anyTags,
                         List<String> noneTags, boolean unsatisfiable) {
            this(item, allTags, anyTags, noneTags, unsatisfiable, List.of(), null, null);
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
            for (EnchantmentRequirement requirement : enchantments) {
                if (empty || !requirement.heldBy(tool)) {
                    return false;
                }
            }
            if (count != null && (empty || tool.getCount() < count.min() || tool.getCount() > count.max())) {
                return false;
            }
            if (durability != null) {
                if (empty || tool.getMaxDurability() <= 0) {
                    return false;
                }
                int remaining = tool.getMaxDurability() - tool.getDamage();
                return remaining >= durability.min() && remaining <= durability.max();
            }
            return true;
        }
    }

    /**
     * Changes an item an entry yields, after its count, data, name and lore are applied.
     */
    public sealed interface ItemFunction permits SpecificEnchants, EnchantRandomly, EnchantWithLevels,
            RandomAuxValue, SetDamage, LootingEnchant, SetPotion, SetActorId, SetOminousBottleAmplifier, RandomDye,
            EnchantRandomGear, RandomBlockState, SetBannerDetails, SetBookContents, SetArmorTrim, FillContainer,
            ExplosionDecay, SetStewEffect {

        /**
         * @param item   the item so far; may be changed in place
         * @param random the generation's random source
         * @param tool   the item the loot is generated for, or null for an empty hand
         * @return the item to yield, which is {@code item} unless the function replaces it
         */
        @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool);
    }

    /**
     * Adds the given enchantments, at a level drawn from each one's range.
     *
     * @param enchantments enchantment identifier to level range
     */
    public record SpecificEnchants(@NotNull Map<String, NumberRange> enchantments) implements ItemFunction {

        public SpecificEnchants {
            enchantments = Map.copyOf(enchantments);
        }

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            Item target = enchantable(item);
            for (Map.Entry<String, NumberRange> entry : enchantments.entrySet()) {
                Enchantment enchantment = findEnchantment(entry.getKey());
                if (enchantment != null) {
                    target.addEnchantment(enchantment.setLevel(Math.max(1, entry.getValue().sample(random)), false));
                }
            }
            return target;
        }
    }

    /**
     * Adds one enchantment the item accepts, at a random level; a book accepts any.
     *
     * @param treasure whether treasure enchantments, which the enchanting table never offers, may be drawn
     */
    public record EnchantRandomly(boolean treasure) implements ItemFunction {

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            boolean book = ItemID.BOOK.equals(item.getId());
            List<Enchantment> pool = new ArrayList<>();
            for (Enchantment enchantment : Enchantment.getRegisteredEnchantments(true)) {
                if (enchantment != null && (treasure || enchantment.isObtainableFromEnchantingTable())
                        && (book || enchantment.canEnchant(item))) {
                    pool.add(enchantment);
                }
            }
            if (pool.isEmpty()) {
                return item;
            }
            Enchantment registered = pool.get(random.nextInt(pool.size()));
            Enchantment picked = registered.getIdentifier() != null
                    ? Enchantment.getEnchantment(registered.getIdentifier())
                    : Enchantment.getEnchantment(registered.getId());
            Item target = enchantable(item);
            target.addEnchantment(picked.setLevel(1 + random.nextInt(Math.max(1, picked.getMaxLevel())), false));
            return target;
        }
    }

    /**
     * Enchants the item the way the enchanting table does for the given number of levels.
     *
     * @param levels   the enchanting cost range
     * @param treasure whether treasure enchantments may be drawn
     */
    public record EnchantWithLevels(@NotNull NumberRange levels, boolean treasure) implements ItemFunction {

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            Item probe = ItemID.BOOK.equals(item.getId()) ? Item.get(ItemID.ENCHANTED_BOOK) : item;
            List<Enchantment> rolled = EnchantmentHelper.selectEnchantments(new NukkitRandom(random.nextLong()), probe,
                    Math.max(1, levels.sample(random)),
                    treasure ? enchantment -> true : Enchantment::isObtainableFromEnchantingTable);
            if (rolled.isEmpty()) {
                return item;
            }
            Item target = enchantable(item);
            target.addEnchantment(rolled.toArray(Enchantment.EMPTY_ARRAY));
            return target;
        }
    }

    /**
     * Sets the item's data value to one drawn from a range.
     */
    public record RandomAuxValue(@NotNull NumberRange values) implements ItemFunction {

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            item.setDamage(values.sample(random));
            return item;
        }
    }

    /**
     * Sets how much durability an item has left, as a fraction drawn from a range: {@code 1.0} is
     * undamaged, {@code 0.0} is broken. Items without durability are left as they are.
     */
    public record SetDamage(float min, float max) implements ItemFunction {

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            int maxDurability = item.getMaxDurability();
            if (maxDurability <= 0) {
                return item;
            }
            float remaining = max <= min ? min : min + random.nextFloat() * (max - min);
            item.setDamage(Math.round(maxDurability * (1 - Math.clamp(remaining, 0f, 1f))));
            return item;
        }
    }

    /**
     * Turns a spawn egg into the egg of the given entity.
     *
     * @param entity the entity identifier, such as {@code minecraft:wolf}
     */
    public record SetActorId(@NotNull String entity) implements ItemFunction {

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            Item egg = Item.get(entity + "_spawn_egg");
            if (egg.isNull()) {
                return item;
            }
            egg.setCount(item.getCount());
            return egg;
        }
    }

    /**
     * Makes a suspicious stew give one of the listed effects, picked at random, by setting the aux value of
     * a stew that gives it. Effects no stew gives are skipped; with none left, any stew effect is picked.
     *
     * @param effectIds the numeric ids of the effects to pick from
     */
    public record SetStewEffect(@NotNull List<Integer> effectIds) implements ItemFunction {

        public SetStewEffect {
            effectIds = List.copyOf(effectIds);
        }

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            if (!ItemID.SUSPICIOUS_STEW.equals(item.getId())) {
                return item;
            }
            List<Integer> metas = new ArrayList<>(effectIds.size());
            for (int effectId : effectIds) {
                int meta = ItemSuspiciousStew.getMeta(effectId);
                if (meta >= 0) {
                    metas.add(meta);
                }
            }
            item.setDamage(metas.isEmpty()
                    ? random.nextInt(ItemSuspiciousStew.MAX_META + 1)
                    : metas.get(random.nextInt(metas.size())));
            return item;
        }
    }

    /**
     * Sets the Bad Omen amplifier of an ominous bottle to one drawn from a range.
     */
    public record SetOminousBottleAmplifier(@NotNull NumberRange amplifier) implements ItemFunction {

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            if (ItemID.OMINOUS_BOTTLE.equals(item.getId())) {
                item.setDamage(Math.clamp(amplifier.sample(random), 0, 4));
            }
            return item;
        }
    }

    /**
     * Dyes leather armour a random colour.
     */
    public record RandomDye() implements ItemFunction {

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            if (item instanceof ItemColorArmor armor) {
                DyeColor[] colors = DyeColor.values();
                armor.setColor(colors[random.nextInt(colors.length)]);
            }
            return item;
        }
    }

    /**
     * Adds a number drawn from {@code count} to the item's count once per level of looting on the tool.
     */
    public record LootingEnchant(@NotNull NumberRange count) implements ItemFunction {

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            int levels = lootingLevel(tool);
            int extra = 0;
            for (int i = 0; i < levels; i++) {
                extra += count.sample(random);
            }
            if (extra > 0) {
                item.setCount(Math.min(item.getCount() + extra, item.getMaxStackSize()));
            }
            return item;
        }
    }

    /**
     * Turns a potion, splash potion, lingering potion or tipped arrow into the given potion type.
     *
     * @param potion the potion type identifier, such as {@code minecraft:strong_healing}
     */
    public record SetPotion(@NotNull String potion) implements ItemFunction {

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            PotionType type = PotionType.get(potion.indexOf(':') >= 0 ? potion : "minecraft:" + potion);
            if (type != null) {
                item.setDamage(type.id());
            }
            return item;
        }
    }

    /**
     * With the given chance, enchants the item as equipment a mob spawns with is enchanted: like the
     * enchanting table at a level between 5 and 22, treasure enchantments excluded.
     */
    public record EnchantRandomGear(float chance) implements ItemFunction {

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            if (random.nextFloat() >= chance) {
                return item;
            }
            return new EnchantWithLevels(new NumberRange(5, 22), false).apply(item, random, tool);
        }
    }

    /**
     * Sets a block item's state to a value drawn from a range.
     *
     * @param state  the block state name
     * @param values the range the value is drawn from
     */
    public record RandomBlockState(@NotNull String state, @NotNull NumberRange values) implements ItemFunction {

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            if (!item.canBePlaced()) {
                return item;
            }
            Block block = item.getBlock();
            for (BlockPropertyType<?> type : block.getProperties().getPropertyTypeSet()) {
                if (type.getName().equals(state)) {
                    try {
                        block.setPropertyValue(type.tryCreateValue(values.sample(random)));
                    } catch (IllegalArgumentException e) {
                        return item;
                    }
                    Item result = new ItemBlock(block);
                    result.setCount(item.getCount());
                    return result;
                }
            }
            return item;
        }
    }

    /**
     * Sets a banner's type, base colour and patterns.
     *
     * @param type      0 for a default banner, 1 for the illager captain's
     * @param baseColor the base colour, or null to keep the banner's
     * @param patterns  the patterns, at most 6
     */
    public record SetBannerDetails(int type, @Nullable DyeColor baseColor, @NotNull List<BannerPattern> patterns) implements ItemFunction {

        public SetBannerDetails {
            patterns = List.copyOf(patterns.subList(0, Math.min(6, patterns.size())));
        }

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            if (item instanceof ItemBanner banner) {
                banner.setType(type);
                if (baseColor != null) {
                    banner.setBaseColor(baseColor);
                }
                if (type == 0) {
                    patterns.forEach(banner::addPattern);
                }
            }
            return item;
        }
    }

    /**
     * Writes a written book's author, title and pages.
     */
    public record SetBookContents(@NotNull String author, @NotNull String title, @NotNull List<String> pages) implements ItemFunction {

        public SetBookContents {
            pages = List.copyOf(pages);
        }

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            if (item instanceof ItemWrittenBook book) {
                return book.writeBook(author, title, pages.toArray(new String[0]));
            }
            return item;
        }
    }

    /**
     * Keeps each item of the stack with a chance of one in the radius of the explosion the loot is rolled
     * for. Loot rolled for anything but an explosion keeps the whole stack.
     *
     * @see #generate(Random, Item, double)
     */
    public record ExplosionDecay() implements ItemFunction {

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            return item;
        }

        /**
         * @param radius the explosion radius; at most 1 keeps the whole stack
         * @return {@code item} with the surviving count, which may be 0
         */
        @NotNull Item decay(@NotNull Item item, @NotNull Random random, double radius) {
            if (radius <= 1) {
                return item;
            }
            int kept = 0;
            for (int i = 0; i < item.getCount(); i++) {
                if (random.nextDouble() * radius < 1) {
                    kept++;
                }
            }
            item.setCount(kept);
            return item;
        }
    }

    /**
     * Applies an armour trim.
     *
     * @param material the trim material, such as {@code netherite}
     * @param pattern  the trim pattern, such as {@code wayfinder}
     */
    public record SetArmorTrim(@NotNull String material, @NotNull String pattern) implements ItemFunction {

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            return new ArmorTrim(pattern, material).applyTo(item);
        }
    }

    /**
     * Fills a container item, such as a shulker box, with what another table rolls, each stack in a random
     * slot of 27. Stacks beyond the 27th are dropped. A table that fills containers with itself stops after
     * {@value #MAX_NESTING_DEPTH} levels.
     *
     * @param table the path of the table to roll, as registered in {@link LootTableRegistry}
     */
    public record FillContainer(@NotNull String table) implements ItemFunction {

        private static final int CONTAINER_SLOTS = 27;
        private static final ThreadLocal<int[]> DEPTH = ThreadLocal.withInitial(() -> new int[1]);

        @Override
        public @NotNull Item apply(@NotNull Item item, @NotNull Random random, @Nullable Item tool) {
            LootTable contents = LootTableRegistry.get(table);
            int[] depth = DEPTH.get();
            if (contents == null || depth[0] >= MAX_NESTING_DEPTH) {
                return item;
            }
            List<Item> stacks;
            depth[0]++;
            try {
                stacks = contents.generate(random, tool);
            } finally {
                depth[0]--;
            }
            List<Integer> slots = new ArrayList<>(CONTAINER_SLOTS);
            for (int slot = 0; slot < CONTAINER_SLOTS; slot++) {
                slots.add(slot);
            }
            Collections.shuffle(slots, random);
            ListTag<CompoundTag> items = new ListTag<>(Tag.TAG_Compound);
            for (int i = 0; i < stacks.size() && i < CONTAINER_SLOTS; i++) {
                items.add(ItemHelper.write(stacks.get(i), slots.get(i)));
            }
            CompoundTag nbt = item.getOrCreateNbt();
            nbt.putList("Items", items);
            item.setNbt(nbt);
            return item;
        }
    }

    /**
     * Enchanting a plain book gives an enchanted book, as in Bedrock.
     */
    private static Item enchantable(Item item) {
        if (!ItemID.BOOK.equals(item.getId())) {
            return item;
        }
        Item book = Item.get(ItemID.ENCHANTED_BOOK);
        book.setCount(item.getCount());
        return book;
    }

    private static @Nullable Enchantment findEnchantment(String identifier) {
        try {
            return Enchantment.getEnchantment(identifier);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static int lootingLevel(@Nullable Item tool) {
        return tool == null || tool.isNull() ? 0 : tool.getEnchantmentLevel(Enchantment.ID_LOOTING);
    }

    /**
     * Picks entries by tier instead of weight: the entries are ordered from lowest to highest tier, a roll
     * starts at a random tier below {@code initialRange}, and each of {@code bonusRolls} attempts moves it up
     * one tier with probability {@code bonusChance}, up to the last entry.
     */
    public record Tiers(int initialRange, int bonusRolls, float bonusChance) {
    }

    /**
     * @param rolls      how many entries to pick per generation
     * @param conditions requirements for the pool to be rolled at all; empty when it always rolls
     * @param entries    the candidates; an empty list makes the pool generate nothing
     * @param tiers      how entries are picked by tier, or null to pick them by weight
     */
    public record Pool(NumberRange rolls, List<Condition> conditions, List<Entry> entries, @Nullable Tiers tiers) {

        public Pool {
            conditions = List.copyOf(conditions);
            entries = List.copyOf(entries);
        }

        public Pool(NumberRange rolls, List<Condition> conditions, List<Entry> entries) {
            this(rolls, conditions, entries, null);
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
     * @param functions   applied in order to the item an item entry yields
     */
    public record Entry(
            EntryType type,
            String name,
            int weight,
            NumberRange count,
            @Nullable Integer data,
            @Nullable String displayName,
            @Nullable List<String> lore,
            List<Condition> conditions,
            List<ItemFunction> functions
    ) {

        public Entry {
            lore = lore == null ? null : List.copyOf(lore);
            conditions = List.copyOf(conditions);
            functions = List.copyOf(functions);
        }

        public Entry(EntryType type, String name, int weight, NumberRange count, @Nullable Integer data,
                     @Nullable String displayName, @Nullable List<String> lore, List<Condition> conditions) {
            this(type, name, weight, count, data, displayName, lore, conditions, List.of());
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
        return generate(random, tool, 0);
    }

    /**
     * Rolls every pool of this table for a block an explosion destroyed, so {@link ExplosionDecay} thins
     * out the stacks.
     *
     * @param random          the random source to draw from
     * @param tool            the item the loot is generated for, as tool conditions see it; null for an
     *                        empty hand
     * @param explosionRadius the radius of the explosion; 0 when the loot is not rolled for one
     * @return the generated stacks, empty when nothing was rolled or survived; never null
     * @see #generate(Random, Item)
     */
    public @NotNull List<Item> generate(@NotNull Random random, @Nullable Item tool, double explosionRadius) {
        List<Item> result = new ArrayList<>();
        generateInto(random, tool, explosionRadius, result, 0);
        return result;
    }

    private void generateInto(Random random, @Nullable Item tool, double explosionRadius, List<Item> result, int depth) {
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
                Entry entry = pool.tiers() != null ? pickTiered(pool, random, tool) : pickWeighted(pool.entries(), random, tool);
                if (entry == null) {
                    continue;
                }
                switch (entry.type()) {
                    case EMPTY -> {
                    }
                    case ITEM -> {
                        Item item = createItem(entry, random, tool, explosionRadius);
                        if (item != null && !item.isNull() && item.getCount() > 0) {
                            result.add(item);
                        }
                    }
                    case LOOT_TABLE -> {
                        LootTable nested = LootTableRegistry.get(entry.name());
                        if (nested != null) {
                            nested.generateInto(random, tool, explosionRadius, result, depth + 1);
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

    private static @Nullable Entry pickTiered(Pool pool, Random random, @Nullable Item tool) {
        List<Entry> entries = pool.entries();
        Tiers tiers = pool.tiers();
        if (entries.isEmpty()) {
            return null;
        }
        int tier = random.nextInt(Math.max(1, tiers.initialRange()));
        for (int i = 0; i < tiers.bonusRolls(); i++) {
            if (random.nextFloat() < tiers.bonusChance()) {
                tier++;
            }
        }
        Entry entry = entries.get(Math.min(tier, entries.size() - 1));
        return allHold(entry.conditions(), random, tool) ? entry : null;
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

    private static @Nullable Item createItem(Entry entry, Random random, @Nullable Item tool, double explosionRadius) {
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
        for (ItemFunction function : entry.functions()) {
            item = function instanceof ExplosionDecay decay
                    ? decay.decay(item, random, explosionRadius)
                    : function.apply(item, random, tool);
        }
        return item;
    }
}
