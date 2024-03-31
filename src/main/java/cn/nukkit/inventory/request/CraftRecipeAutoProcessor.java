package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.inventory.InputInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.request.action.AutoCraftRecipeAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.recipe.Input;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.Registries;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        Inventory inventory = player.getTopWindow().get();
        if (!(player.getTopWindow().isPresent() && inventory instanceof InputInventory craft)) {
            log.error("cant find craft table");
            return context.error();
        }
        var recipe = Registries.RECIPE.getRecipeByNetworkId(action.getRecipeNetworkId());
        List<ItemDescriptor> ingredients = action.getIngredients();
        Item[] eventItems = new Item[9];
        Item[][] items = new Item[3][3];//todo support ItemDescriptor
        for (int i = 0; i < 9; i++) {
            if (ingredients.size() > i) {
                eventItems[i] = ingredients.get(i).toItem();
                items[i / 3][i % 3] = ingredients.get(i).toItem();
            } else {
                eventItems[i] = Item.AIR;
                items[i / 3][i % 3] = Item.AIR;
            }
        }
        Input input = new Input(3, 3, items);

        CraftItemEvent craftItemEvent = new CraftItemEvent(player, eventItems, recipe);
        player.getServer().getPluginManager().callEvent(craftItemEvent);
        if (craftItemEvent.isCancelled()) {
            return context.error();
        }

        var matched = recipe.match(input);
        if (!matched) {
            log.warn("Mismatched recipe! Network id: {},Recipe name: {},Recipe type: {}", action.getRecipeNetworkId(), recipe.getRecipeId(), recipe.getType());
            return context.error();
        } else {
            context.put(RECIPE_DATA_KEY, recipe);
            var consumeActions = findAllConsumeActions(context.getItemStackRequest().getActions(), context.getCurrentActionIndex() + 1);
            var consumeActionCountNeeded = input.canConsumerItemCount();
            if (consumeActions.size() < consumeActionCountNeeded) {
                log.warn("Mismatched consume action count! Expected: " + consumeActionCountNeeded + ", Actual: " + consumeActions.size());
                return context.error();
            }
            if (recipe.getResults().size() == 1) {
                var output = recipe.getResults().get(0);
                output.setCount(output.getCount() * action.getTimesCrafted());
                var createdOutput = player.getCreativeOutputInventory();
                createdOutput.setItem(0, output.clone().autoAssignStackNetworkId(), false);
            }
        }
        return null;
    }
}
