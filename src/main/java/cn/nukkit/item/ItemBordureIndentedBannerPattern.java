package cn.nukkit.item;

import cn.nukkit.network.protocol.types.BannerPatternType;

public class ItemBordureIndentedBannerPattern extends ItemBannerPattern {
    public ItemBordureIndentedBannerPattern() {
        super(BORDURE_INDENTED_BANNER_PATTERN);
    }

    @Override
    public void setDamage(int meta) {
    }

    @Override
    public BannerPatternType getPatternType() {
        return BannerPatternType.BORDER;
    }
}