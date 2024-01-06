package cn.nukkit.item;

public class ItemBordureIndentedBannerPattern extends ItemBannerPattern {
    public ItemBordureIndentedBannerPattern() {
        super(BORDURE_INDENTED_BANNER_PATTERN);
    }

    @Override
    public void setDamage(Integer meta) {
        throw new UnsupportedOperationException();
    }
}