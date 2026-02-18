package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntityBeacon;
import cn.nukkit.blockentity.BlockEntityNameable;
import cn.nukkit.item.Item;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import com.google.common.collect.BiMap;

import java.util.Map;

/**
 * @author Rover656
 */
public class BeaconInventory extends BaseInventory implements BlockEntityInventoryNameable {

    public BeaconInventory(BlockEntityBeacon blockBeacon) {
        super(blockBeacon, InventoryType.BEACON, 1);
    }

    @Override
    public void init() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        map.put(0, ContainerSlotType.BEACON_PAYMENT);

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
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.setId((byte) who.getWindowId(this));
        pk.setType(containerTypeOf(this.getType()));
        InventoryHolder holder = this.getHolder();
        pk.setBlockPosition(Vector3i.from((int) holder.getX(), (int) holder.getY(), (int) holder.getZ()));
        who.dataPacket(pk);
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
