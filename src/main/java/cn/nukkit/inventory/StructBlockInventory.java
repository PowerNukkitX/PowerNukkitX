package cn.nukkit.inventory;

import cn.nukkit.Player;


import cn.nukkit.blockentity.BlockEntityStructBlock;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StructBlockInventory implements Inventory {
    protected final BlockEntityStructBlock holder;
    protected final Set<Player> viewers;
    private List<InventoryListener> listeners;
    /**
     * @deprecated 
     */
    

    public StructBlockInventory(BlockEntityStructBlock holder) {
        this.holder = holder;
        this.viewers = new HashSet<>();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getSize() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setMaxStackSize(int size) {

    }

    @NotNull
    @Override
    public Item getItem(int index) {
        return Item.AIR;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean setItem(int index, Item item, boolean send) {
        return false;
    }

    @Override
    public Item[] addItem(Item... slots) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean canAddItem(Item item) {
        return false;
    }

    @Override
    public Item[] removeItem(Item... slots) {
        return Item.EMPTY_ARRAY;
    }

    @Override
    public Map<Integer, Item> getContents() {
        return Collections.emptyMap();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setContents(Map<Integer, Item> items) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendContents(Player player) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendContents(Player... players) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendContents(Collection<Player> players) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendSlot(int index, Player player) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendSlot(int index, Player... players) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendSlot(int index, Collection<Player> players) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getFreeSpace(Item item) {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean contains(Item item) {
        return false;
    }

    @Override
    public Map<Integer, Item> all(Item item) {
        return Collections.emptyMap();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int first(Item item, boolean exact) {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int firstEmpty(Item item) {
        return 0;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void decreaseCount(int slot) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public void remove(Item item) {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean clear(int index, boolean send) {
        return false;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void clearAll() {

    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isFull() {
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Set<Player> getViewers() {
        return Collections.emptySet();
    }

    @Override
    public InventoryType getType() {
        return InventoryType.STRUCTURE_EDITOR;
    }

    @Override
    public BlockEntityStructBlock getHolder() {
        return this.holder;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onOpen(Player who) {
        if (who.isOp() && who.isCreative()) {
            this.viewers.add(who);
            ContainerOpenPacket $1 = new ContainerOpenPacket();
            pk.windowId = who.getWindowId(this);
            pk.type = getType().getNetworkType();
            InventoryHolder $2 = this.getHolder();
            if (holder != null) {
                pk.x = holder.getVector3().getFloorX();
                pk.y = holder.getVector3().getFloorY();
                pk.z = holder.getVector3().getFloorZ();
            } else {
                pk.x = pk.y = pk.z = 0;
            }
            who.dataPacket(pk);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean open(Player who) {
        if(who.getWindowId(this)!=-1){//todo hack, ContainerClosePacket no longer triggers for command block and struct block, finding the correct way to close them
            who.removeWindow(this);
        }

        InventoryOpenEvent $3 = new InventoryOpenEvent(this, who);
        who.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            return false;
        }
        this.onOpen(who);
        return true;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void close(Player who) {
        InventoryCloseEvent $4 = new InventoryCloseEvent(this, who);
        who.getServer().getPluginManager().callEvent(ev);
        this.onClose(who);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onClose(Player who) {
        ContainerClosePacket $5 = new ContainerClosePacket();
        pk.windowId = who.getWindowId(this);
        pk.wasServerInitiated = who.getClosingWindowId() != pk.windowId;
        who.dataPacket(pk);
        this.viewers.remove(who);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onSlotChange(int index, Item before, boolean send) {
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void addListener(InventoryListener listener) {
        if (this.listeners == null) {
            this.listeners = new ArrayList<>();
        }

        this.listeners.add(listener);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void removeListener(InventoryListener listener) {
        if (this.listeners != null) {
            this.listeners.remove(listener);
        }
    }
}
