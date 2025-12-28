package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.network.protocol.types.itemstack.request.action.CraftRecipeAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.CreateAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.recipe.Recipe;
import cn.nukkit.registry.Registries;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

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
        Optional<ItemStackRequestAction> itemStackRequestAction = Arrays.stream(context.getItemStackRequest().getActions()).filter(action1 -> action1 instanceof CraftRecipeAction).findFirst();
        if (itemStackRequestAction.isEmpty()) {
            log.warn("Recipe not found in ItemStackRequest Context! Context: {}", context);
            return context.error();
        }
        Recipe recipe = Registries.RECIPE.getRecipeByNetworkId(((CraftRecipeAction) itemStackRequestAction.get()).getRecipeNetworkId());
        var output = recipe.getResults().get(action.getSlot());
        var createdOutput = player.getCreativeOutputInventory();
        createdOutput.setItem(0, output, false);
        return null;
    }
}
