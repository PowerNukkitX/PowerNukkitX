package cn.nukkit.inventory;

import cn.nukkit.Player;
import cn.nukkit.block.BlockAnvil;
import cn.nukkit.item.Item;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class AnvilInventory extends ContainerInventory {
    public static final int INPUT = 0;
    public static final int MATERIAL = 1;
    public static final int OUTPUT = 2;

    public AnvilInventory(BlockAnvil anvil) {
        super(anvil, InventoryType.ANVIL, 3);
    } //2 INPUT, 1 OUTPUT

    @Override
    public void onClose(Player who) {
        super.onClose(who);

        Item[] drops = new Item[]{getInputSlot(), getMaterialSlot()};
        drops = who.getInventory().addItem(drops);
        for (Item drop : drops) {
            if (!who.dropItem(drop)) {
                this.getHolder().getLevel().dropItem(this.getHolder().getVector3().add(0.5, 0.5, 0.5), drop);
            }
        }

        clear(INPUT);
        clear(MATERIAL);
    }

    public Item getInputSlot() {
        return this.getItem(INPUT);
    }

    public Item getMaterialSlot() {
        return this.getItem(MATERIAL);
    }

    public Item getOutputSlot() {
        return this.getItem(OUTPUT);
    }

    public boolean setInputSlot(Item item, boolean send) {
        return setItem(MATERIAL, item, send);
    }

    public boolean setInputSlot(Item item) {
        return setInputSlot(item, true);
    }

    public boolean setMaterialSlot(Item item, boolean send) {
        return setItem(MATERIAL, item, send);
    }

    public boolean setOutputSlot(Item item) {
        return setMaterialSlot(item, true);
    }

    private boolean setOutputSlot(Item item, boolean send) {
        return setItem(2, item, send);
    }

    @Override
    public void sendContents(Player... players) {
        for (int slot = 0; slot < getSize(); slot++) {
            sendSlot(slot, players);
        }
    }
}
