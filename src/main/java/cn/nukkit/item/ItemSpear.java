package cn.nukkit.item;

public abstract class ItemSpear extends ItemTool {

    public ItemSpear(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public boolean isSpear() {
        return true;
    }
}
