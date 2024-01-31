package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.types.itemstack.request.action.CraftCreativeAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.registry.Registries;
import lombok.extern.slf4j.Slf4j;

/**
 * Allay Project 2023/7/26
 *
 * @author daoge_cmd
 */
@Slf4j
public class CraftCreativeActionProcessor implements ItemStackRequestActionProcessor<CraftCreativeAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_CREATIVE;
    }

    @Override
    public ActionResponse handle(CraftCreativeAction action, Player player, ItemStackRequestContext context) {
        var item = Registries.CREATIVE.get(action.getCreativeItemNetworkId() - 1);
        if (item == null) {
            log.warn("Unknown creative item network id: {}", action.getCreativeItemNetworkId() - 1);
            return context.error();
        }
        item = item.clone().autoAssignStackNetworkId();
        item.setCount(item.getMaxStackSize());
        player.getCreativeOutputInventory().setItem(0, item, false);
        //从创造物品栏拿东西不需要响应
        return null;
    }
}
