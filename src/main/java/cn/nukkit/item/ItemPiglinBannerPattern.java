package cn.nukkit.item;


public class ItemPiglinBannerPattern extends ItemBannerPattern {
    public ItemPiglinBannerPattern() {
        super(PIGLIN_BANNER_PATTERN);
    }

    @Override
    public String getPatternCode() {
        return "pig";
    }

    @Override
    public void setDamage(int damage) {
    }
}