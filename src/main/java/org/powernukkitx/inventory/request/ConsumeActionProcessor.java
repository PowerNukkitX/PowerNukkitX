package org.powernukkitx.inventory.request;

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
import java.util.Set;

import static org.powernukkitx.inventory.request.CraftRecipeActionProcessor.ENCH_RECIPE_KEY;
import static org.powernukkitx.inventory.request.CraftRecipeActionProcessor.GRID_CONSUMED_KEY;

/**
 * Allay Project 2023/12/1
 *
 * @author daoge_cmd
 */
@Slf4j
public class ConsumeActionProcessor implements ItemStackRequestActionProcessor<ConsumeAction> {

    private static final Set<ContainerEnumName> SERVER_CONSUMED_CONTAINERS = Set.of(
            ContainerEnumName.ANVIL_INPUT_CONTAINER,
            ContainerEnumName.ANVIL_MATERIAL_CONTAINER,
            ContainerEnumName.GRINDSTONE_INPUT_CONTAINER,
            ContainerEnumName.GRINDSTONE_ADDITIONAL_CONTAINER,
            ContainerEnumName.CARTOGRAPHY_INPUT_CONTAINER,
            ContainerEnumName.CARTOGRAPHY_ADDITIONAL_CONTAINER,
            ContainerEnumName.SMITHING_TABLE_INPUT_CONTAINER,
            ContainerEnumName.SMITHING_TABLE_MATERIAL_CONTAINER,
            ContainerEnumName.SMITHING_TABLE_TEMPLATE_CONTAINER,
            ContainerEnumName.LOOM_INPUT_CONTAINER,
            ContainerEnumName.LOOM_DYE_CONTAINER,
            ContainerEnumName.LOOM_MATERIAL_CONTAINER,
            ContainerEnumName.ENCHANTING_INPUT_CONTAINER,
            ContainerEnumName.ENCHANTING_MATERIAL_CONTAINER,
            ContainerEnumName.TRADE2_INGREDIENT1_CONTAINER,
            ContainerEnumName.TRADE2_INGREDIENT2_CONTAINER
    );

    @Override
    public ActionResponse handle(ConsumeAction action, Player player, ItemStackRequestContext context) {
        var count = action.getCount();
        if (count == 0) {
            log.warn("cannot consume 0 items!");

            return context.error();
        }
        FullContainerName containerName = action.getSource().getFullContainerName();
        Integer dynamicId = containerName.getDynamicID();
        Inventory sourceContainer = NetworkMapping.getInventory(player, containerName.getContainerName(), dynamicId);
        int slot = sourceContainer.fromNetworkSlot(action.getSource().getSlot());
        Item item = sourceContainer.getItem(slot);
        ContainerEnumName sourceContainerType = containerName.getContainerName();
        boolean serverConsumed = SERVER_CONSUMED_CONTAINERS.contains(sourceContainerType)
                || (sourceContainerType == ContainerEnumName.CRAFTING_INPUT_CONTAINER && Boolean.TRUE.equals(context.get(GRID_CONSUMED_KEY)));
        if (serverConsumed) {
            return context.success(List.of(
                    new ItemStackResponseContainerInfo(
                            sourceContainer.getContainerEnumName(slot),
                            Lists.newArrayList(
                                    new ItemStackResponseSlotInfo(
                                            sourceContainer.toNetworkSlot(slot),
                                            sourceContainer.toNetworkSlot(slot),
                                            item.getCount(),
                                            item.getNetId(),
                                            item.getCustomName(),
                                            item.getDamage(),
                                            ""
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
        if (isEnchRecipe != null && isEnchRecipe && containerName.getContainerName() == ContainerEnumName.ENCHANTING_INPUT_CONTAINER) {
            return null;
        }

        ContainerEnumName containerEnumName = sourceContainer.getContainerEnumName(slot);
        if (containerEnumName == null) {
            throw new IllegalStateException("Unknown slot type for slot " + slot + " in inventory " + sourceContainer.getClass().getSimpleName());
        }

        return context.success(List.of(
                new ItemStackResponseContainerInfo(
                        containerEnumName,
                        Lists.newArrayList(
                                new ItemStackResponseSlotInfo(
                                        sourceContainer.toNetworkSlot(slot),
                                        sourceContainer.toNetworkSlot(slot),
                                        item.getCount(),
                                        item.getNetId(),
                                        item.getCustomName(),
                                        item.getDamage(),
                                        ""
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
