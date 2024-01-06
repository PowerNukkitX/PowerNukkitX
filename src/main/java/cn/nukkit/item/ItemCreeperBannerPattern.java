package cn.nukkit.item;

public class ItemCreeperBannerPattern extends ItemBannerPattern {
    public ItemCreeperBannerPattern() {
        super(CREEPER_BANNER_PATTERN);
    }

    @Override
    public void setDamage(Integer meta) {
        throw new UnsupportedOperationException();
    }
}