package org.powernukkitx.item;

import org.powernukkitx.network.protocol.types.BannerPatternType;

public class ItemSkullBannerPattern extends ItemBannerPattern {
    public ItemSkullBannerPattern() {
        super(SKULL_BANNER_PATTERN);
    }

    @Override
    public BannerPatternType getPatternType() {
        return BannerPatternType.SKULL;
    }

    @Override
    public void setDamage(int damage) {
    }
}