package cn.nukkit.item;

import cn.nukkit.api.Since;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemAxeGold extends ItemTool {

    public ItemAxeGold() {
        this(0, 1);
    }

    public ItemAxeGold(Integer meta) {
        this(meta, 1);
    }

    public ItemAxeGold(Integer meta, int count) {
        super(GOLD_AXE, meta, count, "Golden Axe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_GOLD;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_GOLD;
    }

    @Override
    public int getAttackDamage() {
        return 3;
    }

    @Since("1.19.21-r4")
    @Override
    public boolean canBreakShield() {
        return true;
    }
}
