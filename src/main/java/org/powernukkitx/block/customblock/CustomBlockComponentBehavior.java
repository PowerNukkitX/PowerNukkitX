package org.powernukkitx.block.customblock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.Player;
import org.powernukkitx.block.Block;
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
