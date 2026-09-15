package org.powernukkitx.block.customblock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.customblock.data.Movable;
import org.powernukkitx.inventory.CustomCraftingTableInventory;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * Handles server-side runtime behavior provided by custom block components.
 * <p>
 * Custom blocks can still override methods such as {@link Block#canBeActivated()}
 * and {@link Block#onActivate(org.powernukkitx.item.Item, Player, org.powernukkitx.math.BlockFace, float, float, float)}
 * to provide their own behavior instead of using the default component handling.
 *
 * @author Curse
 */
public final class CustomBlockComponentBehavior {

    private CustomBlockComponentBehavior() {
    }

    /**
     * Gets the movable behavior of the specified block based on its registered
     * block components.
     *
     * @param block the block to check
     * @return the movable behavior of the block
     */
    public static Movable getMovable(@NotNull Block block) {
        CustomBlockDefinition definition = block.getCustomDefinition();
        return definition != null ? definition.getMovable() : Movable.DEFAULT;
    }

    /**
     * Checks whether the specified block can be pushed by a piston.
     *
     * @param block the block to check
     * @return {@code true} if the block can be pushed or is broken when pushed; {@code false} otherwise
     */
    public static boolean canBePushed(@NotNull Block block) {
        return switch (getMovable(block).movementType()) {
            case PUSH_PULL, PUSH, POPPED -> true;
            case IMMOVABLE -> false;
        };
    }

    /**
     * Checks whether the specified block can be pulled by a piston.
     *
     * @param block the block to check
     * @return {@code true} if the block can be pulled; {@code false} otherwise
     */
    public static boolean canBePulled(@NotNull Block block) {
        return getMovable(block).movementType() == Movable.MovementType.PUSH_PULL;
    }

    /**
     * Checks whether the specified block breaks when moved by a piston.
     *
     * @param block the block to check
     * @return {@code true} if the block breaks when moved; {@code false} otherwise
     */
    public static boolean breaksWhenMoved(@NotNull Block block) {
        return getMovable(block).movementType() == Movable.MovementType.POPPED;
    }

    /**
     * Checks whether the specified block sticks to a piston.
     *
     * @param block the block to check
     * @return {@code true} if the block can stick to a piston; {@code false} otherwise
     */
    public static boolean sticksToPiston(@NotNull Block block) {
        return getMovable(block).movementType() == Movable.MovementType.PUSH_PULL;
    }

    /**
     * Checks whether the specified block can stick to other blocks of the same type
     * when moved by a piston.
     *
     * @param block the block to check
     * @return {@code true} if the block can stick to blocks of the same type; {@code false} otherwise
     */
    public static boolean canSticksBlock(@NotNull Block block) {
        Movable movable = getMovable(block);
        return movable.movementType() == Movable.MovementType.PUSH_PULL && movable.sticky() == Movable.StickyType.SAME;
    }

    /**
     * Checks whether the specified custom block can be activated based on its
     * registered block components.
     * <p>
     * @param block the custom block to check
     * @return {@code true} if one of the block's components provides activation behavior, otherwise {@code false}
     */
    public static boolean canBeActivated(@NotNull Block block) {
        CustomBlockDefinition definition = block.getCustomDefinition();
        if (definition == null) {
            return false;
        }

        CompoundTag components = definition.getComponents();

        if (components.contains("minecraft:crafting_table")) {
            return true;
        }

        if (components.contains("minecraft:custom_components")) {
            CompoundTag custom = components.getCompound("minecraft:custom_components");

            if (custom.contains("hasPlayerInteract")) {
                return custom.getByte("hasPlayerInteract") != 0;
            }
        }

        return false;
    }

    /**
     * Handles activation behavior provided by the custom block's registered components.
     * <p>
     * @param block the activated custom block
     * @param player the player activating the block, or {@code null} when no player is available
     * @return {@code true} if a component handled the activation, otherwise {@code false}
     */
    public static boolean onActivate(@NotNull Block block, @Nullable Player player) {
        CustomBlockDefinition definition = block.getCustomDefinition();
        if (definition == null || !canBeActivated(block)) {
            return false;
        }

        CompoundTag components = definition.getComponents();

        if (components.contains("minecraft:crafting_table")) {
            if (player != null) {
                player.addWindow(CustomCraftingTableInventory.create(block));
            }
            return true;
        }

        return false;
    }
}
