package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.event.inventory.ItemStackRequestActionEvent;
import cn.nukkit.event.player.PlayerTransferItemEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.request.*;
import cn.nukkit.item.Item;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequestSlotInfo;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.DropAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestActionType;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.SwapAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.TransferItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackNetResult;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseContainerInfo;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseInfo;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.response.ItemStackResponseSlotInfo;
import org.cloudburstmc.protocol.bedrock.packet.ItemStackRequestPacket;
import org.cloudburstmc.protocol.bedrock.packet.ItemStackResponsePacket;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author Kaooot
 */
@Slf4j
public class ItemStackRequestHandler implements PacketHandler<ItemStackRequestPacket> {

    static final EnumMap<ItemStackRequestActionType, ItemStackRequestActionProcessor<?>> PROCESSORS = new EnumMap<>(ItemStackRequestActionType.class);

    static {
        PROCESSORS.put(ItemStackRequestActionType.CONSUME, new ConsumeActionProcessor());
        PROCESSORS.put(ItemStackRequestActionType.CRAFT_CREATIVE, new CraftCreativeActionProcessor());
        PROCESSORS.put(ItemStackRequestActionType.CRAFT_RECIPE, new CraftRecipeActionProcessor());
        PROCESSORS.put(ItemStackRequestActionType.CRAFT_RESULTS_DEPRECATED, new CraftResultDeprecatedActionProcessor());
        PROCESSORS.put(ItemStackRequestActionType.CRAFT_RECIPE_AUTO, new CraftRecipeAutoProcessor());
        PROCESSORS.put(ItemStackRequestActionType.CREATE, new CreateActionProcessor());
        PROCESSORS.put(ItemStackRequestActionType.DESTROY, new DestroyActionProcessor());
        PROCESSORS.put(ItemStackRequestActionType.DROP, new DropActionProcessor());
        PROCESSORS.put(ItemStackRequestActionType.PLACE, new PlaceActionProcessor());
        PROCESSORS.put(ItemStackRequestActionType.SWAP, new SwapActionProcessor());
        PROCESSORS.put(ItemStackRequestActionType.TAKE, new TakeActionProcessor());
        PROCESSORS.put(ItemStackRequestActionType.CRAFT_RECIPE_OPTIONAL, new CraftRecipeOptionalProcessor());
        PROCESSORS.put(ItemStackRequestActionType.CRAFT_REPAIR_AND_DISENCHANT, new CraftGrindstoneActionProcessor());
        PROCESSORS.put(ItemStackRequestActionType.MINE_BLOCK, new MineBlockActionProcessor());
        PROCESSORS.put(ItemStackRequestActionType.CRAFT_LOOM, new CraftLoomActionProcessor());
        PROCESSORS.put(ItemStackRequestActionType.BEACON_PAYMENT, new BeaconPaymentActionProcessor());
    }

    @Override
    public void handle(ItemStackRequestPacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        List<ItemStackResponseInfo> responses = new ObjectArrayList<>();

        for (var request : packet.getRequests()) {
            ItemStackRequestAction[] actions = request.getActions();
            ItemStackRequestContext context = new ItemStackRequestContext(request);
            ItemStackResponseInfo itemStackResponse = new ItemStackResponseInfo(ItemStackNetResult.SUCCESS, request.getClientRequestId(), new ObjectArrayList<>());
            Map<ContainerEnumName, ItemStackResponseContainerInfo> responseContainerMap = new LinkedHashMap<>();
            Set<Inventory> affectedInventories = new LinkedHashSet<>();

            for (int index = 0; index < actions.length; index++) {
                ItemStackRequestAction action = actions[index];
                context.setCurrentActionIndex(index);

                ItemStackRequestActionProcessor<ItemStackRequestAction> processor = (ItemStackRequestActionProcessor<ItemStackRequestAction>) PROCESSORS.get(action.getType());

                if (processor == null) {
                    log.warn("Unhandled inventory action type {}", action.getType());
                    continue;
                }

                affectedInventories.addAll(resolveAffectedInventories(player, action));

                ItemStackRequestActionEvent event = new ItemStackRequestActionEvent(player, action, context);
                TransferItemEventCaller.call(event);
                Server.getInstance().getPluginManager().callEvent(event);

                Optional<Inventory> topWindow = player.getTopWindow();
                if (topWindow.isPresent() && topWindow.get() instanceof FakeInventory fakeInventory) {
                    fakeInventory.handle(event);
                }

                ActionResponse response;
                if (event.getResponse() != null) {
                    response = event.getResponse();
                } else if (event.isCancelled()) {
                    response = context.error();
                } else {
                    response = processor.handle(action, player, context);
                }

                if (response != null) {
                    if (!response.ok()) {
                        responses.add(new ItemStackResponseInfo(ItemStackNetResult.ERROR, request.getClientRequestId(), new ObjectArrayList<>()));
                        break;
                    }

                    for (var container : response.containers()) {
                        responseContainerMap.compute(container.getFullContainerName().getContainerName(), (key, oldValue) -> {
                            if (oldValue == null) {
                                return container;
                            } else {
                                oldValue.getSlots().addAll(container.getSlots());
                                return oldValue;
                            }
                        });
                    }
                }
            }

            resyncInventories(player, affectedInventories);
            itemStackResponse.getContainerInfo().addAll(responseContainerMap.values());
            responses.add(itemStackResponse);
        }

        for (var r : responses) {
            for (var c : r.getContainerInfo()) {
                LinkedHashMap<Integer, ItemStackResponseSlotInfo> newItems = new LinkedHashMap<>();
                for (var i : c.getSlots()) {
                    newItems.put(Objects.hash(i.getSlot(), i.getRequestedSlot()), i);
                }
                c.getSlots().clear();
                c.getSlots().addAll(newItems.values());
            }
        }

        var itemStackResponsePacket = new ItemStackResponsePacket();
        itemStackResponsePacket.getResponses().addAll(responses);
        player.sendPacket(itemStackResponsePacket);
    }

    private static Set<Inventory> resolveAffectedInventories(Player player, ItemStackRequestAction action) {
        LinkedHashSet<Inventory> affected = new LinkedHashSet<>();

        try {
            ItemStackRequestSlotInfo source = null;
            ItemStackRequestSlotInfo destination = null;

            switch (action) {
                case TransferItemStackRequestAction transfer -> {
                    source = transfer.getSource();
                    destination = transfer.getDestination();
                }
                case SwapAction swap -> {
                    source = swap.getSource();
                    destination = swap.getDestination();
                }
                case DropAction drop -> source = drop.getSource();
                default -> {
                }
            }

            if (source != null) {
                Inventory sourceInventory = NetworkMapping.getInventory(
                        player,
                        source.getFullContainerName().getContainerName(),
                        source.getFullContainerName().getDynamicID()
                );
                if (sourceInventory != null) {
                    affected.add(sourceInventory);
                }
            }

            if (destination != null) {
                Inventory destinationInventory = NetworkMapping.getInventory(
                        player,
                        destination.getFullContainerName().getContainerName(),
                        destination.getFullContainerName().getDynamicID()
                );
                if (destinationInventory != null) {
                    affected.add(destinationInventory);
                }
            }
        } catch (Throwable t) {
            log.debug("Failed to resolve affected inventories for action {}", action.getType(), t);
        }

        return affected;
    }

    private static void resyncInventories(Player actor, Set<Inventory> inventories) {
        if (inventories == null || inventories.isEmpty()) return;

        for (Inventory inventory : inventories) {
            if (inventory == null) continue;

            try {
                InventoryObserverSync.syncOtherViewers(actor, inventory);
            } catch (Throwable t) {
                log.debug("Failed to resync inventory {}", inventory.getClass().getName(), t);
            }
        }
    }

    private static class TransferItemEventCaller {
        public static void call(ItemStackRequestActionEvent event) {
            ItemStackRequestAction action = event.getAction();

            TransferResult transferResult = handleAction(action);
            if (transferResult == null) return;

            Player player = event.getPlayer();
            Integer dynamicId = transferResult.source.getFullContainerName().getDynamicID();

            Inventory sourceInventory = NetworkMapping.getInventory(player, transferResult.source.getFullContainerName().getContainerName(), dynamicId);
            int sourceSlot = sourceInventory.fromNetworkSlot(transferResult.source.getSlot());

            Optional<Inventory> destinationInventory = transferResult.destination
                    .map(destination -> NetworkMapping.getInventory(player, destination.getFullContainerName().getContainerName(), destination.getFullContainerName().getDynamicID()));

            Optional<Integer> destinationSlot = destinationInventory
                    .flatMap(inventory -> transferResult.destination
                            .map(destination -> inventory.fromNetworkSlot(destination.getSlot())));

            Optional<Item> destinationItem = destinationSlot
                    .flatMap(slot -> destinationInventory.flatMap(inventory -> Optional.of(inventory.getItem(slot))));

            PlayerTransferItemEvent transferEvent = new PlayerTransferItemEvent(
                    player,
                    transferResult.type,
                    sourceInventory.getItem(sourceSlot),
                    destinationItem.orElse(null),
                    sourceSlot,
                    destinationSlot.orElse(-1),
                    sourceInventory,
                    destinationInventory.orElse(null)
            );

            Server.getInstance().getPluginManager().callEvent(transferEvent);
            if (transferEvent.isCancelled()) {
                event.setCancelled(true);
            }
        }

        private static TransferResult handleAction(ItemStackRequestAction action) {
            return switch (action) {
                case TransferItemStackRequestAction transfer -> new TransferResult(
                        transfer.getSource(),
                        Optional.of(transfer.getDestination()),
                        PlayerTransferItemEvent.Type.TRANSFER
                );
                case SwapAction swap -> new TransferResult(
                        swap.getSource(),
                        Optional.of(swap.getDestination()),
                        PlayerTransferItemEvent.Type.SWAP
                );
                case DropAction drop -> new TransferResult(
                        drop.getSource(),
                        Optional.empty(),
                        PlayerTransferItemEvent.Type.DROP
                );
                default -> null;
            };
        }

        private record TransferResult(
                ItemStackRequestSlotInfo source,
                Optional<ItemStackRequestSlotInfo> destination,
                PlayerTransferItemEvent.Type type
        ) {
        }
    }
}