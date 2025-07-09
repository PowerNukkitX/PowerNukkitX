package cn.nukkit.command.tree.node;

import cn.nukkit.block.Block;
import cn.nukkit.block.customblock.CustomBlock;
import cn.nukkit.command.utils.CommandUtils;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.customitem.CustomItem;

/**
 * 解析对应参数为{@link Item}值
 * <p>
 * 所有命令枚举{@link cn.nukkit.command.data.CommandEnum#ENUM_ITEM ENUM_ITEM}如果没有手动指定{@link IParamNode},则会默认使用这个解析
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
