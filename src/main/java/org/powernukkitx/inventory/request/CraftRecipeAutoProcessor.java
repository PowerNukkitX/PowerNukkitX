package org.powernukkitx.inventory.request;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.RecipeIngredient;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.AutoCraftRecipeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ConsumeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.Player;
import org.powernukkitx.event.inventory.CraftItemEvent;
import org.powernukkitx.inventory.CreativeOutputInventory;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.Recipe;
import org.powernukkitx.recipe.UserDataShapelessRecipe;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.recipe.descriptor.ItemDescriptorType;
import org.powernukkitx.registry.Registries;

import java.util.ArrayList;
import java.util.List;

import static org.powernukkitx.inventory.request.CraftRecipeActionProcessor.RECIPE_DATA_KEY;

@Slf4j
public class CraftRecipeAutoProcessor implements ItemStackRequestActionProcessor<AutoCraftRecipeAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_RECIPE_AUTO;
    }

    @Nullable
    @Override
    public ActionResponse handle(AutoCraftRecipeAction action, Player player, ItemStackRequestContext context) {
        Recipe recipe = Registries.RECIPE.getRecipeByNetworkId(action.getRecipeNetId().getRawId());
        if (recipe == null) {
            log.debug("Rejecting auto craft request for unknown recipe network id {} (recipe registry {})",
                    action.getRecipeNetId().getRawId(), Registries.RECIPE.isEnabled() ? "enabled" : "disabled");
            return context.error();
        }

        List<ItemDescriptor> ingredients = recipe.getIngredients();
        if (ingredients.isEmpty()) {
            log.debug("Rejecting auto craft request for recipe {} which carries no server side ingredients", recipe.getRecipeId());
            return context.error();
        }

        int timesCrafted = resolveTimesCrafted(action);
        if (timesCrafted < 1) {
            log.debug("Rejecting auto craft request for recipe {} with invalid craft count {}", recipe.getRecipeId(), timesCrafted);
            return context.error();
        }

        Item[] eventItems = action.getIngredients().stream().map(RecipeIngredient::toItem).map(Item::fromNetwork).toArray(Item[]::new);

        CraftItemEvent craftItemEvent = new CraftItemEvent(player, eventItems, recipe, timesCrafted);
        player.getServer().getPluginManager().callEvent(craftItemEvent);
        if (craftItemEvent.isCancelled()) {
            return context.error();
        }

        var consumeActions = findAllConsumeActions(context.getItemStackRequest().getActions(), context.getCurrentActionIndex() + 1);
        if (!consumeActionsCoverRecipe(player, ingredients, consumeActions, timesCrafted)) {
            log.warn("Mismatched recipe! Network id: {},Recipe name: {},Recipe type: {}", action.getRecipeNetId(), recipe.getRecipeId(), recipe.getType());
            return context.error();
        }

        context.put(RECIPE_DATA_KEY, recipe);
        player.getRecipeBook().unlock(recipe);

        if (recipe.getResults().size() == 1) {
            Item output = recipe.getResults().getFirst().clone();
            if (recipe instanceof UserDataShapelessRecipe) {
                for (Item inputItem : eventItems) {
                    if (!inputItem.isNull() && inputItem.hasNbt()) {
                        output.setNbtBytes(inputItem.getNbtBytes());
                        break;
                    }
                }
            }
            int outputCount = output.getCount() * timesCrafted;
            if (outputCount > output.getMaxStackSize()) {
                log.warn("Rejecting auto craft request for recipe {}: output count {} exceeds the maximum stack size {}",
                        recipe.getRecipeId(), outputCount, output.getMaxStackSize());
                return context.error();
            }
            output.setCount(outputCount);
            CreativeOutputInventory createdOutput = player.getCreativeOutputInventory();
            createdOutput.setItem(0, output.clone().autoAssignStackNetworkId(), false);
        }
        return null;
    }

    private static int resolveTimesCrafted(AutoCraftRecipeAction action) {
        int timesCrafted = action.getTimesCrafted();
        if (timesCrafted > 0) {
            return timesCrafted;
        }
        int numberOfRequestedCrafts = action.getNumberOfRequestedCrafts();
        if (numberOfRequestedCrafts > 0) {
            return numberOfRequestedCrafts;
        }
        log.debug("Auto craft request carries no usable craft count (timesCrafted {}, numberOfRequestedCrafts {}), falling back to 1",
                timesCrafted, numberOfRequestedCrafts);
        return 1;
    }

    private static boolean consumeActionsCoverRecipe(Player player, List<ItemDescriptor> ingredients, List<ConsumeAction> consumeActions, int timesCrafted) {
        int[] available = new int[consumeActions.size()];
        Item[] consumedItems = new Item[consumeActions.size()];
        for (int i = 0; i < consumeActions.size(); i++) {
            ConsumeAction consumeAction = consumeActions.get(i);
            Item item = resolveConsumedItem(player, consumeAction);
            if (item == null || item.isNull()) {
                continue;
            }
            consumedItems[i] = item;
            available[i] = Math.min(consumeAction.getAmount(), item.getCount());
        }

        for (ItemDescriptor ingredient : ingredients) {
            if (ingredient.getType() != ItemDescriptorType.DEFAULT && ingredient.getType() != ItemDescriptorType.ITEM_TAG) {
                continue;
            }
            int required = Math.max(ingredient.getCount(), 1) * timesCrafted;
            for (int i = 0; i < consumedItems.length && required > 0; i++) {
                Item item = consumedItems[i];
                if (item == null || available[i] <= 0 || !ingredient.match(item)) {
                    continue;
                }
                int taken = Math.min(required, available[i]);
                available[i] -= taken;
                required -= taken;
            }
            if (required > 0) {
                log.debug("Auto craft request does not consume enough items for ingredient {}", ingredient);
                return false;
            }
        }
        return true;
    }

    @Nullable
    private static Item resolveConsumedItem(Player player, ConsumeAction action) {
        try {
            var containerName = action.getSource().getFullContainerName();
            Inventory inventory = NetworkMapping.getInventory(player, containerName.getContainerName(), containerName.getDynamicID());
            if (inventory == null) {
                return null;
            }
            int slot = inventory.fromNetworkSlot(action.getSource().getSlot());
            if (slot < 0 || slot >= inventory.getSize()) {
                return null;
            }
            return inventory.getUnclonedItem(slot);
        } catch (Throwable t) {
            log.debug("Failed to resolve the source item of an auto craft consume action", t);
            return null;
        }
    }

    private static List<ConsumeAction> findAllConsumeActions(ItemStackRequestAction[] actions, int startIndex) {
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
