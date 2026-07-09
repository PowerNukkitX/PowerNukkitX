package org.powernukkitx.inventory.request;


import org.powernukkitx.Player;
import org.powernukkitx.inventory.InputInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.Recipe;
import org.powernukkitx.recipe.RecipeType;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.CraftResultsDeprecatedAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;

import static org.powernukkitx.inventory.request.CraftRecipeActionProcessor.RECIPE_DATA_KEY;

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
            if (action.getResultItems().length == 0) {
                log.warn("Multi recipe result is missing!");
                return context.error();
            }
            Item resultItem = Item.fromNetwork(action.getResultItems()[0]);
            if (resultItem.isNull() || resultItem.getCount() <= 0 || resultItem.getCount() > resultItem.getMaxStackSize()) {
                log.warn("Invalid multi recipe result {}!", resultItem);
                return context.error();
            }
            if (!validateResultAgainstInput(player, resultItem)) {
                return context.error();
            }
            var createdOutput = player.getCreativeOutputInventory();
            resultItem.autoAssignStackNetworkId();
            createdOutput.setItem(0, resultItem, false);
            return null;
        }
        context.put(NO_RESPONSE_DESTROY_KEY, true);
        return null;
    }

    protected boolean validateResultAgainstInput(Player player, Item resultItem) {
        if (resultItem.getId().equals(Item.FIREWORK_ROCKET) || resultItem.getId().equals(Item.FIREWORK_STAR)) {
            return true;
        }
        Inventory inventory = player.getTopWindow().orElseGet(player::getCraftingGrid);
        InputInventory craft;
        if (inventory instanceof InputInventory input) {
            craft = input;
        } else {
            craft = player.getCraftingGrid();
        }
        int matchingInputCount = 0;
        int totalInputCount = 0;
        for (Item ingredient : craft.getInput().getFlatItems()) {
            if (ingredient.isNull()) continue;
            totalInputCount += ingredient.getCount();
            if (ingredient.getId().equals(resultItem.getId())) {
                matchingInputCount += ingredient.getCount();
            }
        }
        if (matchingInputCount == 0) {
            log.warn("Multi recipe result {} does not match any crafting input!", resultItem);
            return false;
        }
        if (resultItem.getCount() > totalInputCount) {
            log.warn("Multi recipe result count {} exceeds crafting input count {}!", resultItem.getCount(), totalInputCount);
            return false;
        }
        return true;
    }
}
