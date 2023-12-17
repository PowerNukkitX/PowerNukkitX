package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import lombok.ToString;


@ToString(callSuper = true)

public class EnchantingAction extends NoOpIventoryAction {
    private int type;


    public EnchantingAction(Item source, Item target, int type) {
        super(source, target);
        this.type = type;
    }

    @Override
    public boolean isValid(Player source) {
        return source.getWindowById(Player.ENCHANT_WINDOW_ID) != null;
    }


    public int getType() {
        return type;
    }
}
