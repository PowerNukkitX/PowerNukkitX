package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;


public class ItemAxeNetherite extends ItemTool {


    public ItemAxeNetherite() {
        this(0, 1);
    }


    public ItemAxeNetherite(Integer meta) {
        this(meta, 1);
    }


    public ItemAxeNetherite(Integer meta, int count) {
        super(NETHERITE_AXE, meta, count, "Netherite Axe");
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_NETHERITE;
    }

    @Override
    public boolean isAxe() {
        return true;
    }

    @Override
    public int getTier() {
        return ItemTool.TIER_NETHERITE;
    }

    @Override
    public int getAttackDamage() {
        return 8;
    }


    @Override
    public boolean isLavaResistant() {
        return true;
    }


    @Override
    public boolean canBreakShield() {
        return true;
    }
}
