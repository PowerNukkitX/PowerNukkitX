package cn.nukkit.item;

import cn.nukkit.network.protocol.types.BannerPatternType;

public class ItemGlobeBannerPattern extends ItemBannerPattern {
    public ItemGlobeBannerPattern() {
        super(GLOBE_BANNER_PATTERN);
    }

    @Override
    public BannerPatternType getPatternType() {
        return BannerPatternType.GLOBE;
    }

    @Override
    public void setDamage(int damage) {
    }
}