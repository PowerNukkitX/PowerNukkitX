package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.request.action.AutoCraftRecipeAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.recipe.descriptor.ItemTagDescriptor;
import cn.nukkit.registry.Registries;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import static cn.nukkit.inventory.request.CraftRecipeActionProcessor.RECIPE_DATA_KEY;
import static cn.nukkit.inventory.request.CraftRecipeActionProcessor.findAllConsumeActions;
import static cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType.CRAFT_RECIPE_AUTO;

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

        Item[] eventItems = action.getIngredients().stream().map(ItemDescriptor::toItem).toArray(Item[]::new);

        CraftItemEvent craftItemEvent = new CraftItemEvent(player, eventItems, recipe);
        player.getServer().getPluginManager().callEvent(craftItemEvent);
        if (craftItemEvent.isCancelled()) {
            return context.error();
        }

        int success = 0;
        for (var clientInputItem : eventItems) {
            for (var serverExpect : action.getIngredients()) {
                boolean match = false;
                if (serverExpect instanceof ItemTagDescriptor tagDescriptor) {
                    match = tagDescriptor.match(clientInputItem);
                } else if (serverExpect instanceof DefaultDescriptor descriptor) {
                    match = descriptor.match(clientInputItem);
                }
                if (match) {
                    success++;
                    break;
                }
            }
        }

        var matched = success == action.getIngredients().size();
        if (!matched) {
            log.warn("Mismatched recipe! Network id: {},Recipe name: {},Recipe type: {}", action.getRecipeNetworkId(), recipe.getRecipeId(), recipe.getType());
            return context.error();
        } else {
            context.put(RECIPE_DATA_KEY, recipe);
            var consumeActions = findAllConsumeActions(context.getItemStackRequest().getActions(), context.getCurrentActionIndex() + 1);

            int consumeActionCountNeeded = 0;
            for (var item : eventItems) {
                if (!item.isNull()) {
                    consumeActionCountNeeded++;
                }
            }
            if (consumeActions.size() < consumeActionCountNeeded) {
                log.warn("Mismatched consume action count! Expected: " + consumeActionCountNeeded + ", Actual: " + consumeActions.size());
                return context.error();
            }
            if (recipe.getResults().size() == 1) {
                var output = recipe.getResults().getFirst();
                output.setCount(output.getCount() * action.getTimesCrafted());
                var createdOutput = player.getCreativeOutputInventory();
                createdOutput.setItem(0, output.clone().autoAssignStackNetworkId(), false);
            }
        }
        return null;
    }
}
