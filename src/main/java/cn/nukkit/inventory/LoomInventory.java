package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockLoom;
import cn.nukkit.item.Item;
import com.google.common.collect.BiMap;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket;

import java.util.Map;

public class LoomInventory extends BaseInventory {
    public LoomInventory(BlockLoom blockLoom) {
        super(blockLoom, ContainerType.LOOM, 3);
    }

    @Override
    public void init() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, 9 + i);
        }

        Map<Integer, ContainerEnumName> map2 = super.slotTypeMap();
        map2.put(0, ContainerEnumName.LOOM_INPUT_CONTAINER);
        map2.put(1, ContainerEnumName.LOOM_DYE_CONTAINER);
        map2.put(2, ContainerEnumName.LOOM_MATERIAL_CONTAINER);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        final InventoryHolder holder = this.getHolder();
        final ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.setContainerID((byte) who.getWindowId(this));
        pk.setContainerType(this.getType());
        pk.setPosition(Vector3i.from(holder.getX(), holder.getY(), holder.getZ()));
        who.sendPacket(pk);
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
