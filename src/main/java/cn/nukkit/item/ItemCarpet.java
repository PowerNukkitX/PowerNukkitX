package cn.nukkit.item;

public class ItemCarpet extends Item {
    public ItemCarpet() {
        this(0);
    }

    public ItemCarpet(Integer meta) {
        this(meta, 1);
    }

    public ItemCarpet(Integer meta, int count) {
        super(CARPET, meta, count);
        adjustName();
    }

    private void adjustName() {
        switch (getDamage()) {
            case 0:
                name = "White Carpet";
                return;
            case 1:
                name = "Orange Carpet";
                return;
            case 2:
                name = "Magenta Carpet";
                return;
            case 3:
                name = "Light Blue Carpet";
                return;
            case 4:
                name = "Yellow Carpet";
                return;
            case 5:
                name = "Lime Carpet";
                return;
            case 6:
                name = "Pink Carpet";
                return;
            case 7:
                name = "Gray Carpet";
                return;
            case 8:
                name = "Light Gray Carpet";
                return;
            case 9:
                name = "Cyan Carpet";
                return;
            case 10:
                name = "Purple Carpet";
                return;
            case 11:
                name = "Blue Carpet";
                return;
            case 12:
                name = "Brown Carpet";
                return;
            case 13:
                name = "Green Carpet";
                return;
            case 14:
                name = "Red Carpet";
                return;
            case 15:
                name = "Black Carpet";
                return;
            default:
                name = "Carpet";
        }
    }
}