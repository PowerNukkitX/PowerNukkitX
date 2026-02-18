package cn.nukkit.item;



public class ItemFlowBannerPattern extends ItemBannerPattern {
    public ItemFlowBannerPattern() {
        super(FLOW_BANNER_PATTERN);
    }

    @Override
    public String getPatternCode() {
        return "flw";
    }

    @Override
    public void setDamage(int damage) {
    }
}
