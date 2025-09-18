package cn.nukkit.command.tree.node;

import cn.nukkit.block.Block;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.command.utils.CommandUtils;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.customitem.CustomItem;

/**
 * The corresponding parameters for analysis are {@link Item} value <p>
 * All command enumeration {@link cn.nukkit.command.data.CommandEnum#ENUM_ITEM ENUM_ITEM} If not manually specified {@link IParamNode}, This analysis will be used by default
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
