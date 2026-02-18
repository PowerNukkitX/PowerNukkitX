package cn.nukkit.item;


public class ItemMojangBannerPattern extends ItemBannerPattern {
    public ItemMojangBannerPattern() {
        super(MOJANG_BANNER_PATTERN);
    }

    @Override
    public String getPatternCode() {
        return "moj";
    }

    @Override
    public void setDamage(int damage) {
    }
}