package cn.nukkit.item;

/**
 * @author lion
 * @since 21.03.17
 */
public class ItemCarrotOnAStick extends ItemTool {

    public ItemCarrotOnAStick() {
        this(0, 1);
    }

    public ItemCarrotOnAStick(Integer meta) {
        this(meta, 1);
    }

    public ItemCarrotOnAStick(Integer meta, int count) {
        super(CARROT_ON_A_STICK, meta, count, "Carrot on a Stick");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_CARROT_ON_A_STICK;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean noDamageOnAttack() {
        return true;
    }

    @Override
    public boolean noDamageOnBreak() {
        return true;
    }
}

