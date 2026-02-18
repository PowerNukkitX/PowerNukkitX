package cn.nukkit.item;


public class ItemFieldMasonedBannerPattern extends ItemBannerPattern {
    public ItemFieldMasonedBannerPattern() {
        super(FIELD_MASONED_BANNER_PATTERN);
    }

    @Override
    public String getPatternCode() {
        return "bri";
    }

    @Override
    public void setDamage(int damage) {
    }
}