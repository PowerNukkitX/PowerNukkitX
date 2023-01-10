package cn.nukkit.inventory;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerClosePacket;
import cn.nukkit.network.protocol.ContainerOpenPacket;

import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class ContainerInventory extends BaseInventory {
    public ContainerInventory(InventoryHolder holder, InventoryType type) {
        super(holder, type);
    }

    public ContainerInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items) {
        super(holder, type, items);
    }

    public ContainerInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items, Integer overrideSize) {
        super(holder, type, items, overrideSize);
    }

    public ContainerInventory(InventoryHolder holder, InventoryType type, Map<Integer, Item> items, Integer overrideSize, String overrideTitle) {
        super(holder, type, items, overrideSize, overrideTitle);
    }

    @Override
    public void onOpen(Player who) {
        if (!who.getAdventureSettings().get(AdventureSettings.Type.OPEN_CONTAINERS))
            return;
        super.onOpen(who);
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.type = this.getType().getNetworkType();
        InventoryHolder holder = this.getHolder();
        if (holder instanceof Vector3) {
            pk.x = (int) ((Vector3) holder).getX();
            pk.y = (int) ((Vector3) holder).getY();
            pk.z = (int) ((Vector3) holder).getZ();
        } else {
            pk.x = pk.y = pk.z = 0;
        }
        if (holder instanceof Entity) {
            pk.entityId = ((Entity) holder).getId();
        }

        who.dataPacket(pk);

        this.sendContents(who);

        if (canCauseVibration() && holder instanceof Vector3 vector3) {
            who.level.getVibrationManager().callVibrationEvent(new VibrationEvent(who, vector3.add(0.5, 0.5, 0.5), VibrationType.CONTAINER_OPEN));
        }
    }

    @Override
    public void onClose(Player who) {
        ContainerClosePacket pk = new ContainerClosePacket();
        pk.windowId = who.getWindowId(this);
        pk.wasServerInitiated = who.getClosingWindowId() != pk.windowId;
        who.dataPacket(pk);

        if (canCauseVibration() && getHolder() instanceof Vector3 vector3) {
            who.level.getVibrationManager().callVibrationEvent(new VibrationEvent(who, vector3.add(0.5, 0.5, 0.5), VibrationType.CONTAINER_CLOSE));
        }

        super.onClose(who);
    }

    /**
     * 若返回为true,则在inventory打开和关闭时会发生振动事件 (InventoryHolder为Vector3子类的前提下)
     * @return boolean
     */
    @PowerNukkitXOnly
    @Since("1.19.21-r3")
    public boolean canCauseVibration() {
        return false;
    }

    public static int calculateRedstone(Inventory inv) {
        if (inv == null) {
            return 0;
        } else {
            int itemCount = 0;
            float averageCount = 0;

            for (int slot = 0; slot < inv.getSize(); ++slot) {
                Item item = inv.getItem(slot);

                if (item.getId() != 0) {
                    averageCount += (float) item.getCount() / (float) Math.min(inv.getMaxStackSize(), item.getMaxStackSize());
                    ++itemCount;
                }
            }

            averageCount = averageCount / (float) inv.getSize();
            return NukkitMath.floorFloat(averageCount * 14) + (itemCount > 0 ? 1 : 0);
        }
    }
}
