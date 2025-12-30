package cn.nukkit.item;

import cn.nukkit.network.protocol.types.BannerPatternType;

public class ItemGusterBannerPattern extends ItemBannerPattern {

    public ItemGusterBannerPattern() {
        super(GUSTER_BANNER_PATTERN);
    }

    @Override
    public BannerPatternType getPatternType() {
        return BannerPatternType.GUSTER;
    }

    @Override
    public void setDamage(int damage) {
    }
}