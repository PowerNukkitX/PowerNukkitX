package cn.nukkit.item;

public class ItemEmptyMap extends Item {

    public ItemEmptyMap() {
        this(0, 1);
    }
    
    public ItemEmptyMap(Integer meta) {
        this(meta, 1);
    }
    
    public ItemEmptyMap(Integer meta, int count) {
        super(EMPTY_MAP, meta, count, "Empty Map");
        updateName();
    }

    @Override
    public void setDamage(Integer meta) {
        super.setDamage(meta);
        updateName();
    }

    private void updateName() {
        if (getDamage() == 2) {
            name = "Empty Locator Map";
        } else {
            name = "Empty Map";
        }
    }
}
