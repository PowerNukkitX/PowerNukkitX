package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.types.itemstack.request.action.CraftCreativeAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.registry.Registries;
import lombok.extern.slf4j.Slf4j;

/**
 * Allay Project 2023/7/26
 *
 * @author daoge_cmd | CoolLoong
 */
@Slf4j
public class CraftCreativeActionProcessor implements ItemStackRequestActionProcessor<CraftCreativeAction> {
    public static final String CRAFT_CREATIVE_KEY = "craft_creative_key";

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_CREATIVE;
    }

    @Override
    public ActionResponse handle(CraftCreativeAction action, Player player, ItemStackRequestContext context) {
        var item = Registries.CREATIVE.get(action.getCreativeItemNetworkId() - 1);
        if (!player.isCreative()) {
            log.warn("This player {} is get createitems in non-creative mode, which may be a hacker!",player.getName());
            return context.error();
        }
        if (item == null) {
            log.warn("Unknown creative item network id: {}", action.getCreativeItemNetworkId() - 1);
            return context.error();
        }
        item = item.clone().autoAssignStackNetworkId();
        item.setCount(item.getMaxStackSize());
        player.getCreativeOutputInventory().setItem(item);
        //Picking up something from the creation inventory does not require a response
        context.put(CRAFT_CREATIVE_KEY, true);
        return null;
    }
}
