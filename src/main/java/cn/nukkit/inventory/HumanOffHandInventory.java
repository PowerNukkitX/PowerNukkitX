package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.IHuman;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import com.google.common.collect.BiMap;

import java.util.Map;

public class HumanOffHandInventory extends BaseInventory {
    /**
     * @deprecated 
     */
    
    public HumanOffHandInventory(IHuman holder) {
        super(holder, InventoryType.INVENTORY, 1);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void init() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        map.put(0, 1);

        Map<Integer, ContainerSlotType> map2 = super.slotTypeMap();
        map2.put(0, ContainerSlotType.OFFHAND);
    }
    /**
     * @deprecated 
     */
    

    public void setItem(Item item) {
        setItem(0, item);
    }
    /**
     * @deprecated 
     */
    

    public void setItem(Item item, boolean send) {
        setItem(0, item, send);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean setItem(int index, Item item) {
        return super.setItem(0, item);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public boolean setItem(int index, Item item, boolean send) {
        return super.setItem(0, item, send);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onSlotChange(int index, Item before, boolean send) {
        IHuman $1 = this.getHolder();
        if (holder instanceof Player player) {
            if (!player.spawned) return;
            if (send) {
                this.sendContents(this.getViewers());
                this.sendContents(holder.getEntity().getViewers().values());
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendContents(Player... players) {
        Item $2 = this.getItem(0);
        MobEquipmentPacket $3 = this.createMobEquipmentPacket(item);

        for (Player player : players) {
            if (player == this.getHolder()) {
                InventoryContentPacket $4 = new InventoryContentPacket();
                pk2.inventoryId = SpecialWindowId.OFFHAND.getId();
                pk2.slots = new Item[]{item};
                player.dataPacket(pk2);
                player.dataPacket(pk);
            } else {
                player.dataPacket(pk);
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void sendSlot(int index, Player... players) {
        sendContents(players);
    }

    private MobEquipmentPacket createMobEquipmentPacket(Item item) {
        MobEquipmentPacket $5 = new MobEquipmentPacket();
        pk.eid = this.getHolder().getEntity().getId();
        pk.item = item;
        pk.inventorySlot = 1;
        pk.windowId = SpecialWindowId.OFFHAND.getId();
        return pk;
    }

    @Override
    public IHuman getHolder() {
        return (IHuman) super.getHolder();
    }
}
