package org.powernukkitx.block.customblock;

import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockProperties;
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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

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
     * What a permutation changes on the server while its condition holds.
     *
     * @param condition    evaluated against each state when the {@link Spec} is built
     * @param components   the permutation's components in the client schema, layered over the base
     *                     components of the states the condition holds for; collision boxes and
     *                     transformations are read from them
     * @param serverValues applied, in order, to a builder seeded from the base definition, to set the
     *                     gameplay values the permutation changes
     */
    public record PermutationRule(@NotNull MolangExpression condition, @NotNull CompoundTag components,
                                  @NotNull List<Consumer<CustomBlockDefinition.Builder>> serverValues) {

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
     * @param yRotationOffset   degrees, a multiple of 90, the horizontal directions are turned clockwise by
     * @param blockFace         sets {@code minecraft:block_face} to the face the block was placed against
     * @param verticalHalf      sets {@code minecraft:vertical_half} to the half of the clicked block face
     */
    public record TraitPlacement(boolean cardinalDirection, boolean facingDirection, int yRotationOffset,
                                 boolean blockFace, boolean verticalHalf) {

        public static final TraitPlacement NONE = new TraitPlacement(false, false, 0, false, false);
    }

    /**
     * The server's view of one state, shared by every state the same permutations apply to.
     *
     * @param definition     the definition backing the property getters in this state
     * @param collisionBoxes the collision boxes in block units relative to the block's corner, after
     *                       its transformation, as {@code [minX, minY, minZ, maxX, maxY, maxZ]}; empty
     *                       when collision is disabled, null when the block declares no collision box
     *                       and collides as a full cube
     * @param bounds         the union of {@code collisionBoxes}, or null when there are none
     */
    public record StateData(@NotNull CustomBlockDefinition definition, float @Nullable [][] collisionBoxes,
                            float @Nullable [] bounds) {

        static StateData of(CustomBlockDefinition definition) {
            float[][] boxes = readCollisionBoxes(definition.getComponents());
            return new StateData(definition, boxes, boxes == null ? null : union(boxes));
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
     * @param placementFilter  where the block may be placed, or null for anywhere
     * @param traitPlacement   the states the block's traits set when it is placed
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
            @Nullable PlacementFilter placementFilter,
            @NotNull TraitPlacement traitPlacement
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
         * @param placementFilter  where the block may be placed, or null for anywhere
         * @param traitPlacement   the states the block's traits set when it is placed
         */
        public static @NotNull Spec of(@NotNull BlockProperties properties, @NotNull CustomBlockDefinition definition,
                                       @NotNull Set<String> baseTags, @NotNull List<TagRule> tagRules,
                                       @Nullable String lootTable, @NotNull List<LootRule> lootRules,
                                       @NotNull List<PermutationRule> permutationRules,
                                       @Nullable PlacementFilter placementFilter,
                                       @NotNull TraitPlacement traitPlacement) {
            StateData baseState = StateData.of(definition);
            if (tagRules.isEmpty() && lootRules.isEmpty() && permutationRules.isEmpty()) {
                return new Spec(properties, definition, baseTags, Int2ObjectMaps.emptyMap(), lootTable,
                        Int2ObjectMaps.emptyMap(), baseState, Int2ObjectMaps.emptyMap(), placementFilter, traitPlacement);
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
                            key -> StateData.of(stateDefinition(properties, definition, permutationRules, key))));
                }
            }
            tagsByState.trim();
            lootByState.trim();
            dataByState.trim();
            return new Spec(properties, definition, baseTags, tagsByState, lootTable, lootByState, baseState,
                    dataByState, placementFilter, traitPlacement);
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
        PlacementFilter filter = spec.placementFilter();
        if (filter != null && !filter.allowsPlacement(target, face)) {
            return false;
        }
        applyTraits(face, fy, player);
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    private static final String[] HORIZONTAL_CLOCKWISE = {"north", "east", "south", "west"};

    private void applyTraits(BlockFace face, double fy, @Nullable Player player) {
        TraitPlacement traits = spec.traitPlacement();
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

    private static String turn(BlockFace horizontal, int degrees) {
        int index = switch (horizontal) {
            case EAST -> 1;
            case SOUTH -> 2;
            case WEST -> 3;
            default -> 0;
        };
        return HORIZONTAL_CLOCKWISE[Math.floorMod(index + Math.floorDiv(degrees, 90), 4)];
    }

    private void setTraitState(String name, String value) {
        for (BlockPropertyType<?> type : spec.properties().getPropertyTypeSet()) {
            if (type.getName().equals(name)) {
                setPropertyValue(type.tryCreateValue(value));
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * A block with a placement filter breaks, dropping its loot, once no neighbour supports it any more.
     */
    @Override
    public int onUpdate(int type) {
        PlacementFilter filter = spec.placementFilter();
        if (type == Level.BLOCK_UPDATE_NORMAL && filter != null && !filter.isSupported(this)) {
            this.getLevel().useBreakOn(this);
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
     * block the way the client does: scaled and rotated about the block's centre, then translated, by
     * the {@code minecraft:transformation} next to them, and kept within the 16x24x16 pixel space a
     * collision box may occupy.
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
                point[axis] *= scale[axis];
            }
            rotate(point, 1, 2, rx);
            rotate(point, 2, 0, ry);
            rotate(point, 0, 1, rz);
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
