package cn.nukkit.item;


public class ItemGusterBannerPattern extends ItemBannerPattern {

    public ItemGusterBannerPattern() {
        super(GUSTER_BANNER_PATTERN);
    }

    @Override
    public String getPatternCode() {
        return "gus";
    }

    @Override
    public void setDamage(int damage) {
    }
}