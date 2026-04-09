package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.inventory.CreativeOutputInventory;
import cn.nukkit.item.Item;
import cn.nukkit.registry.Registries;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.DefaultDescriptor;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.ItemDescriptor;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.ItemDescriptorWithCount;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.ItemTagDescriptor;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.AutoCraftRecipeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.jetbrains.annotations.Nullable;

import static cn.nukkit.inventory.request.CraftRecipeActionProcessor.RECIPE_DATA_KEY;
import static cn.nukkit.inventory.request.CraftRecipeActionProcessor.findAllConsumeActions;

@Slf4j
public class CraftRecipeAutoProcessor implements ItemStackRequestActionProcessor<AutoCraftRecipeAction> {
    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CRAFT_RECIPE_AUTO;
    }

    @Nullable
    @Override
    public ActionResponse handle(AutoCraftRecipeAction action, Player player, ItemStackRequestContext context) {
        var recipe = Registries.RECIPE.getRecipeByNetworkId(action.getRecipeNetworkId());

        Item[] eventItems = action.getIngredients().stream().map(ItemDescriptorWithCount::toItem).map(Item::fromNetwork).toArray(Item[]::new);

        CraftItemEvent craftItemEvent = new CraftItemEvent(player, eventItems, recipe, 1);
        player.getServer().getPluginManager().callEvent(craftItemEvent);
        if (craftItemEvent.isCancelled()) {
            return context.error();
        }

        int success = 0;
        for (Item clientInputItem : eventItems) {
            for (ItemDescriptorWithCount serverExpect : action.getIngredients()) {
                boolean match = false;
                if (serverExpect.getDescriptor() instanceof ItemTagDescriptor tagDescriptor) {
                    match = this.match(tagDescriptor, clientInputItem);
                } else if (serverExpect.getDescriptor() instanceof DefaultDescriptor descriptor) {
                    match = this.match(descriptor, clientInputItem);
                }
                if (match) {
                    success++;
                    break;
                }
            }
        }

        boolean matched = success == action.getIngredients().size();
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
                log.warn("Mismatched consume action count! Expected: {}, Actual: {}", consumeActionCountNeeded, consumeActions.size());
                return context.error();
            }
            if (recipe.getResults().size() == 1) {
                Item output = recipe.getResults().getFirst().clone();
                output.setCount(output.getCount() * action.getTimesCrafted());
                CreativeOutputInventory createdOutput = player.getCreativeOutputInventory();
                createdOutput.setItem(0, output.clone().autoAssignStackNetworkId(), false);
            }
        }
        return null;
    }

    private boolean match(ItemDescriptor descriptor, Item item) {
        return false; // TODO protocol
    }
}
