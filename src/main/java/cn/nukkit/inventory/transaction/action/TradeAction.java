package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.entity.passive.EntityVillager;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;

public class TradeAction extends InventoryAction {
    private final EntityVillager villager;
    private final int type;

    public TradeAction(Item sourceItem, Item targetItem, int windowId, EntityVillager villager) {
        super(sourceItem, targetItem);
        this.type = windowId;
        this.villager = villager;
    }

    @Override
    public boolean isValid(Player source) {
        if (type == NetworkInventoryAction.SOURCE_TYPE_TRADING_INPUT_1) {
            for (var tag : villager.recipes.getAll()) {
                var cmp = (CompoundTag) tag;
                if (cmp.contains("buyA") || cmp.contains("buyB")) {
                    var buyA = cmp.getCompound("buyA");
                    var result = buyA.getByte("Count") == targetItem.getCount() && buyA.getByte("Damage") == targetItem.getDamage()
                            && buyA.getString("Name").equals(targetItem.getNamespaceId());
                    if (targetItem.hasCompoundTag()) {
                        result &= targetItem.getNamedTag().equals(buyA.getCompound("tag"));
                    }
                    if (result) {
                        var buyB = cmp.getCompound("buyB");
                        return buyB.getByte("Count") == targetItem.getCount() && buyB.getByte("Damage") == targetItem.getDamage()
                                && buyB.getString("Name").equals(targetItem.getNamespaceId());
                    }
                }
            }
        } else if (type == NetworkInventoryAction.SOURCE_TYPE_TRADING_OUTPUT) {

        }
        return true;
    }

    @Override
    public boolean execute(Player source) {
        return true;
    }

    @Override
    public void onExecuteSuccess(Player source) {

    }

    @Override
    public void onExecuteFail(Player source) {
    }

    public int getType() {
        return type;
    }
}
