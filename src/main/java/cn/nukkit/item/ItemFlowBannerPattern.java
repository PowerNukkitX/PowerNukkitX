package cn.nukkit.item;

import cn.nukkit.network.protocol.types.BannerPatternType;


/**
 * todo future
 */
public class ItemFlowBannerPattern extends ItemBannerPattern {
    public ItemFlowBannerPattern() {
        super(FLOW_BANNER_PATTERN);
    }

    @Override
    public BannerPatternType getPatternType() {
        //todo future
        return null;
    }

    @Override
    public void setDamage(int damage) {
    }
}
