package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockStonecutterBlock;
import cn.nukkit.item.Item;
import cn.nukkit.recipe.Input;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerEnumName;
import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

import java.util.Map;


public class StonecutterInventory extends ContainerInventory implements CraftTypeInventory, InputInventory {


    public StonecutterInventory(BlockStonecutterBlock blockStonecutterBlock) {
        super(blockStonecutterBlock, ContainerType.STONECUTTER, 3);
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
        if (index == 3) {
            slots.put(index, item);
            return true;
        } else return false;
    }

    @Override
    public Input getInput() {
        return new Input(1, 1, new Item[][]{new Item[]{getItem(3)}});
    }

    @Override
    public Map<Integer, ContainerEnumName> slotTypeMap() {
        Map<Integer, ContainerEnumName> map = super.slotTypeMap();
        map.put(3, ContainerEnumName.STONECUTTER_INPUT_CONTAINER);
        map.put(4, ContainerEnumName.STONECUTTER_RESULT_PREVIEW_CONTAINER);
        return map;
    }
}
