package cn.nukkit.inventory.fake;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.inventory.ItemStackRequestActionEvent;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.InputInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.request.NetworkMapping;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import cn.nukkit.network.protocol.types.itemstack.request.ItemStackRequestSlotData;
import cn.nukkit.network.protocol.types.itemstack.request.action.ItemStackRequestAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.SwapAction;
import cn.nukkit.network.protocol.types.itemstack.request.action.TransferItemStackRequestAction;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.recipe.Input;
import com.google.common.collect.BiMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FakeInventory extends BaseInventory implements InputInventory {
    private final Map<Integer, ItemHandler> handlers = new HashMap<>();
    private final FakeBlock fakeBlock;
    private final FakeInventoryType fakeInventoryType;
    private String title;
    private ItemHandler defaultItemHandler;

    public FakeInventory(FakeInventoryType fakeInventoryType) {
        this(fakeInventoryType, fakeInventoryType.name());
    }

    public FakeInventory(FakeInventoryType fakeInventoryType, @NotNull String title) {
        super(null, fakeInventoryType.inventoryType, fakeInventoryType.size);
        this.title = title;
        this.fakeInventoryType = fakeInventoryType;
        this.fakeBlock = fakeInventoryType.fakeBlock;
        this.defaultItemHandler = (a, b, c, d, e) -> {
        };

        switch (fakeInventoryType) {
            case CHEST, DOUBLE_CHEST, HOPPER, DISPENSER, DROPPER, ENDER_CHEST -> {
                Map<Integer, ContainerSlotType> map = super.slotTypeMap();
                for (int i = 0; i < getSize(); i++) {
                    map.put(i, ContainerSlotType.LEVEL_ENTITY);
                }
            }
            case FURNACE -> {
                Map<Integer, ContainerSlotType> map = super.slotTypeMap();
                map.put(0, ContainerSlotType.FURNACE_INGREDIENT);
                map.put(1, ContainerSlotType.FURNACE_FUEL);
                map.put(2, ContainerSlotType.FURNACE_RESULT);
            }
            case BREWING_STAND -> {
                Map<Integer, ContainerSlotType> map = super.slotTypeMap();
                map.put(0, ContainerSlotType.BREWING_INPUT);
                map.put(1, ContainerSlotType.BREWING_RESULT);
                map.put(2, ContainerSlotType.BREWING_RESULT);
                map.put(3, ContainerSlotType.BREWING_RESULT);
                map.put(4, ContainerSlotType.BREWING_FUEL);
            }
            case SHULKER_BOX -> {
                Map<Integer, ContainerSlotType> map = super.slotTypeMap();
                for (int i = 0; i < getSize(); i++) {
                    map.put(i, ContainerSlotType.SHULKER_BOX);
                }
            }
            case WORKBENCH -> {
                BiMap<Integer, Integer> map = super.networkSlotMap();
                for (int i = 0; i < getSize(); i++) {
                    map.put(i, 32 + i);
                }
                Map<Integer, ContainerSlotType> map2 = super.slotTypeMap();
                for (int i = 0; i < getSize(); i++) {
                    map2.put(i, ContainerSlotType.CRAFTING_INPUT);
                }
            }
        }
    }

    @Override
    public InventoryHolder getHolder() {
        return null;
    }

    public FakeInventoryType getFakeInventoryType() {
        return fakeInventoryType;
    }

    @Override
    public void onOpen(Player player) {
        player.setFakeInventoryOpen(true);
        this.fakeBlock.create(player, this.getTitle());
        Server.getInstance().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
            ContainerOpenPacket packet = new ContainerOpenPacket();
            packet.windowId = player.getWindowId(this);
            packet.type = this.getType().getNetworkType();

            Optional<Vector3> first = this.fakeBlock.getLastPositions().stream().findFirst();
            if (first.isPresent()) {
                Vector3 position = first.get();
                packet.x = position.getFloorX();
                packet.y = position.getFloorY();
                packet.z = position.getFloorZ();
                player.dataPacket(packet);

                super.onOpen(player);
                this.sendContents(player);
            } else {
                this.fakeBlock.remove(player);
            }
        }, 5);
    }

    @Override
    public void onClose(Player player) {
        ContainerClosePacket packet = new ContainerClosePacket();
        packet.windowId = player.getWindowId(this);
        packet.wasServerInitiated = player.getClosingWindowId() != packet.windowId;
        packet.type = getType();
        player.dataPacket(packet);
        Server.getInstance().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
            this.fakeBlock.remove(player);
        }, 5);
        super.onClose(player);
        player.setFakeInventoryOpen(false);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setItemHandler(int index, ItemHandler handler) {
        this.handlers.put(index, handler);
    }

    public void setDefaultItemHandler(ItemHandler defaultItemHandler) {
        this.defaultItemHandler = defaultItemHandler;
    }

    @ApiStatus.Internal
    public void handle(ItemStackRequestActionEvent event) {
        ItemStackRequestAction action = event.getAction();
        ItemStackRequestSlotData source = null;
        ItemStackRequestSlotData destination = null;
        if (action instanceof TransferItemStackRequestAction transferItemStackRequestAction) {
            source = transferItemStackRequestAction.getSource();
            destination = transferItemStackRequestAction.getDestination();
        } else if (action instanceof SwapAction swapAction) {
            source = swapAction.getSource();
            destination = swapAction.getDestination();
        }
        if (source != null && destination != null) {
            ContainerSlotType sourceSlotType = source.getContainer();
            ContainerSlotType destinationSlotType = destination.getContainer();
            Inventory sourceI = NetworkMapping.getInventory(event.getPlayer(), sourceSlotType);
            Inventory destinationI = NetworkMapping.getInventory(event.getPlayer(), destinationSlotType);
            int sourceSlot = sourceI.fromNetworkSlot(source.getSlot());
            int destinationSlot = destinationI.fromNetworkSlot(destination.getSlot());
            var sourItem = sourceI.getItem(sourceSlot);
            var destItem = destinationI.getItem(destinationSlot);
            if (sourceI.equals(this)) {
                ItemHandler handler = this.handlers.getOrDefault(sourceSlot, this.defaultItemHandler);
                handler.handle(this, sourceSlot, sourItem, Item.AIR, event);
            } else if (destinationI.equals(this)) {
                ItemHandler handler = this.handlers.getOrDefault(destinationSlot, this.defaultItemHandler);
                handler.handle(this, destinationSlot, destItem, sourItem, event);
            }
        }
    }

    @Override
    public Input getInput() {
        if (fakeInventoryType == FakeInventoryType.WORKBENCH) {
            Item[] item1 = List.of(getItem(0), getItem(1), getItem(2)).toArray(Item.EMPTY_ARRAY);
            Item[] item2 = List.of(getItem(3), getItem(4), getItem(5)).toArray(Item.EMPTY_ARRAY);
            Item[] item3 = List.of(getItem(6), getItem(7), getItem(8)).toArray(Item.EMPTY_ARRAY);
            Item[][] items = new Item[][]{item1, item2, item3};
            return new Input(3, 3, items);
        } else {
            return new Input(3, 3, new Item[3][3]);
        }
    }
}
