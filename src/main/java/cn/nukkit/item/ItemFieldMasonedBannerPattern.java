package cn.nukkit.item;

import cn.nukkit.network.protocol.types.BannerPatternType;

public class ItemFieldMasonedBannerPattern extends ItemBannerPattern {
    public ItemFieldMasonedBannerPattern() {
        super(FIELD_MASONED_BANNER_PATTERN);
    }

    @Override
    public BannerPatternType getPatternType() {
        return BannerPatternType.BRICKS;
    }

    @Override
    public void setDamage(int damage) {
    }
}