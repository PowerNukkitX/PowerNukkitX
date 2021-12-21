package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import lombok.ToString;

@Since("1.4.0.0-PN")
@ToString(callSuper = true)
@PowerNukkitDifference(extendsOnlyInPowerNukkit = NoOpIventoryAction.class, insteadOf = InventoryAction.class)
public class RepairItemAction extends NoOpIventoryAction {

    private int type;

    @Since("1.4.0.0-PN")
    public RepairItemAction(Item sourceItem, Item targetItem, int type) {
        super(sourceItem, targetItem);
        this.type = type;
    }

    @Override
    public boolean isValid(Player source) {
        return source.getWindowById(Player.ANVIL_WINDOW_ID) != null;
    }

    @Since("1.4.0.0-PN")
    public int getType() {
        return this.type;
    }
}
