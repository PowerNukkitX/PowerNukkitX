package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.inventory.CraftingTableInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.network.protocol.types.itemstack.request.action.ConsumeAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.CraftCreativeAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.CraftRecipeAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponse;
import cn.nukkit.registry.Registries;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Allay Project 2023/12/1
 *
 * @author daoge_cmd
 */
@Slf4j
public class CraftRecipeActionProcessor implements ItemStackRequestActionProcessor<CraftRecipeAction> {

    public static final String RECIPE_DATA_KEY = "recipe";

    @Override
    public ItemStackResponse handle(CraftRecipeAction action, Player player, ItemStackRequestContext context) {
        Inventory craft;
        if (player.getTopWindow().isPresent() && player.getTopWindow().get() instanceof CraftingTableInventory) {
            craft = player.getTopWindow().get();
        } else {
            craft = player.getCraftingGrid();
        }
        var recipe = Registries.RECIPE.getRecipeByNetworkId(action.getRecipeNetworkId());
        var matched = recipe(input);
        if (!matched) {
            log.warn("Mismatched recipe! Network id: " + recipe.getNetworkId());
            return error();
        } else {
            dataPool.put(RECIPE_DATA_KEY, recipe);
            // Validate the consume action count which client sent
            // 还有一部分检查被放在了ConsumeActionProcessor里面（例如消耗物品数量检查）
            var consumeActions = findAllConsumeActions(actions, currentActionIndex + 1);
            var consumeActionCountNeeded = craftingContainer.calculateShouldConsumedItemCount();
            if (consumeActions.size() != consumeActionCountNeeded) {
                log.warn("Mismatched consume action count! Expected: " + consumeActionCountNeeded + ", Actual: " + consumeActions.size());
                return error();
            }
            if (recipe.getOutputs().length == 1) {
                // 若配方输出物品为1，客户端将不会发送CreateAction，此时我们直接在CraftRecipeAction输出物品到CREATED_OUTPUT
                // 若配方输出物品为多个，客户端将会发送CreateAction，此时我们将在CreateActionProcessor里面输出物品到CREATED_OUTPUT
                var output = recipe.getOutputs()[0];
                var createdOutput = player.getContainer(CREATED_OUTPUT);
                createdOutput.setItemStack(0, output);
            }
        }
        return null;
    }

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_RECIPE;
    }

    protected List<ConsumeAction> findAllConsumeActions(ItemStackRequestAction[] actions, int startIndex) {
        var found = new ArrayList<ConsumeAction>();
        for (int i = startIndex; i < actions.length; i++) {
            var action = actions[i];
            if (action instanceof ConsumeAction consumeAction) {
                found.add(consumeAction);
            }
        }
        return found;
    }
}
