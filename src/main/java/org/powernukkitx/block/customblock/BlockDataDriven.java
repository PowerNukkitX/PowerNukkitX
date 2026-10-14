package org.powernukkitx.block.customblock;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.type.BlockPropertyType;
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

/**
 * A block that exists only as data, with no class of its own.
 * <p>
 * Blocks defined by a behaviour pack have no dedicated Java class, so one instance of this class
 * backs one identifier and takes everything from the {@link Spec} the pack parser built. Gameplay
 * values live in the {@link CustomBlockDefinition}, handed to the {@link Block} core the same way a
 * hand-written custom block does, so its getters read plain fields.
 * <p>
 * Conditional tags are resolved for every state when the spec is built, never per query, so
 * {@link #hasTag(String)} costs one map lookup.
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
     * Everything the pack parser derived for one block identifier.
     *
     * @param properties  the block's state definition
     * @param definition  the definition backing the property getters and sent to clients
     * @param baseTags    the tags the block carries in every state
     * @param tagsByState the full tag set of each state that carries more than {@code baseTags},
     *                    keyed by block state hash; states not present carry exactly {@code baseTags}
     */
    public record Spec(
            @NotNull BlockProperties properties,
            @NotNull CustomBlockDefinition definition,
            @NotNull Set<String> baseTags,
            @NotNull Int2ObjectMap<Set<String>> tagsByState
    ) {

        public Spec {
            baseTags = Set.copyOf(baseTags);
            tagsByState = Int2ObjectMaps.unmodifiable(tagsByState);
        }

        /**
         * Builds a spec, evaluating every tag rule against every state of {@code properties} once.
         * States that end up with the same tags share one set.
         *
         * @param rules state-conditional tags; empty when the block has none
         */
        public static @NotNull Spec of(@NotNull BlockProperties properties, @NotNull CustomBlockDefinition definition,
                                       @NotNull Set<String> baseTags, @NotNull List<TagRule> rules) {
            if (rules.isEmpty()) {
                return new Spec(properties, definition, baseTags, Int2ObjectMaps.emptyMap());
            }
            Map<Set<String>, Set<String>> shared = new HashMap<>();
            Int2ObjectOpenHashMap<Set<String>> byState = new Int2ObjectOpenHashMap<>();
            for (BlockState state : properties.getSpecialValueMap().values()) {
                Set<String> tags = new HashSet<>(baseTags);
                MolangContext context = new StateContext(state);
                for (TagRule rule : rules) {
                    if (rule.condition().evaluateAsBoolean(context)) {
                        tags.addAll(rule.tags());
                    }
                }
                if (tags.size() > baseTags.size()) {
                    byState.put(state.blockStateHash(), shared.computeIfAbsent(Set.copyOf(tags), key -> key));
                }
            }
            byState.trim();
            return new Spec(properties, definition, baseTags, byState);
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

    private Set<String> currentTags() {
        Set<String> tags = spec.tagsByState().get(this.blockstate.blockStateHash());
        return tags != null ? tags : spec.baseTags();
    }
}
