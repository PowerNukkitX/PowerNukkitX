package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.inventory.CreativeOutputInventory;
import cn.nukkit.item.Item;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.AutoCraftRecipeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.registry.Registries;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import static cn.nukkit.inventory.request.CraftRecipeActionProcessor.RECIPE_DATA_KEY;
import static cn.nukkit.inventory.request.CraftRecipeActionProcessor.findAllConsumeActions;
import static org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType.CRAFT_RECIPE_AUTO;

@Slf4j
public class CraftRecipeAutoProcessor implements ItemStackRequestActionProcessor<AutoCraftRecipeAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return CRAFT_RECIPE_AUTO;
    }

    @Nullable
    @Override
    public ActionResponse handle(AutoCraftRecipeAction action, Player player, ItemStackRequestContext context) {
        var recipe = Registries.RECIPE.getRecipeByNetworkId(action.getRecipeNetworkId());
        if (recipe == null) {
            return context.error();
        }
        Item[] eventItems = Item.EMPTY_ARRAY;

        CraftItemEvent craftItemEvent = new CraftItemEvent(player, eventItems, recipe, 1);
        player.getServer().getPluginManager().callEvent(craftItemEvent);
        if (craftItemEvent.isCancelled()) {
            return context.error();
        }

        context.put(RECIPE_DATA_KEY, recipe);
        var consumeActions = findAllConsumeActions(context.getItemStackRequest().getActions(), context.getCurrentActionIndex() + 1);
        if (recipe.getResults().size() == 1) {
            Item output = recipe.getResults().getFirst().clone();
            output.setCount(output.getCount() * action.getTimesCrafted());
            CreativeOutputInventory createdOutput = player.getCreativeOutputInventory();
            createdOutput.setItem(0, output.clone().autoAssignStackNetworkId(), false);
        }
        if (consumeActions.isEmpty()) {
            log.warn("Missing consume actions for auto craft recipe {}", recipe.getRecipeId());
            return context.error();
        }
        return null;
    }
}
