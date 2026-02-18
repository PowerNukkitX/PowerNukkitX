package cn.nukkit.inventory.request;


import cn.nukkit.Player;
import cn.nukkit.item.Item;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.CraftResultsDeprecatedAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.recipe.Recipe;
import cn.nukkit.recipe.RecipeType;
import lombok.extern.slf4j.Slf4j;

import static cn.nukkit.inventory.request.CraftRecipeActionProcessor.RECIPE_DATA_KEY;

/**
 * Allay Project 2023/12/2
 *
 * @author daoge_cmd | CoolLoong
 */
@Slf4j
public class CraftResultDeprecatedActionProcessor implements ItemStackRequestActionProcessor<CraftResultsDeprecatedAction> {
    public static final String NO_RESPONSE_DESTROY_KEY = "noResponseForDestroyAction";

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_RESULTS_DEPRECATED;
    }

    @Override
    public ActionResponse handle(CraftResultsDeprecatedAction action, Player player, ItemStackRequestContext context) {
        if (context.has(RECIPE_DATA_KEY) && ((Recipe) context.get(RECIPE_DATA_KEY)).getType() == RecipeType.MULTI) {
            return null;
        }
        context.put(NO_RESPONSE_DESTROY_KEY, true);
        return null;
    }
}
