package cn.nukkit.item;


public class ItemGlobeBannerPattern extends ItemBannerPattern {
    public ItemGlobeBannerPattern() {
        super(GLOBE_BANNER_PATTERN);
    }

    @Override
    public String getPatternCode() {
        return "glb";
    }

    @Override
    public void setDamage(int damage) {
    }
}