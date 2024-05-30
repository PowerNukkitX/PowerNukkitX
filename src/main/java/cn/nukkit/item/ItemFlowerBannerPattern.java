package cn.nukkit.item;

import cn.nukkit.network.protocol.types.BannerPatternType;

public class ItemFlowerBannerPattern extends ItemBannerPattern {
    public ItemFlowerBannerPattern() {
        super(FLOWER_BANNER_PATTERN);
    }

    @Override
    public BannerPatternType getPatternType() {
        return BannerPatternType.FLOWER;
    }

    @Override
    public void setDamage(int damage) {
    }
}