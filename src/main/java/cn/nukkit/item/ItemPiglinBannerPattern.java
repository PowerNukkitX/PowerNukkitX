package cn.nukkit.item;

public class ItemPiglinBannerPattern extends ItemBannerPattern {
    public ItemPiglinBannerPattern() {
        super(PIGLIN_BANNER_PATTERN);
    }

    @Override
    public void setAux(Integer aux) {
        throw new UnsupportedOperationException();
    }
}