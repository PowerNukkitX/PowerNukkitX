package cn.nukkit.inventory.request;


import cn.nukkit.Player;
import cn.nukkit.network.protocol.types.itemstack.request.action.CraftResultsDeprecatedAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponse;
import org.jetbrains.annotations.Nullable;

/**
 * Allay Project 2023/12/2
 *
 * @author daoge_cmd
 */
public class CraftResultDeprecatedActionProcessor implements ItemStackRequestActionProcessor<CraftResultsDeprecatedAction> {
    public static final String NO_RESPONSE_DESTROY_KEY = "noResponseForDestroyAction";

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_RESULTS_DEPRECATED;
    }

    @Nullable
    @Override
    public ItemStackResponse handle(CraftResultsDeprecatedAction action, Player player, ItemStackRequestContext context) {
        context.put(NO_RESPONSE_DESTROY_KEY, true);
        return null;
    }
}
