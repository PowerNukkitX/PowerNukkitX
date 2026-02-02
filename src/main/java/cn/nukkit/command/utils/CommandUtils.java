package cn.nukkit.command.utils;

import cn.nukkit.block.Block;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.block.customblock.CustomBlockDefinition;
import cn.nukkit.item.customitem.CustomItem;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * Utility class for command-related helper methods in PowerNukkitX.
 * <p>
 * Provides static methods to determine if custom blocks or custom items are hidden from command usage.
 * These methods inspect the NBT data of the provided block or item to check the 'is_hidden_in_commands' property.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Checks if a {@link CustomBlock} is hidden from commands by inspecting its NBT 'menu_category'.</li>
 *   <li>Checks if a {@link CustomItem} is hidden from commands by inspecting its NBT 'components' and 'item_properties'.</li>
 *   <li>Returns true if the 'is_hidden_in_commands' property is set to 1, false otherwise.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Use in command parameter nodes or plugins to filter out hidden blocks/items from command suggestions or execution.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * if (CommandUtils.isHiddenInCommands(customBlock)) {
 *     // Skip this block in command processing
 * }
 * if (CommandUtils.isHiddenInCommands(customItem)) {
 *     // Skip this item in command processing
 * }
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see CustomBlock
 * @see CustomItem
 * @see CompoundTag
 * @since PowerNukkitX 1.19.50
 */
public final class CommandUtils {
    public static boolean isHiddenInCommands(Block block) {
        CustomBlockDefinition definition = block.getCustomDefinition();
        if (definition == null) {
            return false;
        }
        CompoundTag nbt = definition.nbt();
        CompoundTag menuCategory = nbt.getCompound("menu_category");
        return menuCategory.getByte("is_hidden_in_commands") == 1;
    }

    public static boolean isHiddenInCommands(CustomItem item) {
        CompoundTag nbt = item.getDefinition().nbt();
        CompoundTag components = nbt.getCompound("components");
        if (components.contains("item_properties")) {
            CompoundTag itemProps = components.getCompound("item_properties");
            return itemProps.getByte("is_hidden_in_commands") == 1;
        }
        return false;
    }
}