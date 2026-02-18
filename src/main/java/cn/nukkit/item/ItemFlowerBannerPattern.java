package cn.nukkit.item;


public class ItemFlowerBannerPattern extends ItemBannerPattern {
    public ItemFlowerBannerPattern() {
        super(FLOWER_BANNER_PATTERN);
    }

    @Override
    public String getPatternCode() {
        return "flo";
    }

    @Override
    public void setDamage(int damage) {
    }
}