package cn.nukkit.item;

public class ItemChicken extends ItemEdible {
    public ItemChicken() {
        super(CHICKEN, 0, 1, "Raw Chicken");
    }

    public ItemChicken(int count) {
        super(CHICKEN, 0, count, "Raw Chicken");
    }
}