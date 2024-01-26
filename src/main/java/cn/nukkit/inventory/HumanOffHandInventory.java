package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.entity.IHuman;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.InventoryContentPacket;
import cn.nukkit.network.protocol.InventorySlotPacket;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.types.ContainerIds;

public class HumanOffHandInventory extends BaseInventory {
    public HumanOffHandInventory(IHuman holder) {
        super(holder, InventoryType.INVENTORY, 1);
    }

    public void setItem(Item item) {
        setItem(0, item);
    }

    public void setItem(Item item, boolean send) {
        setItem(0, item, send);
    }


    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        IHuman holder = this.getHolder();
        if (holder instanceof Player player) {
            if (!player.spawned) return;
            if (send) {
                this.sendContents(this.getViewers());
                this.sendContents(holder.getEntity().getViewers().values());
                this.sendContents(player);
            }
        }
    }

    @Override
    public void sendContents(Player... players) {
        Item item = this.getItem(0);
        MobEquipmentPacket pk = this.createMobEquipmentPacket(item);

        for (Player player : players) {
            if (player == this.getHolder()) {
                InventoryContentPacket pk2 = new InventoryContentPacket();
                pk2.inventoryId = ContainerIds.OFFHAND;
                pk2.slots = new Item[]{item};
                player.dataPacket(pk2);
            } else {
                player.dataPacket(pk);
            }
        }
    }

    @Override
    public void sendSlot(int index, Player... players) {
        Item item = this.getItem(0);
        MobEquipmentPacket pk = this.createMobEquipmentPacket(item);

        for (Player player : players) {
            if (player == this.getHolder()) {
                InventorySlotPacket pk2 = new InventorySlotPacket();
                pk2.inventoryId = ContainerIds.OFFHAND;
                pk2.item = item;
                player.dataPacket(pk2);
            } else {
                player.dataPacket(pk);
            }
        }
    }

    private MobEquipmentPacket createMobEquipmentPacket(Item item) {
        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.eid = this.getHolder().getEntity().getId();
        pk.item = item;
        pk.inventorySlot = 1;
        pk.windowId = ContainerIds.OFFHAND;
        pk.tryEncode();
        return pk;
    }

    @Override
    public IHuman getHolder() {
        return (IHuman) super.getHolder();
    }
}
