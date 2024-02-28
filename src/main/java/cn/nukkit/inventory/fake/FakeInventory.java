package cn.nukkit.inventory.fake;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.inventory.ItemStackRequestActionEvent;
import cn.nukkit.inventory.BaseInventory;
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
import com.google.common.collect.BiMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FakeInventory extends BaseInventory {
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
        this.defaultItemHandler = (i, ii, e) -> {
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
        throw new UnsupportedOperationException();
    }

    public FakeInventoryType getFakeInventoryType() {
        return fakeInventoryType;
    }

    @Override
    public void onOpen(Player player) {
        this.fakeBlock.create(player, this.getTitle());

        Server.getInstance().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
            ContainerOpenPacket packet = new ContainerOpenPacket();
            packet.windowId = player.getWindowId(this);
            packet.type = this.getType().getNetworkType();

            Vector3 position = this.fakeBlock.getPlacePositions(player).get(0);
            packet.x = position.getFloorX();
            packet.y = position.getFloorY();
            packet.z = position.getFloorZ();
            player.dataPacket(packet);

            super.onOpen(player);

            this.sendContents(player);
        }, 5);
    }

    @Override
    public void onClose(Player player) {
        ContainerClosePacket packet = new ContainerClosePacket();
        packet.windowId = player.getWindowId(this);
        packet.wasServerInitiated = player.getClosingWindowId() != packet.windowId;
        player.dataPacket(packet);

        super.onClose(player);

        Server.getInstance().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
            this.fakeBlock.remove(player);
        }, 5);
    }

    public Item[] addItem(ItemHandler handler, Item... slots) {
        List<Item> itemSlots = new ArrayList<>();
        for (Item slot : slots) {
            if (!slot.isNull()) {
                //todo: clone only if necessary
                itemSlots.add(slot.clone());
            }
        }

        //使用FastUtils的IntArrayList提高性能
        IntList emptySlots = new IntArrayList(this.getSize());

        for (int i = 0; i < this.getSize(); ++i) {
            //获取未克隆Item对象
            Item item = this.getUnclonedItem(i);
            if (item.isNull() || item.getCount() <= 0) {
                emptySlots.add(i);
            }

            //使用迭代器而不是新建一个ArrayList
            for (Iterator<Item> iterator = itemSlots.iterator(); iterator.hasNext(); ) {
                Item slot = iterator.next();
                if (slot.equals(item)) {
                    int maxStackSize = Math.min(this.getMaxStackSize(), item.getMaxStackSize());
                    if (item.getCount() < maxStackSize) {
                        int amount = Math.min(maxStackSize - item.getCount(), slot.getCount());
                        amount = Math.min(amount, this.getMaxStackSize());
                        if (amount > 0) {
                            //在需要clone时再clone
                            item = item.clone();
                            slot.setCount(slot.getCount() - amount);
                            item.setCount(item.getCount() + amount);
                            this.setItem(i, item, handler);
                            if (slot.getCount() <= 0) {
                                iterator.remove();
                            }
                        }
                    }
                }
            }
            if (itemSlots.isEmpty()) {
                break;
            }
        }

        if (!itemSlots.isEmpty() && !emptySlots.isEmpty()) {
            for (int slotIndex : emptySlots) {
                if (!itemSlots.isEmpty()) {
                    Item slot = itemSlots.get(0);
                    int maxStackSize = Math.min(slot.getMaxStackSize(), this.getMaxStackSize());
                    int amount = Math.min(maxStackSize, slot.getCount());
                    amount = Math.min(amount, this.getMaxStackSize());
                    slot.setCount(slot.getCount() - amount);
                    Item item = slot.clone();
                    item.setCount(amount);
                    this.setItem(slotIndex, item, handler);
                    if (slot.getCount() <= 0) {
                        itemSlots.remove(slot);
                    }
                }
            }
        }

        return itemSlots.toArray(Item.EMPTY_ARRAY);
    }

    public void setItem(int index, Item item, ItemHandler handler) {
        if (super.setItem(index, item)) {
            this.handlers.put(index, handler);
        }
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDefaultItemHandler(ItemHandler defaultItemHandler) {
        this.defaultItemHandler = defaultItemHandler;
    }

    @ApiStatus.Internal
    public void handle(ItemStackRequestActionEvent event) {
        if (event.getPlayer().equals(getHolder())) {
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
                    handler.handle(sourceSlot, sourItem, event);
                } else if (destinationI.equals(this)) {
                    ItemHandler handler = this.handlers.getOrDefault(destinationSlot, this.defaultItemHandler);
                    handler.handle(destinationSlot, destItem, event);
                }
            }
        }
    }
}
