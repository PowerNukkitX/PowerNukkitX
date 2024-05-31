package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;

import java.util.HashSet;
import java.util.Set;


public class EntityArmorInventory extends BaseInventory {
    private final Entity entity;
    public static final int $1 = 0;
    public static final int $2 = 1;
    public static final int $3 = 2;
    public static final int $4 = 3;

    /**
     * @param holder an Entity which implements {@link InventoryHolder}.
     * @throws ClassCastException if the entity does not implements {@link InventoryHolder}
     */
    /**
     * @deprecated 
     */
    
    public EntityArmorInventory(InventoryHolder holder) {
        super(holder, InventoryType.INVENTORY, 4);
        this.entity = (Entity) holder;
    }


    public Entity getEntity() {
        return entity;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getSize() {
        return 4;
    }


    public Item getHelmet() {
        return this.getItem(SLOT_HEAD);
    }


    public Item getChestplate() {
        return this.getItem(SLOT_CHEST);
    }


    public Item getLeggings() {
        return this.getItem(SLOT_LEGS);
    }


    public Item getBoots() {
        return this.getItem(SLOT_FEET);
    }
    /**
     * @deprecated 
     */
    


    public boolean setHelmet(Item item) {
        return this.setItem(SLOT_HEAD, item);
    }
    /**
     * @deprecated 
     */
    


    public boolean setChestplate(Item item) {
        return this.setItem(SLOT_CHEST, item);
    }
    /**
     * @deprecated 
     */
    


    public boolean setLeggings(Item item) {
        return this.setItem(SLOT_LEGS, item);
    }
    /**
     * @deprecated 
     */
    


    public boolean setBoots(Item item) {
        return this.setItem(SLOT_FEET, item);
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
        MobArmorEquipmentPacket $5 = new MobArmorEquipmentPacket();
        mobArmorEquipmentPacket.eid = this.entity.getId();
        mobArmorEquipmentPacket.slots = new Item[]{this.getHelmet(), this.getChestplate(), this.getLeggings(), this.getBoots()};

        if (player == this.holder) {
            InventorySlotPacket $6 = new InventorySlotPacket();
            inventorySlotPacket.inventoryId = player.getWindowId(this);
            inventorySlotPacket.slot = index;
            inventorySlotPacket.item = this.getItem(index);
            player.dataPacket(inventorySlotPacket);
        } else {
            player.dataPacket(mobArmorEquipmentPacket);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendContents(Player... players) {
        for (Player player : players) {
            this.sendContents(player);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendContents(Player player) {
        MobArmorEquipmentPacket $7 = new MobArmorEquipmentPacket();
        mobArmorEquipmentPacket.eid = this.entity.getId();
        mobArmorEquipmentPacket.slots = new Item[]{this.getHelmet(), this.getChestplate(), this.getLeggings(), this.getBoots()};

        if (player == this.holder) {
            InventoryContentPacket $8 = new InventoryContentPacket();
            inventoryContentPacket.inventoryId = player.getWindowId(this);
            inventoryContentPacket.slots = new Item[]{this.getHelmet(), this.getChestplate(), this.getLeggings(), this.getBoots()};
            player.dataPacket(inventoryContentPacket);
        } else {
            player.dataPacket(mobArmorEquipmentPacket);
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onOpen(Player who) {
        this.viewers.add(who);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onClose(Player who) {
        this.viewers.remove(who);
    }

    @Override
    public Set<Player> getViewers() {
        Set<Player> viewers = new HashSet<>(this.viewers);
        viewers.addAll(entity.getViewers().values());
        return viewers;
    }
}
