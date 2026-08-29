package org.powernukkitx.inventory.request;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.CraftRecipeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.CreateAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.powernukkitx.Player;
import org.powernukkitx.recipe.Recipe;
import org.powernukkitx.registry.Registries;

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
        Recipe recipe = Registries.RECIPE.getRecipeByNetworkId(((CraftRecipeAction) itemStackRequestAction.get()).getRecipeNetId().getRawId());
        if (recipe == null) {
            log.warn("Recipe with network id {} not found! ItemStackRequest: {}", ((CraftRecipeAction) itemStackRequestAction.get()).getRecipeNetId().getRawId(), context.getItemStackRequest());
            return context.error();
        }
        var output = recipe.getResults().get(action.getResultsIndex());
        var createdOutput = player.getCreativeOutputInventory();
        createdOutput.setItem(0, output, false);
        return null;
    }
}
