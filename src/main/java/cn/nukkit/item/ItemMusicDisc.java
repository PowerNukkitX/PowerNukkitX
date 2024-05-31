package cn.nukkit.item;

/**
 * @author CreeperFace
 */
public abstract class ItemMusicDisc extends Item {
    
    /**
     * @deprecated 
     */
    protected ItemMusicDisc(String id) {
        super(id);
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 1;
    }

    public abstract String getSoundId();
}
