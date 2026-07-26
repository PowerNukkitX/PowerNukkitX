package org.powernukkitx.inventory.request;

import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.ItemDescriptor;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.ItemTagDescriptor;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.NameDescriptor;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.RecipeIngredient;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.AutoCraftRecipeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ConsumeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.jetbrains.annotations.Nullable;
import org.powernukkitx.Player;
import org.powernukkitx.event.inventory.CraftItemEvent;
import org.powernukkitx.inventory.CreativeOutputInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.UserDataShapelessRecipe;
import org.powernukkitx.recipe.descriptor.InvalidDescriptor;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.ItemTags;

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
        var recipe = Registries.RECIPE.getRecipeByNetworkId(action.getRecipeNetId().getRawId());
        if (recipe == null) {
            log.debug("Rejecting auto craft request for unknown recipe network id {} (recipe registry {})",
                    action.getRecipeNetId().getRawId(), Registries.RECIPE.isEnabled() ? "enabled" : "disabled");
            return context.error();
        }

        Item[] eventItems = action.getIngredients().stream().map(RecipeIngredient::toItem).map(Item::fromNetwork).toArray(Item[]::new);

        CraftItemEvent craftItemEvent = new CraftItemEvent(player, eventItems, recipe, 1);
        player.getServer().getPluginManager().callEvent(craftItemEvent);
        if (craftItemEvent.isCancelled()) {
            return context.error();
        }

        int success = 0;
        for (Item clientInputItem : eventItems) {
            for (RecipeIngredient serverExpect : action.getIngredients()) {
                boolean match = false;
                if (serverExpect.getDescriptor() instanceof ItemTagDescriptor tagDescriptor) {
                    match = this.match(serverExpect, tagDescriptor, clientInputItem);
                } else if (serverExpect.getDescriptor() instanceof NameDescriptor descriptor) {
                    match = this.match(serverExpect, descriptor, clientInputItem);
                } else if (serverExpect.getDescriptor() instanceof InvalidDescriptor) {
                    match = clientInputItem.equals(Item.AIR);
                }
                if (match) {
                    success++;
                    break;
                }
            }
        }

        boolean matched = success == action.getIngredients().size();
        if (!matched) {
            log.warn("Mismatched recipe! Network id: {},Recipe name: {},Recipe type: {}", action.getRecipeNetId(), recipe.getRecipeId(), recipe.getType());
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
                log.warn("Mismatched consume action count! Expected: {}, Actual: {}", consumeActionCountNeeded, consumeActions.size());
                return context.error();
            }
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
                output.setCount(output.getCount() * action.getTimesCrafted());
                CreativeOutputInventory createdOutput = player.getCreativeOutputInventory();
                createdOutput.setItem(0, output.clone().autoAssignStackNetworkId(), false);
            }
        }
        return null;
    }

    private boolean match(RecipeIngredient descriptorWithCount, ItemDescriptor descriptor, Item item) {
        if (descriptor instanceof ItemTagDescriptor tagDescriptor) {
            return item.getCount() >= descriptorWithCount.getStackSize() && ItemTags.getTagSet(item.getId()).contains(tagDescriptor.getItemTag());
        } else if (descriptor instanceof NameDescriptor defaultDescriptor) {
            final ItemData itemData = ItemData.builder()
                .definition(defaultDescriptor.getItemId())
                .damage(defaultDescriptor.getAuxValue())
                .build();
            return Item.fromNetwork(itemData).equals(item, true, false);
        }
        return false;
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
