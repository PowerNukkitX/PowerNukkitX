package cn.nukkit.item;

import cn.nukkit.network.protocol.types.BannerPatternType;


public class ItemFlowBannerPattern extends ItemBannerPattern {
    public ItemFlowBannerPattern() {
        super(FLOW_BANNER_PATTERN);
    }

    @Override
    public BannerPatternType getPatternType() {
        return BannerPatternType.FLOW;
    }

    @Override
    public void setDamage(int damage) {
    }
}
