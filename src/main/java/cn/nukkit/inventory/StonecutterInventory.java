package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.BlockStonecutterBlock;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;
import cn.nukkit.recipe.Input;

import java.util.Map;


public class StonecutterInventory extends ContainerInventory implements CraftTypeInventory, InputInventory {


    public StonecutterInventory(BlockStonecutterBlock blockStonecutterBlock) {
        super(blockStonecutterBlock, InventoryType.STONECUTTER, 3);
    }

    @Override
    public BlockStonecutterBlock getHolder() {
        return (BlockStonecutterBlock) super.getHolder();
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
    public boolean setItem(int index, Item item, boolean send) {
        if(index == 3) {
            slots.put(index, item);
            return true;
        } else return false;
    }

    @Override
    public Input getInput() {
        return new Input(1, 1, new Item[][]{new Item[]{getItem(3)}});
    }

    @Override
    public Map<Integer, ContainerSlotType> slotTypeMap() {
        Map<Integer, ContainerSlotType> map = super.slotTypeMap();
        map.put(3, ContainerSlotType.STONECUTTER_INPUT);
        map.put(4, ContainerSlotType.STONECUTTER_RESULT);
        return map;
    }
}
