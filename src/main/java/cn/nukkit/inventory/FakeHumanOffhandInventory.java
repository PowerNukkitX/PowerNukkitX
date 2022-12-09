package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligentHuman;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.MobEquipmentPacket;
import cn.nukkit.network.protocol.types.ContainerIds;

/**
 * 这个Inventory是一个hack实现，用来实现{@link EntityIntelligentHuman}的背包实现，它无法被open 和 close，因为虚拟人类不会自己打开物品栏<p>
 * 它的{@link FakeHumanInventory#viewers}永远为空,因为不允许打开它
 */
@PowerNukkitXOnly
@Since("1.19.50-r3")
public class FakeHumanOffhandInventory extends BaseInventory {
    public FakeHumanOffhandInventory(EntityIntelligentHuman holder) {
        super(holder, InventoryType.OFFHAND);
    }

    @Override
    public void setSize(int size) {
        throw new UnsupportedOperationException("Offhand can only carry one item at a time");
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        EntityIntelligentHuman holder = this.getHolder();
        this.sendContents(holder.getViewers().values());
    }

    @Override
    public void sendContents(Player... players) {
        Item item = this.getItem(0);
        MobEquipmentPacket pk = this.createMobEquipmentPacket(item);

        for (Player player : players) {
            player.dataPacket(pk);
        }
    }

    @Override
    public void sendSlot(int index, Player... players) {
        Item item = this.getItem(0);
        MobEquipmentPacket pk = this.createMobEquipmentPacket(item);

        for (Player player : players) {
            player.dataPacket(pk);
        }
    }

    private MobEquipmentPacket createMobEquipmentPacket(Item item) {
        MobEquipmentPacket pk = new MobEquipmentPacket();
        pk.eid = this.getHolder().getId();
        pk.item = item;
        pk.inventorySlot = 1;
        pk.windowId = ContainerIds.OFFHAND;
        pk.tryEncode();
        return pk;
    }

    @Override
    public EntityIntelligentHuman getHolder() {
        return (EntityIntelligentHuman) super.getHolder();
    }

    //non
    @Override
    public void close(Player who) {
    }

    @Override
    public void onOpen(Player who) {
    }

    @Override
    public void onClose(Player who) {
    }

    @Override
    public boolean open(Player who) {
        return false;
    }
}
