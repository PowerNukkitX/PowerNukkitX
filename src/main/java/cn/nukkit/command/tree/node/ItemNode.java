package cn.nukkit.command.tree.node;

import cn.nukkit.block.Block;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.command.utils.CommandUtils;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.customitem.CustomItem;

/**
 * Parses a command parameter as an {@link Item} value for PowerNukkitX command trees.
 * <p>
 * This node is used for all command enums of type {@link cn.nukkit.command.data.CommandEnum#ENUM_ITEM ENUM_ITEM}
 * if no custom {@link IParamNode} is specified. It resolves item names (with or without namespace), rejects hidden custom items,
 * and validates ItemBlock custom blocks. Sets the parsed item as the node value or triggers an error if invalid.
 * <p>
 * <b>Features:</b>
 * <ul>
 *   <li>Resolves item names to {@link Item} instances, supporting namespaced and non-namespaced names.</li>
 *   <li>Rejects custom items and ItemBlocks of custom blocks marked as hidden from commands.</li>
 *   <li>Sets the parsed item as the node value or triggers an error if invalid.</li>
 * </ul>
 * <p>
 * <b>Usage:</b>
 * <ul>
 *   <li>Used in command trees for item parameter parsing.</li>
 *   <li>Automatically selected for item enums if no custom node is provided.</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <pre>
 * // Parses: "diamond_sword", "minecraft:apple"
 * </pre>
 *
 * @author PowerNukkitX Project Team
 * @see Item
 * @see cn.nukkit.command.data.CommandEnum#ENUM_ITEM
 * @see IParamNode
 * @since PowerNukkitX 1.19.50
 */
public class ItemNode extends ParamNode<Item> {
    @Override
    public void fill(String arg) {
        if (arg.indexOf(':') == -1) {
            arg = "minecraft:" + arg;
        }

        Item item = Item.get(arg);
        if (item.isNull()) {
            error();
            return;
        }

        // Reject if it's a custom item marked as hidden
        if (item instanceof CustomItem customItem && CommandUtils.isHiddenInCommands(customItem)) {
            error();
            return;
        }

        // Reject if it's an ItemBlock of a custom block marked as hidden
        if (item instanceof ItemBlock itemBlock) {
            Block block = itemBlock.getBlock();
            if (block instanceof CustomBlock customBlock && CommandUtils.isHiddenInCommands(customBlock)) {
                error();
                return;
            }
        }

        this.value = item;
    }
}
