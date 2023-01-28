package cn.nukkit.command.tree.node;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;

/**
 * 解析对应参数为{@link Item}值
 * <p>
 * 所有命令枚举{@link cn.nukkit.command.data.CommandEnum#ENUM_ITEM ENUM_ITEM}如果没有手动指定{@link IParamNode},则会默认使用这个解析
 */
@PowerNukkitXOnly
@Since("1.19.60-r1")
public class ItemNode extends ParamNode<Item> {
    @Override
    public void fill(String arg) {
        Item item = Item.fromString(arg);
        if (item.getId() == 0) {
            error();
            return;
        }
        this.value = item;
    }
}
