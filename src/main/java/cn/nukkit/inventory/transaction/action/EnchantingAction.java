package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import lombok.ToString;

@Since("1.3.1.0-PN")
@ToString(callSuper = true)
@PowerNukkitDifference(extendsOnlyInPowerNukkit = NoOpIventoryAction.class, insteadOf = InventoryAction.class)
public class EnchantingAction extends NoOpIventoryAction {
    private int type;

    @Since("1.3.1.0-PN")
    public EnchantingAction(Item source, Item target, int type) {
        super(source, target);
        this.type = type;
    }

    @Override
    public boolean isValid(Player source) {
        return source.getWindowById(Player.ENCHANT_WINDOW_ID) != null;
    }

    @Since("1.3.1.0-PN")
    public int getType() {
        return type;
    }
}
