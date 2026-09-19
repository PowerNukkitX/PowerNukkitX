package org.powernukkitx.inventory;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockTrappedChest;
import org.powernukkitx.blockentity.BlockEntityChest;
import org.powernukkitx.blockentity.BlockEntityNameable;
import org.powernukkitx.event.redstone.RedstoneUpdateEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.utils.LevelException;
import org.powernukkitx.utils.RedstoneComponent;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ChestInventory extends ContainerInventory implements BlockEntityInventoryNameable {
    @Nullable
    protected DoubleChestInventory doubleInventory;

    public ChestInventory(BlockEntityChest chest) {
        super(chest, ContainerType.CONTAINER, 27);
    }

    @Override
    public void init() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, ContainerEnumName.LEVEL_ENTITY_CONTAINER);
        }
    }

    @Override
    public Map<Integer, ContainerEnumName> slotTypeMap() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        for (int i = 0; i < this.getSize(); i++) {
            map.put(i, ContainerEnumName.INVENTORY_CONTAINER);
        }
        return map;
    }


    @Override
    public BlockEntityChest getHolder() {
        return (BlockEntityChest) this.holder;
    }

    private void unpackLootTable() {
        this.getHolder().unpackLootTable();
    }

    @NotNull
    @Override
    public Item getItem(int index) {
        this.unpackLootTable();
        return super.getItem(index);
    }

    @Override
    public Item getUnclonedItem(int index) {
        this.unpackLootTable();
        return super.getUnclonedItem(index);
    }

    @Override
    public Map<Integer, Item> getContents() {
        this.unpackLootTable();
        return super.getContents();
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        this.unpackLootTable();
        return super.setItem(index, item, send);
    }

    @Override
    public boolean clear(int index, boolean send) {
        this.unpackLootTable();
        return super.clear(index, send);
    }

    @Override
    public void decreaseCount(int slot, int amount) {
        this.unpackLootTable();
        super.decreaseCount(slot, amount);
    }

    @Override
    public boolean isFull() {
        this.unpackLootTable();
        return super.isFull();
    }

    @Override
    public boolean isEmpty() {
        this.unpackLootTable();
        return super.isEmpty();
    }

    @Override
    public int getFreeSpace(Item item) {
        this.unpackLootTable();
        return super.getFreeSpace(item);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);

        if (who.getDataFlag(ActorFlags.SILENT)) {
            return;
        }

        if (this.getVisibleViewersCount() == 1) {
            this.getHolder().broadcastLidState(true);
        }
        try {
            if (this.getHolder().getBlock() instanceof BlockTrappedChest trappedChest) {
                RedstoneUpdateEvent event = new RedstoneUpdateEvent(trappedChest);
                this.getHolder().level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    RedstoneComponent.updateAllAroundRedstone(this.getHolder());
                }
            }
        } catch (LevelException ignored) {
        }
    }

    @Override
    public void onClose(Player who) {
        if (this.getVisibleViewersCount() == 1) {
            this.getHolder().broadcastLidState(false);
        }

        try {
            if (this.getHolder().getBlock() instanceof BlockTrappedChest trappedChest) {
                RedstoneUpdateEvent event = new RedstoneUpdateEvent(trappedChest);
                this.getHolder().level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    RedstoneComponent.updateAllAroundRedstone(this.getHolder());
                }
            }
        } catch (LevelException ignored) {
        }
        super.onClose(who);
    }

    public void setDoubleInventory(@Nullable DoubleChestInventory doubleInventory) {
        this.doubleInventory = doubleInventory;
    }

    @Nullable
    public DoubleChestInventory getDoubleInventory() {
        return doubleInventory;
    }

    @Override
    public void sendSlot(int index, Player... players) {
        if (this.doubleInventory != null) {
            this.doubleInventory.sendSlot(this, index, players);
        } else {
            super.sendSlot(index, players);
        }
    }

    @Override
    public boolean canCauseVibration() {
        return true;
    }

    @Override
    public BlockEntityNameable getBlockEntityInventoryHolder() {
        return getHolder();
    }
}
