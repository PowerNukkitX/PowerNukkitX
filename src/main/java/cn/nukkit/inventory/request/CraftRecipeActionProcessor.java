package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.inventory.InputInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.network.protocol.PlayerEnchantOptionsPacket;
import cn.nukkit.network.protocol.types.itemstack.request.action.ConsumeAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.CraftRecipeAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.recipe.Input;
import cn.nukkit.registry.Registries;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Allay Project 2023/12/1
 *
 * @author daoge_cmd | Cool_Loong
 */
@Slf4j
public class CraftRecipeActionProcessor implements ItemStackRequestActionProcessor<CraftRecipeAction> {
    public static final String RECIPE_DATA_KEY = "recipe";
    public static final String ENCH_RECIPE_KEY = "ench_recipe";

    @Override
    public ActionResponse handle(CraftRecipeAction action, Player player, ItemStackRequestContext context) {
        //handle ench recipe
        if (action.getRecipeNetworkId() >= PlayerEnchantOptionsPacket.ENCH_RECIPEID) {
            EnchantInventory inventory = (EnchantInventory) player.getTopWindow().get();
            PlayerEnchantOptionsPacket.EnchantOptionData enchantOptionData = PlayerEnchantOptionsPacket.RECIPE_MAP.get(action.getRecipeNetworkId());
            if (enchantOptionData == null) {
                log.error("cant find enchant recipe from netId " + action.getRecipeNetworkId());
                return context.error();
            }
            Item first = inventory.getFirst();
            if (first.isNull()) {
                log.error("cant find enchant input!");
                return context.error();
            }
            Item item = first.clone().autoAssignStackNetworkId();
            List<Enchantment> enchantments = enchantOptionData.enchantments();
            item.addEnchantment(enchantments.toArray(Enchantment.EMPTY_ARRAY));
            player.getCreativeOutputInventory().setItem(item);
            PlayerEnchantOptionsPacket.RECIPE_MAP.remove(action.getRecipeNetworkId());
            player.regenerateEnchantmentSeed();
            context.put(ENCH_RECIPE_KEY, true);
            return null;
        }

        InputInventory craft;
        if (player.getTopWindow().isPresent() && player.getTopWindow().get() instanceof InputInventory input) {
            craft = input;
        } else {
            craft = player.getCraftingGrid();
        }
        var recipe = Registries.RECIPE.getRecipeByNetworkId(action.getRecipeNetworkId());
        Input input = craft.getInput();
        var matched = recipe.match(input);
        if (!matched) {
            log.warn("Mismatched recipe! Network id: {},Recipe name: {},Recipe type: {}", action.getRecipeNetworkId(), recipe.getRecipeId(), recipe.getType());
            return context.error();
        } else {
            context.put(RECIPE_DATA_KEY, recipe);
            // Validate the consume action count which client sent
            // 还有一部分检查被放在了ConsumeActionProcessor里面（例如消耗物品数量检查）
            var consumeActions = findAllConsumeActions(context.getItemStackRequest().getActions(), context.getCurrentActionIndex() + 1);
            var consumeActionCountNeeded = input.canConsumerItemCount();
            if (consumeActions.size() != consumeActionCountNeeded) {
                log.warn("Mismatched consume action count! Expected: " + consumeActionCountNeeded + ", Actual: " + consumeActions.size());
                return context.error();
            }
            if (recipe.getResults().size() == 1) {
                // 若配方输出物品为1，客户端将不会发送CreateAction，此时我们直接在CraftRecipeAction输出物品到CREATED_OUTPUT
                // 若配方输出物品为多个，客户端将会发送CreateAction，此时我们将在CreateActionProcessor里面输出物品到CREATED_OUTPUT
                var output = recipe.getResults().get(0);
                var createdOutput = player.getCreativeOutputInventory();
                createdOutput.setItem(0, output.clone().autoAssignStackNetworkId(), false);
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
