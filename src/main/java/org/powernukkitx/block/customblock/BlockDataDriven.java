package org.powernukkitx.block.customblock;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
import org.powernukkitx.block.BlockFlowerPot;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.block.BlockNoteblock;
import org.powernukkitx.block.BlockState;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.Level;
import org.powernukkitx.loot.LootTable;
import org.powernukkitx.loot.LootTableRegistry;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.molang.MolangContext;
import org.powernukkitx.molang.MolangExpression;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.nbt.tag.ListTag;
import org.powernukkitx.nbt.tag.Tag;
import org.powernukkitx.registry.ItemRegistry;
import org.powernukkitx.utils.BlockColor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A block that exists only as data, with no class of its own.
 * <p>
 * Blocks defined by a behaviour pack have no dedicated Java class, so one instance of this class
 * backs one identifier and takes everything from the {@link Spec} the pack parser built. Gameplay
 * values live in a {@link CustomBlockDefinition}, handed to the {@link Block} core the same way a
 * hand-written custom block does, so its getters read plain fields.
 * <p>
 * Everything a pack makes depend on the block's state - tags, loot tables, and the gameplay values
 * and collision boxes its permutations change - is resolved for every state when the spec is built,
 * never per query. A state whose permutations change gameplay gets a definition of its own, so the
 * {@link Block} getters stay plain field reads and {@link #hasTag(String)}, {@link #getDrops(Item)}
 * and the collision queries cost one map lookup at most.
 */
public class BlockDataDriven extends Block implements CustomBlock, BlockFlowerPot.FlowerPotBlock {

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
     * What a permutation changes on the server while its condition holds.
     *
     * @param condition    evaluated against each state when the {@link Spec} is built
     * @param components   the permutation's components in the client schema, layered over the base
     *                     components of the states the condition holds for; collision boxes and
     *                     transformations are read from them
     * @param serverValues applied, in order, to a builder seeded from the base definition, to set the
     *                     gameplay values the permutation changes
     * @param redstoneProducer the redstone signal the block produces while the condition holds, or null to
     *                         keep the block's own
     */
    public record PermutationRule(@NotNull MolangExpression condition, @NotNull CompoundTag components,
                                  @NotNull List<Consumer<CustomBlockDefinition.Builder>> serverValues,
                                  @Nullable RedstoneProducer redstoneProducer) {

        public PermutationRule {
            serverValues = List.copyOf(serverValues);
        }
    }

    /**
     * The states a block's traits set when it is placed, as {@code minecraft:placement_direction} and
     * {@code minecraft:placement_position} declare them.
     *
     * @param cardinalDirection sets {@code minecraft:cardinal_direction} to the horizontal direction the
     *                          placing player faces
     * @param facingDirection   sets {@code minecraft:facing_direction} to the direction the placing player
     *                          faces, up and down included
     * @param yRotationOffset   degrees, a multiple of 90, the horizontal directions are turned
     *                          counter-clockwise by, so 90 stores {@code east} for a player facing south
     * @param blockFace         sets {@code minecraft:block_face} to the face the block was placed against
     * @param verticalHalf      sets {@code minecraft:vertical_half} to the half of the clicked block face
     */
    public record TraitPlacement(boolean cardinalDirection, boolean facingDirection, int yRotationOffset,
                                 boolean blockFace, boolean verticalHalf) {

        public static final TraitPlacement NONE = new TraitPlacement(false, false, 0, false, false);
    }

    /**
     * The support shape {@code minecraft:support} gives a block, which decides what can attach to it.
     */
    public enum SupportShape {
        /** Supports attachments on its top only, like a vanilla fence. */
        FENCE,
        /** Full on the half its {@code minecraft:vertical_half} names and on its back, like vanilla stairs. */
        STAIR
    }

    /**
     * How a block directly above a chest affects whether the chest opens, from {@code minecraft:chest_obstruction}.
     */
    public enum ChestObstruction {
        /** The chest never opens. */
        ALWAYS,
        /** The chest always opens. */
        NEVER,
        /** The block's collision box decides, as for vanilla blocks. */
        SHAPE
    }

    /**
     * Which blocks may connect to a block, from {@code minecraft:connection_rule}.
     */
    public enum ConnectionSource {
        ALL,
        /** Fences only, so walls and panes do not connect. */
        ONLY_FENCES,
        NONE
    }

    /**
     * @param accepts    which blocks may connect
     * @param directions the block's own sides connections are accepted on
     */
    public record ConnectionRule(@NotNull ConnectionSource accepts, @NotNull Set<BlockFace> directions) {

        public ConnectionRule {
            directions = directions.isEmpty() ? EnumSet.noneOf(BlockFace.class) : EnumSet.copyOf(directions);
        }
    }

    /**
     * The redstone signal a block produces, from {@code minecraft:redstone_producer}. Faces are the block's
     * own, before its transformation.
     *
     * @param power               the signal strength, 0 to 15
     * @param stronglyPoweredFace the face whose neighbour is strongly powered, or null for none
     * @param connectedFaces      the faces that power their neighbour
     * @param transformRelative   whether the faces turn with the block's {@code minecraft:transformation}
     */
    public record RedstoneProducer(int power, @Nullable BlockFace stronglyPoweredFace,
                                   @NotNull Set<BlockFace> connectedFaces, boolean transformRelative) {

        public RedstoneProducer {
            connectedFaces = connectedFaces.isEmpty() ? EnumSet.noneOf(BlockFace.class) : EnumSet.copyOf(connectedFaces);
        }
    }

    /**
     * A destroy time that replaces the block's own while it is mined with a matching item, from
     * {@code minecraft:destructible_by_mining}'s {@code item_specific_speeds}.
     *
     * @param item    which items the time applies to
     * @param seconds the destroy time, used where the block's {@code seconds_to_destroy} would be
     */
    public record ItemSpeed(@NotNull Predicate<Item> item, float seconds) {

        /**
         * @param identifier the item identifier the time applies to
         */
        public static ItemSpeed ofItem(@NotNull String identifier, float seconds) {
            return new ItemSpeed(item -> identifier.equals(item.getId()), seconds);
        }

        /**
         * @param tags a Molang expression over the item's tags, such as {@code q.any_tag('minecraft:is_pickaxe')}
         */
        public static ItemSpeed ofTags(@NotNull MolangExpression tags, float seconds) {
            return new ItemSpeed(item -> tags.evaluateAsBoolean(new TagQueryContext(item::hasTag)), seconds);
        }
    }

    /**
     * The note block instruments a block sounds, from {@code minecraft:instrument_sound}.
     *
     * @param up   the instrument when the block is under a note block, or null for no sound
     * @param down the instrument when the block is on top of a note block, or null to leave the choice to the
     *             block under the note block
     */
    public record InstrumentSound(BlockNoteblock.@Nullable Instrument up, BlockNoteblock.@Nullable Instrument down) {
    }

    /**
     * A block made of several parts that act as one, from the {@code minecraft:multi_block} trait. Part 0
     * is where the block was placed; the others follow it in {@code direction}.
     *
     * @param parts     how many parts, 2 to 4
     * @param direction where the parts extend, unless the block's {@code minecraft:cardinal_direction}
     *                  overrides a horizontal direction
     */
    public record MultiBlock(int parts, @NotNull BlockFace direction) {
    }

    /**
     * How water treats the block beyond whether it flows into it, from {@code minecraft:liquid_detection}.
     *
     * @param stopsFlowingFrom the faces water held in the block cannot flow out of; with {@code noReaction}
     *                         also the faces water cannot flow in through
     * @param noReaction       whether water flows through the block without affecting it
     * @param broken           whether water destroys the block without dropping its loot
     */
    public record LiquidRule(@NotNull Set<BlockFace> stopsFlowingFrom, boolean noReaction, boolean broken) {

        public LiquidRule {
            stopsFlowingFrom = stopsFlowingFrom.isEmpty() ? Set.of() : Set.copyOf(stopsFlowingFrom);
        }
    }

    /**
     * What a block does on the server whatever its state.
     *
     * @param instrumentSound   the note block instruments it sounds, or null for the default
     * @param placementFilter   where the block may be placed, or null for anywhere
     * @param traitPlacement    the states the block's traits set when it is placed
     * @param itemSpeeds        destroy times for particular items, checked in order
     * @param support           the support shape, or null for a full cube
     * @param chestObstruction  how the block affects a chest below it
     * @param flowerPottable    whether the block can be put in a flower pot
     * @param redstoneConductor whether redstone powers the block, or null to decide by its shape as vanilla does
     * @param connectionRule    which blocks may connect to it, or null for the vanilla rules
     * @param redstoneProducer  the redstone signal it produces in states no permutation changes, or null for none
     * @param multiBlock        the parts the block is made of, or null for a single block
     * @param cardinalConnections whether the {@code minecraft:connection} trait keeps its
     *                          {@code minecraft:connection_*} states up to date
     * @param cornerWith        the blocks the {@code minecraft:corner} state forms corners with, empty for blocks
     *                          of the same identifier, or null when the block does not form corners
     * @param liquid            how water treats the block, or null for the defaults
     * @param wireStepsDown     whether redstone wire on top of the block connects down its sides
     * @param mapTint           the biome tint maps multiply the block's map colour by
     */
    public record Behavior(@Nullable InstrumentSound instrumentSound,
                           @Nullable PlacementFilter placementFilter, @NotNull TraitPlacement traitPlacement,
                           @NotNull List<ItemSpeed> itemSpeeds, @Nullable SupportShape support,
                           @NotNull ChestObstruction chestObstruction, boolean flowerPottable,
                           @Nullable Boolean redstoneConductor, @Nullable ConnectionRule connectionRule,
                           @Nullable RedstoneProducer redstoneProducer, @Nullable MultiBlock multiBlock,
                           boolean cardinalConnections, @Nullable List<PlacementFilter.BlockMatcher> cornerWith,
                           @Nullable LiquidRule liquid, boolean wireStepsDown, @NotNull BlockColor.Tint mapTint) {

        public static final Behavior DEFAULT = new Behavior(null, null, TraitPlacement.NONE, List.of(), null,
                ChestObstruction.SHAPE, false, null, null, null, null, false, null, null, true, BlockColor.Tint.NONE);

        public Behavior {
            itemSpeeds = List.copyOf(itemSpeeds);
            cornerWith = cornerWith == null ? null : List.copyOf(cornerWith);
        }
    }

    /**
     * The server's view of one state, shared by every state the same permutations apply to.
     *
     * @param definition       the definition backing the property getters in this state
     * @param collisionBoxes   the collision boxes in block units relative to the block's corner, after
     *                         its transformation, as {@code [minX, minY, minZ, maxX, maxY, maxZ]}; empty
     *                         when collision is disabled, null when the block declares no collision box
     *                         and collides as a full cube
     * @param bounds           the union of {@code collisionBoxes}, or null when there are none
     * @param redstoneProducer the redstone signal produced in this state, or null for none
     * @param quarterTurnsY    the quarter turns about the vertical axis the state's transformation applies
     */
    public record StateData(@NotNull CustomBlockDefinition definition, float @Nullable [][] collisionBoxes,
                            float @Nullable [] bounds, @Nullable RedstoneProducer redstoneProducer,
                            int quarterTurnsY) {

        static StateData of(CustomBlockDefinition definition, @Nullable RedstoneProducer redstoneProducer) {
            CompoundTag components = definition.getComponents();
            float[][] boxes = readCollisionBoxes(components);
            int turns = components.contains("minecraft:transformation")
                    ? components.getCompound("minecraft:transformation").getInt("RY") : 0;
            return new StateData(definition, boxes, boxes == null ? null : union(boxes), redstoneProducer, turns);
        }
    }

    /**
     * Everything the pack parser derived for one block identifier.
     *
     * @param properties       the block's state definition
     * @param definition       the definition registered for the identifier and sent to clients
     * @param baseTags         the tags the block carries in every state
     * @param tagsByState      the full tag set of each state that carries more than {@code baseTags},
     *                         keyed by block state hash; states not present carry exactly {@code baseTags}
     * @param lootTable        the loot table path the block drops from, or null to drop itself
     * @param lootTableByState the loot table path of each state whose permutations replace
     *                         {@code lootTable}, keyed by block state hash
     * @param baseState        the server's view of every state no permutation changes
     * @param stateData        the server's view of each state some permutation changes, keyed by block
     *                         state hash
     * @param behavior         what the block does whatever its state
     */
    public record Spec(
            @NotNull BlockProperties properties,
            @NotNull CustomBlockDefinition definition,
            @NotNull Set<String> baseTags,
            @NotNull Int2ObjectMap<Set<String>> tagsByState,
            @Nullable String lootTable,
            @NotNull Int2ObjectMap<String> lootTableByState,
            @NotNull StateData baseState,
            @NotNull Int2ObjectMap<StateData> stateData,
            @NotNull Behavior behavior
    ) {

        public Spec {
            baseTags = Set.copyOf(baseTags);
            tagsByState = Int2ObjectMaps.unmodifiable(tagsByState);
            lootTableByState = Int2ObjectMaps.unmodifiable(lootTableByState);
            stateData = Int2ObjectMaps.unmodifiable(stateData);
        }

        /**
         * @return the server's view of the given state
         */
        public @NotNull StateData stateData(@NotNull BlockState state) {
            StateData data = stateData.get(state.blockStateHash());
            return data != null ? data : baseState;
        }

        /**
         * Builds a spec, evaluating every rule against every state of {@code properties} once. States
         * that end up with the same tags share one set and states the same permutation rules apply to
         * share one {@link StateData}; when several loot rules hold for a state, the last one wins, and
         * permutation rules apply in order, so a later one overrides an earlier one.
         *
         * @param tagRules         state-conditional tags; empty when the block has none
         * @param lootTable        the block's own loot table path, or null to drop itself
         * @param lootRules        state-conditional loot tables; empty when the block has none
         * @param permutationRules state-conditional gameplay values; empty when the block has none
         * @param behavior         what the block does whatever its state
         */
        public static @NotNull Spec of(@NotNull BlockProperties properties, @NotNull CustomBlockDefinition definition,
                                       @NotNull Set<String> baseTags, @NotNull List<TagRule> tagRules,
                                       @Nullable String lootTable, @NotNull List<LootRule> lootRules,
                                       @NotNull List<PermutationRule> permutationRules,
                                       @NotNull Behavior behavior) {
            StateData baseState = StateData.of(definition, behavior.redstoneProducer());
            if (tagRules.isEmpty() && lootRules.isEmpty() && permutationRules.isEmpty()) {
                return new Spec(properties, definition, baseTags, Int2ObjectMaps.emptyMap(), lootTable,
                        Int2ObjectMaps.emptyMap(), baseState, Int2ObjectMaps.emptyMap(), behavior);
            }
            Map<Set<String>, Set<String>> sharedTags = new HashMap<>();
            Map<BitSet, StateData> sharedStates = new HashMap<>();
            Int2ObjectOpenHashMap<Set<String>> tagsByState = new Int2ObjectOpenHashMap<>();
            Int2ObjectOpenHashMap<String> lootByState = new Int2ObjectOpenHashMap<>();
            Int2ObjectOpenHashMap<StateData> dataByState = new Int2ObjectOpenHashMap<>();
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
                        tagsByState.put(state.blockStateHash(), sharedTags.computeIfAbsent(Set.copyOf(tags), key -> key));
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
                BitSet matched = new BitSet(permutationRules.size());
                for (int i = 0; i < permutationRules.size(); i++) {
                    if (permutationRules.get(i).condition().evaluateAsBoolean(context)) {
                        matched.set(i);
                    }
                }
                if (!matched.isEmpty()) {
                    dataByState.put(state.blockStateHash(), sharedStates.computeIfAbsent(matched,
                            key -> StateData.of(stateDefinition(properties, definition, permutationRules, key),
                                    producer(permutationRules, key, behavior.redstoneProducer()))));
                }
            }
            tagsByState.trim();
            lootByState.trim();
            dataByState.trim();
            return new Spec(properties, definition, baseTags, tagsByState, lootTable, lootByState, baseState,
                    dataByState, behavior);
        }

        private static @Nullable RedstoneProducer producer(List<PermutationRule> rules, BitSet matched,
                                                           @Nullable RedstoneProducer base) {
            RedstoneProducer producer = base;
            for (int i = matched.nextSetBit(0); i >= 0; i = matched.nextSetBit(i + 1)) {
                if (rules.get(i).redstoneProducer() != null) {
                    producer = rules.get(i).redstoneProducer();
                }
            }
            return producer;
        }

        private static CustomBlockDefinition stateDefinition(BlockProperties properties, CustomBlockDefinition base,
                                                             List<PermutationRule> rules, BitSet matched) {
            CompoundTag components = new CompoundTag(base.getComponents().getTags());
            for (int i = matched.nextSetBit(0); i >= 0; i = matched.nextSetBit(i + 1)) {
                for (Map.Entry<String, Tag> entry : rules.get(i).components().getTags().entrySet()) {
                    components.put(entry.getKey(), entry.getValue());
                }
            }
            CompoundTag nbt = new CompoundTag(base.nbt().getTags()).putCompound("components", components);
            StateDefinitionBuilder builder = new StateDefinitionBuilder(properties, base, nbt);
            for (int i = matched.nextSetBit(0); i >= 0; i = matched.nextSetBit(i + 1)) {
                for (Consumer<CustomBlockDefinition.Builder> value : rules.get(i).serverValues()) {
                    value.accept(builder);
                }
            }
            return builder.build();
        }
    }

    /**
     * Seeds a definition with another one's gameplay values, tick settings and NBT, so a permutation
     * only has to apply what it changes.
     */
    private static final class StateDefinitionBuilder extends CustomBlockDefinition.Builder {

        private StateDefinitionBuilder(BlockProperties properties, CustomBlockDefinition base, CompoundTag nbt) {
            super(properties);
            this.$fillValuesFrom(base);
            if (base.tickSettings() != null) {
                this.blockTick(base.tickSettings().minTicks(), base.tickSettings().maxTicks(), base.tickSettings().looping());
            }
            this.nbt = nbt;
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
    private StateData stateData;

    public BlockDataDriven(@Nullable BlockState blockState, @NotNull Spec spec) {
        super(blockState, spec.properties(), spec.definition());
        this.spec = spec;
        refreshStateData();
    }

    private void refreshStateData() {
        this.stateData = spec.stateData(this.blockstate);
        this.definition = stateData.definition();
    }

    @Override
    public @NotNull BlockProperties getProperties() {
        return spec.properties();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The definition registered for the identifier and sent to clients, whatever the block's state. The
     * values the block's current state applies are those of {@link #getCustomDefinition()}.
     */
    @Override
    public CustomBlockDefinition getDefinition() {
        return spec.definition();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The definition of the block's current state, so the tick settings and collision boxes its
     * permutations declare take effect.
     */
    @Override
    public CustomBlockDefinition getCustomDefinition() {
        return stateData.definition();
    }

    @Override
    public Block setPropertyValues(BlockPropertyType.BlockPropertyValue<?, ?, ?>... values) {
        super.setPropertyValues(values);
        refreshStateData();
        return this;
    }

    @Override
    public Block setPropertyValues(List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> values) {
        super.setPropertyValues(values);
        refreshStateData();
        return this;
    }

    @Override
    public <DATATYPE, PROPERTY extends BlockPropertyType<DATATYPE>> Block setPropertyValue(PROPERTY property, DATATYPE value) {
        super.setPropertyValue(property, value);
        refreshStateData();
        return this;
    }

    @Override
    public Block setPropertyValue(BlockPropertyType.BlockPropertyValue<?, ?, ?> propertyValue) {
        super.setPropertyValue(propertyValue);
        refreshStateData();
        return this;
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
     * The item a pack registered to replace this block's item, when there is one, so the block drops and
     * is picked as that item.
     */
    @Override
    public Item toItem() {
        if (ItemRegistry.getCustomItemDefinitionByIdStatic(getId()) != null) {
            return Item.get(getId());
        }
        return super.toItem();
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
        return getDrops(item, 0);
    }

    /**
     * The drops when an explosion destroys the block. A pack loot table's {@code explosion_decay} keeps
     * each item with a chance of one in {@code explosionRadius}; a block without a table drops as
     * {@link #getDrops(Item)} does.
     *
     * @param item            the tool the drops are rolled for
     * @param explosionRadius the radius of the explosion
     * @return the drops, empty when nothing survived
     */
    public Item[] getExplosionDrops(Item item, double explosionRadius) {
        return getDrops(item, explosionRadius);
    }

    /**
     * {@inheritDoc}
     * <p>
     * A {@code tint_method} on the pack's {@code minecraft:map_color} multiplies the colour by that tint of
     * the biome the block is in.
     */
    @Override
    public BlockColor getColor() {
        BlockColor color = super.getColor();
        BlockColor.Tint tint = spec.behavior().mapTint();
        if (tint == BlockColor.Tint.NONE) {
            return color;
        }
        BlockColor biome = new BlockColor(0, 0, 0);
        biome.applyTint(level.getBiomeId(getFloorX(), getFloorY(), getFloorZ()), tint);
        return new BlockColor(color.getRed() * biome.getRed() / 255, color.getGreen() * biome.getGreen() / 255,
                color.getBlue() * biome.getBlue() / 255, color.getAlpha());
    }

    private Item[] getDrops(Item item, double explosionRadius) {
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
        return loot.generate(ThreadLocalRandom.current(), item, explosionRadius).toArray(Item.EMPTY_ARRAY);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Refused when the pack's placement filter does not accept {@code target} and {@code face}.
     * Otherwise the states the block's traits declare are set from the placement first, the way the
     * client predicts them.
     */
    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face,
                         double fx, double fy, double fz, @Nullable Player player) {
        PlacementFilter filter = spec.behavior().placementFilter();
        if (filter != null && !filter.allowsPlacement(target, face)) {
            return false;
        }
        applyTraits(face, fy, player);
        updateNeighbourStates();
        MultiBlock multiBlock = spec.behavior().multiBlock();
        if (multiBlock == null) {
            return super.place(item, block, target, face, fx, fy, fz, player);
        }
        BlockFace direction = multiBlockDirection(multiBlock);
        for (int part = 1; part < multiBlock.parts(); part++) {
            if (!getSide(direction, part).canBeReplaced()) {
                return false;
            }
        }
        if (!super.place(item, block, target, face, fx, fy, fz, player)) {
            return false;
        }
        for (int part = 1; part < multiBlock.parts(); part++) {
            Block partBlock = this.clone();
            partBlock.setPropertyValue(stateType(MULTI_BLOCK_PART).tryCreateValue(part));
            getLevel().setBlock(getSide(direction, part), partBlock, true, true);
        }
        return true;
    }

    private static final String MULTI_BLOCK_PART = "minecraft:multi_block_part";

    /**
     * {@inheritDoc}
     * <p>
     * Breaking one part of a multi-block removes the other parts without dropping them, so the block drops
     * once.
     */
    @Override
    public boolean onBreak(Item item) {
        MultiBlock multiBlock = spec.behavior().multiBlock();
        if (multiBlock != null && getPropertyValue(MULTI_BLOCK_PART) instanceof Integer brokenPart) {
            BlockFace direction = multiBlockDirection(multiBlock);
            for (int part = 0; part < multiBlock.parts(); part++) {
                if (part == brokenPart) {
                    continue;
                }
                int offset = part - brokenPart;
                Block other = offset > 0 ? getSide(direction, offset) : getSide(direction.getOpposite(), -offset);
                if (getId().equals(other.getId()) && Integer.valueOf(part).equals(other.getPropertyValue(MULTI_BLOCK_PART))) {
                    getLevel().setBlock(other, Block.get(BlockID.AIR), true, true);
                }
            }
        }
        return super.onBreak(item);
    }

    private BlockFace multiBlockDirection(MultiBlock multiBlock) {
        if (multiBlock.direction().getAxis() != BlockFace.Axis.Y
                && getPropertyValue("minecraft:cardinal_direction") instanceof String cardinal) {
            BlockFace facing = horizontalFace(cardinal);
            if (facing != null) {
                return facing;
            }
        }
        return multiBlock.direction();
    }

    private static @Nullable BlockFace horizontalFace(String name) {
        return switch (name) {
            case "north" -> BlockFace.NORTH;
            case "south" -> BlockFace.SOUTH;
            case "east" -> BlockFace.EAST;
            case "west" -> BlockFace.WEST;
            default -> null;
        };
    }

    /**
     * Recomputes the states that follow the neighbours: the {@code minecraft:connection} trait's connections
     * and the {@code minecraft:corner} shape.
     *
     * @return whether a state changed
     */
    private boolean updateNeighbourStates() {
        Behavior behavior = spec.behavior();
        if (!behavior.cardinalConnections() && behavior.cornerWith() == null) {
            return false;
        }
        BlockState before = this.blockstate;
        if (behavior.cardinalConnections()) {
            for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
                setTraitState("minecraft:connection_" + face.getName(), connectsTo(getSide(face), face));
            }
        }
        if (behavior.cornerWith() != null) {
            setTraitState("minecraft:corner", cornerShape());
        }
        return before != this.blockstate;
    }

    private boolean connectsTo(Block neighbour, BlockFace face) {
        if (neighbour instanceof BlockDataDriven dataDriven) {
            if (!dataDriven.acceptsConnectionFrom(this, false)) {
                return false;
            }
            if (dataDriven.spec.behavior().cardinalConnections()) {
                return true;
            }
        }
        return getId().equals(neighbour.getId()) || (neighbour.isSolid(face.getOpposite()) && !neighbour.isTransparent());
    }

    /**
     * The shape vanilla stairs take: an outer corner when the block behind faces across this one, an inner
     * corner when the block in front does, unless a matching block beside it continues the line.
     */
    private String cornerShape() {
        BlockFace facing = horizontalFace(String.valueOf(getPropertyValue("minecraft:cardinal_direction")));
        if (facing == null) {
            return "none";
        }
        Object half = getPropertyValue("minecraft:vertical_half");
        Block behind = getSide(facing);
        BlockFace behindFacing = cornerFacing(behind, half);
        if (behindFacing != null && behindFacing.getAxis() != facing.getAxis() && canTakeShape(behindFacing.getOpposite(), facing, half)) {
            return behindFacing == facing.rotateYCCW() ? "outer_left" : "outer_right";
        }
        Block front = getSide(facing.getOpposite());
        BlockFace frontFacing = cornerFacing(front, half);
        if (frontFacing != null && frontFacing.getAxis() != facing.getAxis() && canTakeShape(frontFacing, facing, half)) {
            return frontFacing == facing.rotateYCCW() ? "inner_left" : "inner_right";
        }
        return "none";
    }

    private boolean canTakeShape(BlockFace side, BlockFace facing, @Nullable Object half) {
        BlockFace sideFacing = cornerFacing(getSide(side), half);
        return sideFacing != facing;
    }

    /**
     * @return the facing of a neighbour this block forms corners with when it sits on the same half, else null
     */
    private @Nullable BlockFace cornerFacing(Block neighbour, @Nullable Object half) {
        List<PlacementFilter.BlockMatcher> cornerWith = spec.behavior().cornerWith();
        boolean matches = cornerWith == null || cornerWith.isEmpty()
                ? getId().equals(neighbour.getId())
                : cornerWith.stream().anyMatch(matcher -> matcher.matches(neighbour));
        if (!matches || !Objects.equals(half, neighbour.getPropertyValue("minecraft:vertical_half"))) {
            return null;
        }
        return horizontalFace(String.valueOf(neighbour.getPropertyValue("minecraft:cardinal_direction")));
    }

    private static final String[] HORIZONTAL_CLOCKWISE = {"north", "east", "south", "west"};

    private void applyTraits(BlockFace face, double fy, @Nullable Player player) {
        TraitPlacement traits = spec.behavior().traitPlacement();
        if (player != null && traits.cardinalDirection()) {
            setTraitState("minecraft:cardinal_direction", turn(player.getHorizontalFacing(), traits.yRotationOffset()));
        }
        if (player != null && traits.facingDirection()) {
            String facing;
            if (player.pitch > 45) {
                facing = "down";
            } else if (player.pitch < -45) {
                facing = "up";
            } else {
                facing = turn(player.getHorizontalFacing(), traits.yRotationOffset());
            }
            setTraitState("minecraft:facing_direction", facing);
        }
        if (traits.blockFace()) {
            setTraitState("minecraft:block_face", face.getName().toLowerCase(Locale.ENGLISH));
        }
        if (traits.verticalHalf()) {
            boolean top = face == BlockFace.DOWN || (face != BlockFace.UP && fy > 0.5);
            setTraitState("minecraft:vertical_half", top ? "top" : "bottom");
        }
    }

    /**
     * Turns a horizontal facing counter-clockwise by a {@code y_rotation_offset}.
     *
     * @return the turned direction's state value, such as {@code east}
     */
    static String turn(BlockFace horizontal, int degrees) {
        int index = switch (horizontal) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };
        return HORIZONTAL_CLOCKWISE[Math.floorMod(index - Math.floorDiv(degrees, 90), 4)];
    }

    private void setTraitState(String name, Object value) {
        BlockPropertyType<?> type = stateType(name);
        if (type != null) {
            setPropertyValue(type.tryCreateValue(value));
        }
    }

    private @Nullable BlockPropertyType<?> stateType(String name) {
        for (BlockPropertyType<?> type : spec.properties().getPropertyTypeSet()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * A block with a placement filter breaks, dropping its loot, once no neighbour supports it any more.
     */
    @Override
    public int onUpdate(int type) {
        PlacementFilter filter = spec.behavior().placementFilter();
        if (type == Level.BLOCK_UPDATE_NORMAL && filter != null && !filter.isSupported(this)) {
            this.getLevel().useBreakOn(this);
            return Level.BLOCK_UPDATE_NORMAL;
        }
        if (type == Level.BLOCK_UPDATE_NORMAL && updateNeighbourStates()) {
            this.getLevel().setBlock(this, this, true, true);
            return Level.BLOCK_UPDATE_NORMAL;
        }
        return super.onUpdate(type);
    }

    @Override
    public AxisAlignedBB[] getCollisionBoxes() {
        float[][] boxes = stateData.collisionBoxes();
        if (boxes == null) {
            return super.getCollisionBoxes();
        }
        AxisAlignedBB[] result = new AxisAlignedBB[boxes.length];
        for (int i = 0; i < boxes.length; i++) {
            result[i] = offset(boxes[i]);
        }
        return result;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        if (stateData.collisionBoxes() == null) {
            return super.recalculateBoundingBox();
        }
        float[] bounds = stateData.bounds();
        return bounds == null ? null : offset(bounds);
    }

    @Override
    public boolean isSolid() {
        if (stateData.collisionBoxes() == null) {
            return super.isSolid();
        }
        float[] bounds = stateData.bounds();
        return bounds != null && bounds[4] - bounds[1] >= 0.999f;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        if (spec.behavior().support() == SupportShape.STAIR) {
            return isStairSideFull(side);
        }
        if (stateData.collisionBoxes() == null) {
            return super.isSolid(side);
        }
        float[] bounds = stateData.bounds();
        if (bounds == null) {
            return false;
        }
        return switch (side) {
            case UP -> bounds[4] - bounds[1] >= 0.999f;
            case DOWN -> bounds[1] <= 0.001f;
            default -> true;
        };
    }

    /**
     * A stair is full on the half its {@code minecraft:vertical_half} names and on the side it faces.
     */
    private boolean isStairSideFull(BlockFace side) {
        boolean top = "top".equals(getPropertyValue("minecraft:vertical_half"));
        if (side == BlockFace.DOWN || side == BlockFace.UP) {
            return (side == BlockFace.UP) == top;
        }
        Object facing = getPropertyValue("minecraft:cardinal_direction");
        if (facing == null) {
            facing = getPropertyValue("minecraft:facing_direction");
        }
        return side.getName().equalsIgnoreCase(String.valueOf(facing));
    }

    /**
     * @return the support shape the block declares, or null when it supports attachments as a full cube
     */
    public @Nullable SupportShape getSupportShape() {
        return spec.behavior().support();
    }

    /**
     * @return the note block instruments the block sounds, or null when it sounds the default harp
     */
    public @Nullable InstrumentSound getInstrumentSound() {
        return spec.behavior().instrumentSound();
    }

    /**
     * @return how the block affects a chest directly below it
     */
    public @NotNull ChestObstruction getChestObstruction() {
        return spec.behavior().chestObstruction();
    }

    /**
     * Checks the block's {@code minecraft:connection_rule} for a neighbour that connects to blocks around it.
     *
     * @param neighbour the block connecting, next to this one
     * @param fence     whether the neighbour is a fence
     * @return false when the rule forbids the connection; true when it allows it or the block has no rule,
     * in which case the neighbour's own rules decide
     */
    public boolean acceptsConnectionFrom(@NotNull Block neighbour, boolean fence) {
        ConnectionRule rule = spec.behavior().connectionRule();
        if (rule == null) {
            return true;
        }
        if (rule.accepts() == ConnectionSource.NONE || (rule.accepts() == ConnectionSource.ONLY_FENCES && !fence)) {
            return false;
        }
        for (BlockFace face : rule.directions()) {
            if (neighbour.getFloorX() == getFloorX() + face.getXOffset()
                    && neighbour.getFloorY() == getFloorY() + face.getYOffset()
                    && neighbour.getFloorZ() == getFloorZ() + face.getZOffset()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return whether redstone wire on top of the block connects to wire one block lower beside it, from
     * {@code allows_wire_to_step_down} of {@code minecraft:redstone_conductivity}
     */
    public boolean allowsWireToStepDown() {
        return spec.behavior().wireStepsDown();
    }

    /**
     * @return whether the pack's {@code minecraft:placement_filter} decides where the block may be placed;
     * the server then checks placements itself instead of relying on the client's prediction
     */
    public boolean hasPlacementFilter() {
        return spec.behavior().placementFilter() != null;
    }

    /**
     * @param face the face water would leave through
     * @return whether water held in the block cannot flow out through that face
     */
    public boolean stopsLiquidFlowingOut(@NotNull BlockFace face) {
        LiquidRule liquid = spec.behavior().liquid();
        return liquid != null && liquid.stopsFlowingFrom().contains(face);
    }

    /**
     * @param face the face of this block water would enter through
     * @return whether the block keeps water out on that face while letting it through elsewhere
     */
    public boolean stopsLiquidFlowingIn(@NotNull BlockFace face) {
        LiquidRule liquid = spec.behavior().liquid();
        return liquid != null && liquid.noReaction() && liquid.stopsFlowingFrom().contains(face);
    }

    /**
     * @return whether water flowing into the block destroys it without dropping its loot
     */
    public boolean isBrokenByLiquidWithoutDrops() {
        LiquidRule liquid = spec.behavior().liquid();
        return liquid != null && liquid.broken();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Only blocks declaring {@code minecraft:flower_pottable} go in a flower pot.
     */
    @Override
    public boolean isPotBlockState() {
        return spec.behavior().flowerPottable();
    }

    @Override
    public boolean isNormalBlock() {
        Boolean conductor = spec.behavior().redstoneConductor();
        return conductor != null ? conductor : super.isNormalBlock();
    }

    @Override
    public boolean isPowerSource() {
        return stateData.redstoneProducer() != null || super.isPowerSource();
    }

    /**
     * {@inheritDoc}
     * <p>
     * The block's {@code minecraft:redstone_producer} powers the neighbours on its connected faces.
     */
    @Override
    public int getWeakPower(BlockFace side) {
        RedstoneProducer producer = stateData.redstoneProducer();
        if (producer == null) {
            return 0;
        }
        return producer.connectedFaces().contains(ownFace(producer, side.getOpposite())) ? producer.power() : 0;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The block's {@code minecraft:redstone_producer} strongly powers the neighbour on its strongly powered face.
     */
    @Override
    public int getStrongPower(BlockFace side) {
        RedstoneProducer producer = stateData.redstoneProducer();
        if (producer == null || producer.stronglyPoweredFace() == null) {
            return 0;
        }
        return producer.stronglyPoweredFace() == ownFace(producer, side.getOpposite()) ? producer.power() : 0;
    }

    private static final BlockFace[] HORIZONTAL_COUNTER_CLOCKWISE = {BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.EAST};

    /**
     * Turns a face in the world into the block's own face, undoing the state's transformation when the
     * producer's faces turn with it.
     */
    private BlockFace ownFace(RedstoneProducer producer, BlockFace worldFace) {
        if (!producer.transformRelative() || stateData.quarterTurnsY() == 0 || worldFace.getAxis() == BlockFace.Axis.Y) {
            return worldFace;
        }
        int index = switch (worldFace) {
            case WEST -> 1;
            case SOUTH -> 2;
            case EAST -> 3;
            default -> 0;
        };
        return HORIZONTAL_COUNTER_CLOCKWISE[Math.floorMod(index - stateData.quarterTurnsY(), 4)];
    }

    /**
     * {@inheritDoc}
     * <p>
     * An {@code item_specific_speeds} entry matching the item replaces the block's destroy time.
     */
    @Override
    public double calculateBreakTimeNotInAir(@NotNull Item item, @Nullable Player player) {
        double seconds = super.calculateBreakTimeNotInAir(item, player);
        double hardness = getHardness();
        if (hardness <= 0) {
            return seconds;
        }
        for (ItemSpeed speed : spec.behavior().itemSpeeds()) {
            if (speed.item().test(item)) {
                return seconds * speed.seconds() / hardness;
            }
        }
        return seconds;
    }

    private AxisAlignedBB offset(float[] box) {
        return new SimpleAxisAlignedBB(this.x + box[0], this.y + box[1], this.z + box[2],
                this.x + box[3], this.y + box[4], this.z + box[5]);
    }

    private Set<String> currentTags() {
        Set<String> tags = spec.tagsByState().get(this.blockstate.blockStateHash());
        return tags != null ? tags : spec.baseTags();
    }

    /**
     * Reads the collision boxes of a component compound in the client schema and places them in the
     * block the way the client does: scaled about the scale pivot and rotated about the rotation pivot,
     * both offsets from the block's centre, then translated, by the {@code minecraft:transformation} next
     * to them, and kept within the 16x24x16 pixel space a collision box may occupy.
     */
    static float @Nullable [][] readCollisionBoxes(CompoundTag components) {
        if (!components.contains("minecraft:collision_box")) {
            return null;
        }
        CompoundTag collision = components.getCompound("minecraft:collision_box");
        if ((collision.contains("enabled") && !collision.getBoolean("enabled")) || !collision.contains("boxes")) {
            return new float[0][];
        }
        CompoundTag transformation = components.contains("minecraft:transformation")
                ? components.getCompound("minecraft:transformation") : null;
        ListTag<CompoundTag> boxTags = collision.getList("boxes", CompoundTag.class);
        float[][] boxes = new float[boxTags.size()][];
        int count = 0;
        for (CompoundTag boxTag : boxTags.getAll()) {
            float[] box = {
                    boxTag.getFloat("minX") / 16f, boxTag.getFloat("minY") / 16f, boxTag.getFloat("minZ") / 16f,
                    boxTag.getFloat("maxX") / 16f, boxTag.getFloat("maxY") / 16f, boxTag.getFloat("maxZ") / 16f
            };
            if (transformation != null) {
                box = transform(box, transformation);
            }
            box[0] = Math.max(box[0], 0f);
            box[1] = Math.max(box[1], 0f);
            box[2] = Math.max(box[2], 0f);
            box[3] = Math.min(box[3], 1f);
            box[4] = Math.min(box[4], 1.5f);
            box[5] = Math.min(box[5], 1f);
            if (box[0] < box[3] && box[1] < box[4] && box[2] < box[5]) {
                boxes[count++] = box;
            }
        }
        return Arrays.copyOf(boxes, count);
    }

    private static float[] transform(float[] box, CompoundTag transformation) {
        float[] scale = {
                floatOr(transformation, "SX", 1f), floatOr(transformation, "SY", 1f), floatOr(transformation, "SZ", 1f)
        };
        float[] translation = {
                floatOr(transformation, "TX", 0f), floatOr(transformation, "TY", 0f), floatOr(transformation, "TZ", 0f)
        };
        float[] scalePivot = {
                floatOr(transformation, "SXP", 0f), floatOr(transformation, "SYP", 0f), floatOr(transformation, "SZP", 0f)
        };
        float[] rotationPivot = {
                floatOr(transformation, "RXP", 0f), floatOr(transformation, "RYP", 0f), floatOr(transformation, "RZP", 0f)
        };
        int rx = transformation.getInt("RX");
        int ry = transformation.getInt("RY");
        int rz = transformation.getInt("RZ");
        float[] result = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE};
        for (int corner = 0; corner < 8; corner++) {
            float[] point = {
                    ((corner & 1) == 0 ? box[0] : box[3]) - 0.5f,
                    ((corner & 2) == 0 ? box[1] : box[4]) - 0.5f,
                    ((corner & 4) == 0 ? box[2] : box[5]) - 0.5f
            };
            for (int axis = 0; axis < 3; axis++) {
                point[axis] = (point[axis] - scalePivot[axis]) * scale[axis] + scalePivot[axis] - rotationPivot[axis];
            }
            rotate(point, 1, 2, rx);
            rotate(point, 2, 0, ry);
            rotate(point, 0, 1, rz);
            for (int axis = 0; axis < 3; axis++) {
                point[axis] += rotationPivot[axis];
            }
            for (int axis = 0; axis < 3; axis++) {
                float value = point[axis] + 0.5f + translation[axis];
                result[axis] = Math.min(result[axis], value);
                result[axis + 3] = Math.max(result[axis + 3], value);
            }
        }
        return result;
    }

    /**
     * Rotates a point by quarter turns in the plane of two axes, so that a quarter turn about Y takes
     * the north side of a block to its west side, as Bedrock rotates block geometry.
     */
    private static void rotate(float[] point, int first, int second, int quarterTurns) {
        int turns = Math.floorMod(quarterTurns, 4);
        for (int i = 0; i < turns; i++) {
            float a = point[first];
            float b = point[second];
            point[first] = -b;
            point[second] = a;
        }
    }

    private static float floatOr(CompoundTag tag, String key, float fallback) {
        return tag.contains(key) ? tag.getFloat(key) : fallback;
    }

    private static float @Nullable [] union(float[][] boxes) {
        if (boxes.length == 0) {
            return null;
        }
        float[] bounds = boxes[0].clone();
        for (int i = 1; i < boxes.length; i++) {
            for (int axis = 0; axis < 3; axis++) {
                bounds[axis] = Math.min(bounds[axis], boxes[i][axis]);
                bounds[axis + 3] = Math.max(bounds[axis + 3], boxes[i][axis + 3]);
            }
        }
        return bounds;
    }
}
