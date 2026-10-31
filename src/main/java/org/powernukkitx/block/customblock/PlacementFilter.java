package org.powernukkitx.block.customblock;

import org.powernukkitx.block.Block;
import org.powernukkitx.block.property.type.BlockPropertyType;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.molang.MolangExpression;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The server side of a behaviour pack's {@code minecraft:placement_filter}: which faces a block may be
 * attached to and which blocks it may be attached against.
 * <p>
 * A block passes when any one condition holds. A condition holds for a support block and the face of
 * that block the new block is attached to when the face is allowed and the support matches the
 * condition's block filter; an empty filter accepts any block that is not air.
 *
 * @param conditions alternatives, of which one must hold
 */
public record PlacementFilter(@NotNull List<Condition> conditions) {

    public PlacementFilter {
        conditions = List.copyOf(conditions);
    }

    /**
     * @param allowedFaces the faces of the support block this block may be attached to
     * @param blockFilter  the blocks this block may be attached against; empty for any block
     */
    public record Condition(@NotNull Set<BlockFace> allowedFaces, @NotNull List<BlockMatcher> blockFilter) {

        public Condition {
            allowedFaces = allowedFaces.isEmpty() ? EnumSet.noneOf(BlockFace.class) : EnumSet.copyOf(allowedFaces);
            blockFilter = List.copyOf(blockFilter);
        }

        boolean accepts(Block support, BlockFace face) {
            if (!allowedFaces.contains(face) || support.isAir()) {
                return false;
            }
            if (blockFilter.isEmpty()) {
                return true;
            }
            for (BlockMatcher matcher : blockFilter) {
                if (matcher.matches(support)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * One entry of a block filter.
     */
    public sealed interface BlockMatcher permits IdMatcher, TagMatcher {

        boolean matches(@NotNull Block block);
    }

    /**
     * Matches a block identifier and, optionally, some of its states.
     *
     * @param identifier the block identifier
     * @param states     state name to required value, compared as strings; empty for any state
     */
    public record IdMatcher(@NotNull String identifier, @NotNull Map<String, String> states) implements BlockMatcher {

        public IdMatcher {
            states = Map.copyOf(states);
        }

        @Override
        public boolean matches(@NotNull Block block) {
            if (!identifier.equals(block.getId())) {
                return false;
            }
            for (Map.Entry<String, String> required : states.entrySet()) {
                if (!required.getValue().equals(stateValue(block, required.getKey()))) {
                    return false;
                }
            }
            return true;
        }

        private static @Nullable String stateValue(Block block, String name) {
            for (BlockPropertyType.BlockPropertyValue<?, ?, ?> value : block.getBlockState().getBlockPropertyValues()) {
                if (value.getPropertyType().getName().equals(name)) {
                    Object raw = value.getValue();
                    return raw instanceof Enum<?> enumValue ? enumValue.name().toLowerCase(Locale.ENGLISH) : String.valueOf(raw);
                }
            }
            return null;
        }
    }

    /**
     * Matches blocks for which a Molang expression over their tags, such as
     * {@code q.any_tag('stone', 'dirt')}, holds.
     */
    public record TagMatcher(@NotNull MolangExpression expression) implements BlockMatcher {

        @Override
        public boolean matches(@NotNull Block block) {
            return expression.evaluateAsBoolean(new TagQueryContext(block::hasTag));
        }
    }

    /**
     * Checks a placement against the block it is being attached to.
     *
     * @param support the block clicked to place this one
     * @param face    the face of {@code support} that was clicked
     * @return true when some condition accepts the placement
     */
    public boolean allowsPlacement(@NotNull Block support, @NotNull BlockFace face) {
        for (Condition condition : conditions) {
            if (condition.accepts(support, face)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks a block already in the world against its neighbours, for when one of them changes. The
     * face it was originally attached to is not stored, so the block survives while any allowed face
     * still has an accepted support.
     *
     * @param block the placed block; must be in a loaded level
     * @return true when the block may stay
     */
    public boolean isSupported(@NotNull Block block) {
        for (Condition condition : conditions) {
            for (BlockFace face : condition.allowedFaces()) {
                if (condition.accepts(block.getSide(face.getOpposite()), face)) {
                    return true;
                }
            }
        }
        return false;
    }
}
