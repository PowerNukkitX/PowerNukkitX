package org.powernukkitx.item;

public class ItemBrush extends ItemTool {
    public ItemBrush() {
        super(BRUSH);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getMaxDurability() {
        return 65;
    }
}