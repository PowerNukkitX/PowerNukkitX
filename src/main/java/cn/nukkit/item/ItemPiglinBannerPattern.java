package cn.nukkit.item;

import cn.nukkit.network.protocol.types.BannerPatternType;

public class ItemPiglinBannerPattern extends ItemBannerPattern {
    /**
     * @deprecated 
     */
    
    public ItemPiglinBannerPattern() {
        super(PIGLIN_BANNER_PATTERN);
    }

    @Override
    public BannerPatternType getPatternType() {
        return BannerPatternType.PIGLIN;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int damage) {
    }
}