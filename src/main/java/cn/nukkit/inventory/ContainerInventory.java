package cn.nukkit.inventory;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerOpenPacket;

public abstract class ContainerInventory extends BaseInventory {
    public ContainerInventory(InventoryHolder holder, InventoryType type, int size) {
        super(holder, type, size);
    }

    @Override
    public InventoryHolder getHolder() {
        return super.getHolder();
    }

    @Override
    public void onOpen(Player who) {
        if (!who.getAdventureSettings().get(AdventureSettings.Type.OPEN_CONTAINERS)) return;
        super.onOpen(who);
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.type = this.getType().getNetworkType();
        InventoryHolder holder = this.getHolder();
        pk.x = (int) holder.getX();
        pk.y = (int) holder.getY();
        pk.z = (int) holder.getZ();
        who.dataPacket(pk);

        this.sendContents(who);

        if (canCauseVibration() && holder instanceof Vector3 vector3) {
            who.level.getVibrationManager().callVibrationEvent(new VibrationEvent(who, vector3.add(0.5, 0.5, 0.5), VibrationType.CONTAINER_OPEN));
        }
    }

    @Override
    public void onClose(Player who) {
        if (canCauseVibration() && getHolder() instanceof Vector3 vector3) {
            who.level.getVibrationManager().callVibrationEvent(new VibrationEvent(who, vector3.add(0.5, 0.5, 0.5), VibrationType.CONTAINER_CLOSE));
        }
        super.onClose(who);
    }

    /**
     * 若返回为true,则在inventory打开和关闭时会发生振动事件 (InventoryHolder为Vector3子类的前提下)
     *
     * @return boolean
     */
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

                if (!item.isNull()) {
                    averageCount += (float) item.getCount() / (float) Math.min(inv.getMaxStackSize(), item.getMaxStackSize());
                    ++itemCount;
                }
            }

            averageCount = averageCount / (float) inv.getSize();
            return NukkitMath.floorFloat(averageCount * 14) + (itemCount > 0 ? 1 : 0);
        }
    }
}
