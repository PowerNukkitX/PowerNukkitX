package org.powernukkitx.inventory;

import org.powernukkitx.AdventureSettings;
import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;
import org.powernukkitx.item.Item;
import org.powernukkitx.level.vibration.VibrationEvent;
import org.powernukkitx.level.vibration.VibrationType;
import org.powernukkitx.math.NukkitMath;
import org.powernukkitx.math.Vector3;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket;

public abstract class ContainerInventory extends BaseInventory {
    public ContainerInventory(InventoryHolder holder, ContainerType type, int size) {
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
        final InventoryHolder holder = this.getHolder();
        final ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.setContainerID((byte) who.getWindowId(this));
        pk.setContainerType(this.getType());
        pk.setPosition(Vector3i.from(holder.getX(), holder.getY(), holder.getZ()));
        if (holder instanceof Entity entity) {
            pk.setTargetActorID(entity.getId());
        }
        who.sendPacket(pk);

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
     * If this returns true, a vibration event is emitted when the inventory is opened and closed (provided the InventoryHolder is a subclass of Vector3).
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
