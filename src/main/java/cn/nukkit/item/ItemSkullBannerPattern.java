package cn.nukkit.item;


public class ItemSkullBannerPattern extends ItemBannerPattern {
    public ItemSkullBannerPattern() {
        super(SKULL_BANNER_PATTERN);
    }

    @Override
    public String getPatternCode() {
        return "sku";
    }

    @Override
    public void setDamage(int damage) {
    }
}