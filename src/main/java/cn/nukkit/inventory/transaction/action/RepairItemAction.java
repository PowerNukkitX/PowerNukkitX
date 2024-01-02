package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import lombok.ToString;


@ToString(callSuper = true)

public class RepairItemAction extends NoOpIventoryAction {

    private int type;


    public RepairItemAction(Item sourceItem, Item targetItem, int type) {
        super(sourceItem, targetItem);
        this.type = type;
    }

    @Override
    public boolean isValid(Player source) {
        return source.getWindowById(Player.ANVIL_WINDOW_ID) != null;
    }

    public int getType() {
        return this.type;
    }
}
