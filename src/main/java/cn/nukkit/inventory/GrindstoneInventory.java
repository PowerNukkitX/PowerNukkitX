package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockGrindstone;
import cn.nukkit.event.inventory.InventoryCloseEvent;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import com.google.common.collect.BiMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class GrindstoneInventory extends ContainerInventory implements CraftTypeInventory, SoleInventory {
    private static final int SLOT_FIRST_ITEM = 0;
    private static final int SLOT_SECOND_ITEM = 1;
    private static final int SLOT_RESULT = 2;

    public GrindstoneInventory(BlockGrindstone blockGrindstone) {
        super(blockGrindstone, InventoryType.GRINDSTONE, 3);
    }

    @Override
    public BiMap<Integer, Integer> networkSlotMap() {
        BiMap<Integer, Integer> map = super.networkSlotMap();
        map.put(0, 16);//INPUT
        map.put(1, 17);//ADDITIONAL
        map.put(2, 18);//RESULT
        return map;
    }

    @Override
    public Map<Integer, ContainerSlotType> slotTypeMap() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        map.put(0, ContainerSlotType.GRINDSTONE_INPUT);
        map.put(1, ContainerSlotType.GRINDSTONE_ADDITIONAL);
        map.put(2, ContainerSlotType.GRINDSTONE_RESULT);
        return map;
    }

    @Override
    public void close(Player who) {
        InventoryCloseEvent ev = new InventoryCloseEvent(this, who);
        who.getServer().getPluginManager().callEvent(ev);
        onClose(who);
    }

    @Override
    public void onClose(Player who) {
        super.onClose(who);

        Item[] drops = new Item[]{getFirstItem(), getSecondItem()};
        drops = who.getInventory().addItem(drops);
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().getVector3().add(0.5, 0.5, 0.5), drop);
            }
        }

        clear(SLOT_FIRST_ITEM);
        clear(SLOT_SECOND_ITEM);
    }

    public Item getFirstItem() {
        return getItem(SLOT_FIRST_ITEM);
    }


    public Item getSecondItem() {
        return getItem(SLOT_SECOND_ITEM);
    }


    public Item getResult() {
        return getItem(SLOT_RESULT);
    }


    public boolean setFirstItem(Item item, boolean send) {
        return setItem(SLOT_FIRST_ITEM, item, send);
    }


    public boolean setFirstItem(Item item) {
        return setFirstItem(item, true);
    }


    public boolean setSecondItem(Item item, boolean send) {
        return setItem(SLOT_SECOND_ITEM, item, send);
    }


    public boolean setSecondItem(Item item) {
        return setSecondItem(item, true);
    }


    public boolean setResult(Item item, boolean send) {
        return setItem(SLOT_RESULT, item, send);
    }


    public boolean setResult(Item item) {
        return setResult(item, true);
    }

    @NotNull
    @Override
    public Item getItem(int index) {
        if (index < 0 || index > 3) {
            return Item.AIR;
        }
        return super.getItem(index);
    }


    @Override
    public Item getUnclonedItem(int index) {
        if (index < 0 || index > 3) {
            return Item.AIR;
        }
        return super.getUnclonedItem(index);
    }

    @Override
    public boolean setItem(int index, Item item, boolean send) {
        if (index < 0 || index > 3) {
            return false;
        }
        return super.setItem(index, item, send);
    }
}
