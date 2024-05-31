package cn.nukkit.inventory;

import cn.nukkit.Player;


import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.MobEquipmentPacket;

import java.util.HashSet;
import java.util.Set;


public class EntityEquipmentInventory extends BaseInventory {
    private final Entity entity;
    public static final int $1 = 0;
    public static final int $2 = 1;

    /**
     * @param holder an Entity which implements {@link InventoryHolder}.
     * @throws ClassCastException if the entity does not implements {@link InventoryHolder}
     */
    /**
     * @deprecated 
     */
    
    public EntityEquipmentInventory(InventoryHolder holder) {
        super(holder, InventoryType.INVENTORY, 9 + 27);
        this.entity = (Entity) holder;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getSize() {
        return 2;
    }


    public Entity getEntity() {
        return entity;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendSlot(int index, Player... players) {
        for (Player player : players) {
            this.sendSlot(index, player);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendSlot(int index, Player player) {
        MobEquipmentPacket $3 = new MobEquipmentPacket();
        mobEquipmentPacket.eid = this.entity.getId();
        mobEquipmentPacket.inventorySlot = mobEquipmentPacket.hotbarSlot = index;//todo check inventorySlot and hotbarSlot for MobEquipmentPacket
        mobEquipmentPacket.item = this.getItem(index);
        player.dataPacket(mobEquipmentPacket);
    }

    @Override
    public Set<Player> getViewers() {
        Set<Player> viewers = new HashSet<>(this.viewers);
        viewers.addAll(entity.getViewers().values());
        return viewers;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean open(Player who) {
        return this.viewers.add(who);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onClose(Player who) {
        this.viewers.remove(who);
    }


    public Item getItemInHand() {
        return this.getItem(MAIN_HAND);
    }


    public Item getItemInOffhand() {
        return this.getItem(OFFHAND);
    }
    /**
     * @deprecated 
     */
    


    public boolean setItemInHand(Item item) {
        return this.setItem(MAIN_HAND, item);
    }
    /**
     * @deprecated 
     */
    


    public boolean setItemInHand(Item item, boolean send) {
        return this.setItem(MAIN_HAND, item, send);
    }
    /**
     * @deprecated 
     */
    


    public boolean setItemInOffhand(Item item, boolean send) {
        return this.setItem(OFFHAND, item, send);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendContents(Player target) {
        this.sendSlot(MAIN_HAND, target);
        this.sendSlot(OFFHAND, target);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendContents(Player... target) {
        for (Player player : target) {
            this.sendContents(player);
        }
    }
}
