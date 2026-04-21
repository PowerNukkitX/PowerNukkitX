package cn.nukkit.inventory.fake;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.event.inventory.ItemStackRequestActionEvent;
import cn.nukkit.inventory.BaseInventory;
import cn.nukkit.inventory.InputInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.request.NetworkMapping;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.InternalPlugin;
import cn.nukkit.recipe.Input;
import com.google.common.collect.BiMap;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.ItemStackRequestSlotInfo;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.DropAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.ItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.SwapAction;
import org.cloudburstmc.protocol.bedrock.data.inventory.itemstack.request.action.TransferItemStackRequestAction;
import org.cloudburstmc.protocol.bedrock.packet.ContainerClosePacket;
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class FakeInventory extends BaseInventory implements InputInventory {
    private final Map<Integer, ItemHandler> handlers = new HashMap<>();
    private final FakeBlock fakeBlock;
    private final FakeInventoryType fakeInventoryType;
    private String title;
    private ItemHandler defaultItemHandler;
    private Consumer<Player> onCloseHandler;

    public FakeInventory(FakeInventoryType fakeInventoryType) {
        this(fakeInventoryType, fakeInventoryType.name(), fakeInventoryType.size);
    }

    public FakeInventory(FakeInventoryType fakeInventoryType, @NotNull String title) {
        this(fakeInventoryType, title, fakeInventoryType.size);
    }

    public FakeInventory(FakeInventoryType fakeInventoryType, @NotNull String title, int size) {
        super(null, fakeInventoryType.inventoryType,
                fakeInventoryType == FakeInventoryType.ENTITY ? size : fakeInventoryType.size);

        this.title = title;
        this.fakeInventoryType = fakeInventoryType;
        this.fakeBlock = fakeInventoryType.builder != null ? fakeInventoryType.builder.create(this) : fakeInventoryType.fakeBlock;
        this.defaultItemHandler = (a, b, c, d, e) -> {
        };

        switch (fakeInventoryType) {
            case ENTITY -> {
                Map<Integer, ContainerEnumName> map = super.slotTypeMap();
                for (int i = 0; i < getSize(); i++) {
                    map.put(i, ContainerEnumName.LEVEL_ENTITY_CONTAINER);
                }
            }
            case CHEST, DOUBLE_CHEST, HOPPER, DISPENSER, DROPPER, ENDER_CHEST -> {
                Map<Integer, ContainerEnumName> map = super.slotTypeMap();
                for (int i = 0; i < getSize(); i++) {
                    map.put(i, ContainerEnumName.LEVEL_ENTITY_CONTAINER);
                }
            }
            case FURNACE -> {
                Map<Integer, ContainerEnumName> map = super.slotTypeMap();
                map.put(0, ContainerEnumName.FURNACE_INGREDIENT_CONTAINER);
                map.put(1, ContainerEnumName.FURNACE_FUEL_CONTAINER);
                map.put(2, ContainerEnumName.FURNACE_RESULT_CONTAINER);
            }
            case BREWING_STAND -> {
                Map<Integer, ContainerEnumName> map = super.slotTypeMap();
                map.put(0, ContainerEnumName.BREWING_STAND_INPUT_CONTAINER);
                map.put(1, ContainerEnumName.BREWING_STAND_RESULT_CONTAINER);
                map.put(2, ContainerEnumName.BREWING_STAND_RESULT_CONTAINER);
                map.put(3, ContainerEnumName.BREWING_STAND_RESULT_CONTAINER);
                map.put(4, ContainerEnumName.BREWING_STAND_FUEL_CONTAINER);
            }
            case SHULKER_BOX -> {
                Map<Integer, ContainerEnumName> map = super.slotTypeMap();
                for (int i = 0; i < getSize(); i++) {
                    map.put(i, ContainerEnumName.SHULKER_BOX_CONTAINER);
                }
            }
            case WORKBENCH -> {
                BiMap<Integer, Integer> map = super.networkSlotMap();
                for (int i = 0; i < getSize(); i++) {
                    map.put(i, 32 + i);
                }
                Map<Integer, ContainerEnumName> map2 = super.slotTypeMap();
                for (int i = 0; i < getSize(); i++) {
                    map2.put(i, ContainerEnumName.CRAFTING_INPUT_CONTAINER);
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

        if (this.fakeBlock != null) {
            this.fakeBlock.create(player, this.getTitle());
        }

        player.getLevel().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
            if (this.fakeBlock == null) {
                player.setFakeInventoryOpen(false);
                return;
            }

            Optional<Vector3> first = this.fakeBlock.getLastPositions(player).stream().filter(v -> !(v instanceof BlockEntity)).findAny();
            if (first.isPresent()) {
                Vector3 position = first.get();
                long entityId = this.fakeBlock.getEntityId(player);
                sendOpenContainerPacket(player, position.getFloorX(), position.getFloorY(), position.getFloorZ(), entityId);

                super.onOpen(player);
                this.sendContents(player);
            } else {
                this.fakeBlock.remove(player);
                player.setFakeInventoryOpen(false);
            }
        }, 5);
    }

    private void sendOpenContainerPacket(Player player, int x, int y, int z, long entityId) {
        final ContainerOpenPacket packet = new ContainerOpenPacket();
        packet.setContainerID((byte) player.getWindowId(this));
        packet.setContainerType(this.getType());
        packet.setPosition(Vector3i.from(x, y, z));
        if (entityId != 0) {
            packet.setTargetActorID(entityId);
        }
        player.sendPacket(packet);
    }

    @Override
    public void onClose(Player player) {
        final ContainerClosePacket packet = new ContainerClosePacket();
        packet.setContainerID((byte) player.getWindowId(this));
        packet.setServerInitiatedClose(player.getClosingWindowId() != packet.getContainerID());
        packet.setContainerType(this.getType());
        player.sendPacket(packet);

        if (this.fakeBlock != null) {
            if (this.fakeInventoryType == FakeInventoryType.ENTITY) {
                this.fakeBlock.remove(player);
            } else {
                player.getLevel().getScheduler().scheduleDelayedTask(InternalPlugin.INSTANCE, () -> {
                    this.fakeBlock.remove(player);
                }, 5);
            }
        }

        super.onClose(player);
        player.setFakeInventoryOpen(false);

        if (this.onCloseHandler != null) {
            this.onCloseHandler.accept(player);
        }
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

    public void setOnCloseHandler(Consumer<Player> onCloseHandler) {
        this.onCloseHandler = onCloseHandler;
    }

    @ApiStatus.Internal
    public void handle(ItemStackRequestActionEvent event) {
        ItemStackRequestAction action = event.getAction();
        ItemStackRequestSlotInfo source = null;
        ItemStackRequestSlotInfo destination = null;
        if (action instanceof TransferItemStackRequestAction transferItemStackRequestAction) {
            source = transferItemStackRequestAction.getSource();
            destination = transferItemStackRequestAction.getDestination();
        } else if (action instanceof SwapAction swapAction) {
            source = swapAction.getSource();
            destination = swapAction.getDestination();
        } else if (action instanceof DropAction dropAction) {
            source = dropAction.getSource();
        }
        if (source != null) {
            ContainerEnumName sourceSlotType = source.getFullContainerName().getContainerName();
            Integer dynamicSrc = source.getFullContainerName().getDynamicID();
            Inventory sourceI = NetworkMapping.getInventory(event.getPlayer(), sourceSlotType, dynamicSrc);
            int sourceSlot = sourceI.fromNetworkSlot(source.getSlot());
            Item sourItem = sourceI.getItem(sourceSlot);
            if (sourceI.equals(this)) {
                ItemHandler handler = this.handlers.getOrDefault(sourceSlot, this.defaultItemHandler);
                handler.handle(this, sourceSlot, sourItem, Item.AIR, event);
            } else if (destination != null) {
                ContainerEnumName destinationSlotType = destination.getFullContainerName().getContainerName();
                Integer dynamicDst = source.getFullContainerName().getDynamicID();
                Inventory destinationI = NetworkMapping.getInventory(event.getPlayer(), destinationSlotType, dynamicDst);
                int destinationSlot = destinationI.fromNetworkSlot(destination.getSlot());
                Item destItem = destinationI.getItem(destinationSlot);
                if (destinationI.equals(this)) {
                    ItemHandler handler = this.handlers.getOrDefault(destinationSlot, this.defaultItemHandler);
                    handler.handle(this, destinationSlot, destItem, sourItem, event);
                }
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
