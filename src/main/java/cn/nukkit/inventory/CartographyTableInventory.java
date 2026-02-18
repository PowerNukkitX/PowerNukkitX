package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockCartographyTable;
import cn.nukkit.item.Item;
import org.cloudburstmc.math.vector.Vector3i;
import org.cloudburstmc.protocol.bedrock.packet.ContainerOpenPacket;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerSlotType;
import com.google.common.collect.BiMap;

import java.util.Map;

public class CartographyTableInventory extends BaseInventory {

    public CartographyTableInventory(BlockCartographyTable blockCartographyTable) {
        super(blockCartographyTable, InventoryType.CARTOGRAPHY, 2);
    }

    @Override
    public void init() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        for (int i = 0; i < getSize(); i++) {
            map.put(i, 12 + i);
        }

        Map<Integer, ContainerSlotType> map2 = super.slotTypeMap();
        map2.put(0, ContainerSlotType.CARTOGRAPHY_INPUT);
        map2.put(1, ContainerSlotType.CARTOGRAPHY_ADDITIONAL);
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
