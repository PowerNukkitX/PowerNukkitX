package org.powernukkitx.inventory;

import org.powernukkitx.Player;
import org.powernukkitx.blockentity.BlockEntityBeacon;
import org.powernukkitx.blockentity.BlockEntityNameable;
import org.powernukkitx.item.Item;
import com.google.common.collect.BiMap;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket;

import java.util.Map;

/**
 * @author Rover656
 */
public class BeaconInventory extends BaseInventory implements BlockEntityInventoryNameable {

    public BeaconInventory(BlockEntityBeacon blockBeacon) {
        super(blockBeacon, ContainerType.BEACON, 1);
    }

    @Override
    public void init() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        map.put(0, ContainerEnumName.BEACON_PAYMENT_CONTAINER);

        BiMap<Integer, Integer> networkSlotMap = super.networkSlotMap();
        networkSlotMap.put(0, 27);
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);

        Item[] drops = who.getInventory().addItem(this.getItem(0));
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().getVector3().add(0.5, 0.5, 0.5), drop);
            }
        }

        this.clear(0);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        final InventoryHolder holder = this.getHolder();
        final ContainerOpenPacket packet = new ContainerOpenPacket();
        packet.setContainerID((byte) who.getWindowId(this));
        packet.setContainerType(this.getType());
        packet.setPosition(Vector3i.from(holder.getX(), holder.getY(), holder.getZ()));
        who.sendPacket(packet);
        this.sendContents(who);
    }

    @Override
    public BlockEntityBeacon getHolder() {
        return (BlockEntityBeacon) super.getHolder();
    }

    @Override
    public BlockEntityNameable getBlockEntityInventoryHolder() {
        return getHolder();
    }
}
