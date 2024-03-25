package cn.nukkit.inventory.request;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import cn.nukkit.network.protocol.types.itemstack.request.action.ConsumeAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseContainer;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseSlot;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static cn.nukkit.inventory.request.CraftRecipeActionProcessor.ENCH_RECIPE_KEY;

/**
 * Allay Project 2023/12/1
 *
 * @author daoge_cmd
 */
@Slf4j
public class ConsumeActionProcessor implements ItemStackRequestActionProcessor<ConsumeAction> {
    @Override
    public ActionResponse handle(ConsumeAction action, Player player, ItemStackRequestContext context) {
        // We have validated the recipe in CraftRecipeActionProcessor, so here we can believe the client directly
        var count = action.getCount();
        if (count == 0) {
            log.warn("cannot consume 0 items!");

            return context.error();
        }

        Inventory sourceContainer = NetworkMapping.getInventory(player, action.getSource().getContainer());
        int slot = sourceContainer.fromNetworkSlot(action.getSource().getSlot());
        Item item = sourceContainer.getItem(slot);
        if (validateStackNetworkId(item.getNetId(), action.getSource().getStackNetworkId())) {
            log.warn("mismatch stack network id!");

            return context.error();
        }

        if (item.isNull()) {
            log.warn("cannot consume an air!");

            return context.error();
        }

        if (item.getCount() < count) {
            log.warn("cannot consume more items than the current amount!");

            return context.error();
        }

        if (item.getCount() > count) {
            item.setCount(item.getCount() - count);
            sourceContainer.setItem(slot, item, false);
        } else {
            sourceContainer.clear(slot, false);
            item = sourceContainer.getItem(slot);
        }

        Boolean isEnchRecipe = context.get(ENCH_RECIPE_KEY);
        if (isEnchRecipe != null && isEnchRecipe && action.getSource().getContainer() == ContainerSlotType.ENCHANTING_INPUT) {
            return null;
        }

        ContainerSlotType containerSlotType = sourceContainer.getSlotType(slot);
        if (containerSlotType == null) {
            throw new IllegalStateException("Unknown slot type for slot " + slot + " in inventory " + sourceContainer.getClass().getSimpleName());
        }

        return context.success(List.of(
                new ItemStackResponseContainer(
                        containerSlotType,
                        Lists.newArrayList(
                                new ItemStackResponseSlot(
                                        sourceContainer.toNetworkSlot(slot),
                                        sourceContainer.toNetworkSlot(slot),
                                        item.getCount(),
                                        item.getNetId(),
                                        item.getCustomName(),
                                        item.getDamage()
                                )
                        )
                )
        ));
    }

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CONSUME;
    }
}
