package cn.nukkit.item;

public class ItemCreeperBannerPattern extends ItemBannerPattern {
    public ItemCreeperBannerPattern() {
        super(CREEPER_BANNER_PATTERN);
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}