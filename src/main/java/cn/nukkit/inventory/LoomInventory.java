package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockLoom;
import cn.nukkit.item.Item;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import com.google.common.collect.BiMap;

import java.util.Map;

public class LoomInventory extends BaseInventory {
    public LoomInventory(BlockLoom blockLoom) {
        super(blockLoom, InventoryType.LOOM, 3);
    }

    @Override
    public void init() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, 9 + i);
        }

        Map<Integer, ContainerSlotType> map2 = super.slotTypeMap();
        map2.put(0, ContainerSlotType.LOOM_INPUT);
        map2.put(1, ContainerSlotType.LOOM_DYE);
        map2.put(2, ContainerSlotType.LOOM_MATERIAL);
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

    public Item getBanner() {
        return getItem(0);
    }

    public Item getDye() {
        return getItem(1);
    }

    public Item getPattern() {
        return getItem(2);
    }
}
