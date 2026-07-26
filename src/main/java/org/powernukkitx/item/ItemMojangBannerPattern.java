package org.powernukkitx.item;

import org.powernukkitx.network.protocol.types.BannerPatternType;

public class ItemMojangBannerPattern extends ItemBannerPattern {
    public ItemMojangBannerPattern() {
        super(MOJANG_BANNER_PATTERN);
    }

    @Override
    public BannerPatternType getPatternType() {
        return BannerPatternType.MOJANG;
    }

    @Override
    public void setDamage(int damage) {
    }
}