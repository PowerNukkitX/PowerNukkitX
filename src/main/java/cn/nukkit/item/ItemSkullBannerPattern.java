package cn.nukkit.item;

public class ItemSkullBannerPattern extends ItemBannerPattern {
    public ItemSkullBannerPattern() {
        super(SKULL_BANNER_PATTERN);
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}