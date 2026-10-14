package org.powernukkitx.inventory.request;

import org.cloudburstmc.protocol.bedrock.data.payload.common.RedactableString;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.net.ItemStackNetId;
import org.powernukkitx.Player;
import org.powernukkitx.inventory.Inventory;
import org.powernukkitx.item.Item;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.FullContainerName;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ConsumeAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainerInfo;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseSlotInfo;

import java.util.List;

import static org.powernukkitx.inventory.request.CraftRecipeActionProcessor.ENCH_RECIPE_KEY;

/**
 * Allay Project 2023/12/1
 *
 * @author daoge_cmd
 */
@Slf4j
public class ConsumeActionProcessor implements ItemStackRequestActionProcessor<ConsumeAction> {

    @Override
    public ActionResponse handle(ConsumeAction action, Player player, ItemStackRequestContext context) {
        var count = action.getAmount();
        if (count == 0) {
            log.warn("cannot consume 0 items!");

            return context.error();
        }
        FullContainerName containerName = action.getSource().getFullContainerName();
        Integer dynamicId = containerName.getDynamicID();
        Inventory sourceContainer = NetworkMapping.getInventory(player, containerName.getContainerName(), dynamicId);
        int slot = sourceContainer.fromNetworkSlot(action.getSource().getSlot());
        if (slot < 0 || slot >= sourceContainer.getSize()) {
            log.warn("consume action points at slot {} which is outside of {}", slot, sourceContainer.getClass().getSimpleName());

            return context.error();
        }
        Item item = sourceContainer.getItem(slot);
        ContainerEnumName sourceSlotType = resolveSlotType(sourceContainer, slot);
        if (sourceSlotType == null) {
            log.warn("unknown slot type for slot {} in inventory {}", slot, sourceContainer.getClass().getSimpleName());

            return context.error();
        }
        // Only slots a station processor declared as taken in this very request are skipped.
        if (context.isServerConsumed(sourceSlotType)) {
            return context.success(List.of(
                new ItemStackResponseContainerInfo(
                    sourceSlotType,
                    Lists.newArrayList(
                        new ItemStackResponseSlotInfo(
                            sourceContainer.toNetworkSlot(slot),
                            sourceContainer.toNetworkSlot(slot),
                            item.getCount(),
                            new ItemStackNetId(item.getNetId()),
                            new RedactableString(item.getCustomName(), ""),
                            item.getDamage()
                        )
                    ),
                    containerName
                )
            ));
        }
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
        if (isEnchRecipe != null && isEnchRecipe && sourceSlotType == ContainerEnumName.ENCHANTING_INPUT_CONTAINER) {
            return null;
        }

        return context.success(List.of(
            new ItemStackResponseContainerInfo(
                sourceSlotType,
                Lists.newArrayList(
                    new ItemStackResponseSlotInfo(
                        sourceContainer.toNetworkSlot(slot),
                        sourceContainer.toNetworkSlot(slot),
                        item.getCount(),
                        new ItemStackNetId(item.getNetId()),
                        new RedactableString(item.getCustomName(), ""),
                        item.getDamage()
                    )
                ),
                containerName
            )
        ));
    }

    @Override
    public ItemStackRequestActionType getType() {
        return ItemStackRequestActionType.CONSUME;
    }
}
