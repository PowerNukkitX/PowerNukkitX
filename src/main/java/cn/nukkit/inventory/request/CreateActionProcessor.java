package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.types.itemstack.request.action.CreateAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.recipe.Recipe;
import lombok.extern.slf4j.Slf4j;

import static cn.nukkit.inventory.request.CraftRecipeActionProcessor.RECIPE_DATA_KEY;

/**
 * Allay Project 2023/12/2
 *
 * @author daoge_cmd
 */
@Slf4j
public class CreateActionProcessor implements ItemStackRequestActionProcessor<CreateAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CREATE;
    }

    @Override
    public ActionResponse handle(CreateAction action, Player player, ItemStackRequestContext context) {
        Recipe recipe = context.get(RECIPE_DATA_KEY);
        if (recipe == null) {
            log.warn("Recipe not found in ItemStackRequest Context!");
            return context.error();
        }
        var output = recipe.getResults().get(action.getSlot());
        var createdOutput = player.getCreativeOutputInventory();
        createdOutput.setItem(0, output, false);
        return null;
    }
}
