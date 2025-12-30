package cn.nukkit.item;


/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class ItemFood extends Item {
    public ItemFood(String id) {
        super(id);
    }

    public ItemFood(String id, Integer meta) {
        super(id, meta);
    }

    public ItemFood(String id, Integer meta, int count) {
        super(id, meta, count);
    }

    public ItemFood(String id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Override
    public boolean isEdible() {
        return true;
    }

    @Override
    public int getUsingTicks() {
        return 31;
    }
}
