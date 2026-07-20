package org.powernukkitx.item;

import org.powernukkitx.network.protocol.types.BannerPatternType;

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