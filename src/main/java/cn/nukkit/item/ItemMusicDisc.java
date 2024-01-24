package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public abstract class ItemMusicDisc extends Item {
    protected ItemMusicDisc(String id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public abstract String getSoundId();
}
