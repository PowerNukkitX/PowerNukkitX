package cn.nukkit.command.tree.node;

import cn.nukkit.item.Item;

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
