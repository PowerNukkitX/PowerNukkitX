package org.powernukkitx.block.customblock;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.item.Item;
import org.powernukkitx.loot.LootTable;
import org.powernukkitx.loot.LootTableRegistry;
import org.powernukkitx.molang.MolangContext;
import org.powernukkitx.molang.MolangExpression;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A block that exists only as data, with no class of its own.
 * <p>
 * Blocks defined by a behaviour pack have no dedicated Java class, so one instance of this class
 * backs one identifier and takes everything from the {@link Spec} the pack parser built. Gameplay
 * values live in the {@link CustomBlockDefinition}, handed to the {@link Block} core the same way a
 * hand-written custom block does, so its getters read plain fields.
 * <p>
 * Conditional tags and loot tables are resolved for every state when the spec is built, never
 * per query, so {@link #hasTag(String)} and {@link #getDrops(Item)} cost one map lookup each.
 */
public class BlockDataDriven extends Block implements CustomBlock {

    /**
     * Tags a block carries only in some of its states, as a behaviour pack expresses them: a
     * permutation applies its tags while its Molang condition holds.
     *
     * @param condition evaluated against each state when the {@link Spec} is built
     * @param tags      the tags the block carries while the condition holds
     */
    public record TagRule(@NotNull MolangExpression condition, @NotNull List<String> tags) {

        public TagRule {
            tags = List.copyOf(tags);
        }
    }

    /**
     * A loot table a block uses only in some of its states, as a behaviour pack expresses it: a
     * permutation's {@code minecraft:loot} replaces the block's table while its condition holds.
     *
     * @param condition evaluated against each state when the {@link Spec} is built
     * @param table     the loot table path used while the condition holds
     */
    public record LootRule(@NotNull MolangExpression condition, @NotNull String table) {
    }

    /**
     * Everything the pack parser derived for one block identifier.
     *
     * @param properties  the block's state definition
     * @param definition  the definition backing the property getters and sent to clients
     * @param baseTags    the tags the block carries in every state
     * @param tagsByState the full tag set of each state that carries more than {@code baseTags},
     *                    keyed by block state hash; states not present carry exactly {@code baseTags}
     * @param lootTable   the loot table path the block drops from, or null to drop itself
     * @param lootTableByState the loot table path of each state whose permutations replace
     *                         {@code lootTable}, keyed by block state hash
     */
    public record Spec(
            @NotNull BlockProperties properties,
            @NotNull CustomBlockDefinition definition,
            @NotNull Set<String> baseTags,
            @NotNull Int2ObjectMap<Set<String>> tagsByState,
            @Nullable String lootTable,
            @NotNull Int2ObjectMap<String> lootTableByState
    ) {

        public Spec {
            baseTags = Set.copyOf(baseTags);
            tagsByState = Int2ObjectMaps.unmodifiable(tagsByState);
            lootTableByState = Int2ObjectMaps.unmodifiable(lootTableByState);
        }

        /**
         * Builds a spec, evaluating every tag and loot rule against every state of
         * {@code properties} once. States that end up with the same tags share one set; when
         * several loot rules hold for a state, the last one wins.
         *
         * @param tagRules  state-conditional tags; empty when the block has none
         * @param lootTable the block's own loot table path, or null to drop itself
         * @param lootRules state-conditional loot tables; empty when the block has none
         */
        public static @NotNull Spec of(@NotNull BlockProperties properties, @NotNull CustomBlockDefinition definition,
                                       @NotNull Set<String> baseTags, @NotNull List<TagRule> tagRules,
                                       @Nullable String lootTable, @NotNull List<LootRule> lootRules) {
            if (tagRules.isEmpty() && lootRules.isEmpty()) {
                return new Spec(properties, definition, baseTags, Int2ObjectMaps.emptyMap(), lootTable, Int2ObjectMaps.emptyMap());
            }
            Map<Set<String>, Set<String>> shared = new HashMap<>();
            Int2ObjectOpenHashMap<Set<String>> tagsByState = new Int2ObjectOpenHashMap<>();
            Int2ObjectOpenHashMap<String> lootByState = new Int2ObjectOpenHashMap<>();
            for (BlockState state : properties.getSpecialValueMap().values()) {
                MolangContext context = new StateContext(state);
                if (!tagRules.isEmpty()) {
                    Set<String> tags = new HashSet<>(baseTags);
                    for (TagRule rule : tagRules) {
                        if (rule.condition().evaluateAsBoolean(context)) {
                            tags.addAll(rule.tags());
                        }
                    }
                    if (tags.size() > baseTags.size()) {
                        tagsByState.put(state.blockStateHash(), shared.computeIfAbsent(Set.copyOf(tags), key -> key));
                    }
                }
                String table = lootTable;
                for (LootRule rule : lootRules) {
                    if (rule.condition().evaluateAsBoolean(context)) {
                        table = rule.table();
                    }
                }
                if (table != null && !table.equals(lootTable)) {
                    lootByState.put(state.blockStateHash(), table);
                }
            }
            tagsByState.trim();
            lootByState.trim();
            return new Spec(properties, definition, baseTags, tagsByState, lootTable, lootByState);
        }
    }

    /**
     * Answers {@code query.block_state('name')} from a block state, which is what permutation
     * conditions are written against.
     */
    private record StateContext(BlockState state) implements MolangContext {

        @Override
        public @Nullable Object query(@NotNull String name, @NotNull Object[] arguments) {
            if (arguments.length != 1 || !("block_state".equals(name) || "block_property".equals(name))) {
                return null;
            }
            String stateName = String.valueOf(arguments[0]);
            for (BlockPropertyType.BlockPropertyValue<?, ?, ?> value : state.getBlockPropertyValues()) {
                if (value.getPropertyType().getName().equals(stateName)) {
                    Object raw = value.getValue();
                    return raw instanceof Enum<?> enumValue ? enumValue.name().toLowerCase(Locale.ENGLISH) : raw;
                }
            }
            return null;
        }
    }

    private final Spec spec;

    public BlockDataDriven(@Nullable BlockState blockState, @NotNull Spec spec) {
        super(blockState, spec.properties(), spec.definition());
        this.spec = spec;
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return spec.properties();
    }

    @Override
    public CustomBlockDefinition getDefinition() {
        return spec.definition();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Includes the tags of the permutations whose condition holds in the block's current state.
     */
    @Override
    public boolean hasTag(String blockTag) {
        return currentTags().contains(blockTag);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Includes the tags of the permutations whose condition holds in the block's current state.
     */
    @Override
    public String[] getTags() {
        return currentTags().toArray(new String[0]);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Rolls the block's loot table for its current state when the pack declares one, passing the
     * tool on to the table's tool conditions. A table that is declared but not loaded drops
     * nothing, as in Bedrock; a block without any table drops itself.
     */
    @Override
    public Item[] getDrops(Item item) {
        String table = spec.lootTableByState().get(this.blockstate.blockStateHash());
        if (table == null) {
            table = spec.lootTable();
        }
        if (table == null) {
            return super.getDrops(item);
        }
        LootTable loot = LootTableRegistry.get(table);
        if (loot == null) {
            return Item.EMPTY_ARRAY;
        }
        return loot.generate(ThreadLocalRandom.current(), item).toArray(Item.EMPTY_ARRAY);
    }

    private Set<String> currentTags() {
        Set<String> tags = spec.tagsByState().get(this.blockstate.blockStateHash());
        return tags != null ? tags : spec.baseTags();
    }
}
