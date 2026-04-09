package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockCartographyTable;
import cn.nukkit.item.Item;
import com.google.common.collect.BiMap;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket;

import java.util.Map;

public class CartographyTableInventory extends BaseInventory {

    public CartographyTableInventory(BlockCartographyTable blockCartographyTable) {
        super(blockCartographyTable, ContainerType.CARTOGRAPHY, 2);
    }

    @Override
    public void init() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, 12 + i);
        }

        Map<Integer, ContainerEnumName> map2 = super.slotTypeMap();
        map2.put(0, ContainerEnumName.CARTOGRAPHY_INPUT_CONTAINER);
        map2.put(1, ContainerEnumName.CARTOGRAPHY_ADDITIONAL_CONTAINER);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        final InventoryHolder holder = this.getHolder();
        final ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.setContainerID((byte) who.getWindowId(this));
        pk.setContainerType(this.getType());
        pk.setPosition(Vector3i.from(holder.getX(), holder.getY(), holder.getZ()));
        who.dataPacket(pk);
        this.sendContents(who);
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);

        Item[] drops = new Item[]{getInput(), getAdditional()};
        drops = who.getInventory().addItem(drops);
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().getVector3().add(0.5, 0.5, 0.5), drop);
            }
        }

        clear(0);
        clear(1);
    }

    public Item getInput() {
        return this.getItem(0);
    }

    public Item getAdditional() {
        return this.getItem(1);
    }

}
