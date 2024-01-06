package cn.nukkit.network.protocol.types.itemstack.request.action;

import cn.nukkit.item.Item;
import lombok.Value;

/**
 * CraftResultsDeprecatedStackRequestAction is an additional, deprecated packet sent by the client after
 * crafting. It holds the final results and the amount of times the recipe was crafted. It shouldn't be used.
 * This action is also sent when an item is enchanted. Enchanting should be treated mostly the same way as
 * crafting, where the old item is consumed.
 */
@Value
public class CraftResultsDeprecatedAction implements ItemStackRequestAction {
    Item[] resultItems;
    int timesCrafted;

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_RESULTS_DEPRECATED;
    }
}
