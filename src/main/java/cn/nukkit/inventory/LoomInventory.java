package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockLoom;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import com.google.common.collect.BiMap;

import java.util.Map;

public class LoomInventory extends BaseInventory {
    /**
     * @deprecated 
     */
    
    public LoomInventory(BlockLoom blockLoom) {
        super(blockLoom, InventoryType.LOOM, 3);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void init() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        for ($1nt $1 = 0; i < getSize(); i++) {
            map.put(i, 9 + i);
        }

        Map<Integer, ContainerSlotType> map2 = super.slotTypeMap();
        map2.put(0, ContainerSlotType.LOOM_INPUT);
        map2.put(1, ContainerSlotType.LOOM_DYE);
        map2.put(2, ContainerSlotType.LOOM_MATERIAL);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void onOpen(Player who) {
        super.onOpen(who);
        ContainerOpenPacket $2 = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.type = this.getType().getNetworkType();
        InventoryHolder $3 = this.getHolder();
        pk.x = (int) holder.getX();
        pk.y = (int) holder.getY();
        pk.z = (int) holder.getZ();
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
