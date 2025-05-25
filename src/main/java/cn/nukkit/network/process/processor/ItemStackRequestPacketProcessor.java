package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.event.inventory.ItemStackRequestActionEvent;
import cn.nukkit.event.player.PlayerTransferItemEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.fake.FakeInventory;
import cn.nukkit.inventory.request.*;
import cn.nukkit.item.Item;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ItemStackRequestPacket;
import cn.nukkit.network.protocol.ItemStackResponsePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequestSlotData;
import cn.nukkit.network.protocol.types.itemstack.request.action.DropAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestActionType;
import cn.nukkit.network.protocol.types.itemstack.request.action.SwapAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.TransferItemStackRequestAction;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponse;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseContainer;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseSlot;
import cn.nukkit.network.protocol.types.itemstack.response.ItemStackResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class ItemStackRequestPacketProcessor extends DataPacketProcessor<ItemStackRequestPacket> {
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
    @SuppressWarnings("unchecked")
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ItemStackRequestPacket pk) {
        Player player = playerHandle.player;
        List<ItemStackResponse> responses = new ArrayList<>();
        for (var request : pk.requests) {
            ItemStackRequestAction[] actions = request.getActions();
            ItemStackRequestContext context = new ItemStackRequestContext(request);
            ItemStackResponse itemStackResponse = new ItemStackResponse(ItemStackResponseStatus.OK, request.getRequestId(), new ArrayList<>());
            Map<ContainerSlotType, ItemStackResponseContainer> responseContainerMap = new LinkedHashMap<>();
            for (int index = 0; index < actions.length; index++) {
                ItemStackRequestAction action = actions[index];
                context.setCurrentActionIndex(index);
                ItemStackRequestActionProcessor<ItemStackRequestAction> processor = (ItemStackRequestActionProcessor<ItemStackRequestAction>) PROCESSORS.get(action.getType());
                if (processor == null) {
                    log.warn("Unhandled inventory action type {}", action.getType());
                    continue;
                }

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
                        itemStackResponse.setResult(ItemStackResponseStatus.ERROR);
                        itemStackResponse.getContainers().clear();
                        responses.add(itemStackResponse);
                        break;
                    }

                    for (var container : response.containers()) {
                        responseContainerMap.compute(container.getContainerName().getContainer(), (key, oldValue) -> {
                            if (oldValue == null) {
                                return container;
                            } else {
                                oldValue.getItems().addAll(container.getItems());
                                return oldValue;
                            }
                        });
                    }
                }
            }
            itemStackResponse.getContainers().addAll(responseContainerMap.values());
            responses.add(itemStackResponse);
        }

        for (var r : responses) {
            for (var c : r.getContainers()) {
                LinkedHashMap<Integer, ItemStackResponseSlot> newItems = new LinkedHashMap<>();
                for (var i : c.getItems()) {
                    newItems.put(Objects.hash(i.getSlot(), i.getHotbarSlot()), i);
                }
                c.getItems().clear();
                c.getItems().addAll(newItems.values());
            }
        }

        var itemStackResponsePacket = new ItemStackResponsePacket();
        itemStackResponsePacket.entries.addAll(responses);
        player.dataPacket(itemStackResponsePacket);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.ITEM_STACK_REQUEST_PACKET;
    }


    private static class TransferItemEventCaller {

        public static void call(ItemStackRequestActionEvent event) {
            ItemStackRequestAction action = event.getAction();

            TransferResult transferResult = handleAction(action);
            if (transferResult == null)
                return;

            Player player = event.getPlayer();
            Integer dynamicId = transferResult.source.getContainerName().getDynamicId();

            Inventory sourceInventory = NetworkMapping.getInventory(player, transferResult.source.getContainerName().getContainer(), dynamicId);
            int sourceSlot = sourceInventory.fromNetworkSlot(transferResult.source.getSlot());

            Optional<Inventory> destinationInventory = transferResult.destination
                    .map(destination -> NetworkMapping.getInventory(player, destination.getContainerName().getContainer(), destination.getContainerName().getDynamicId()));
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
                ItemStackRequestSlotData source,
                Optional<ItemStackRequestSlotData> destination,
                PlayerTransferItemEvent.Type type
        ) {
        }

    }
}
