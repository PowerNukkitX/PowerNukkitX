package cn.nukkit.item;

import cn.nukkit.network.protocol.types.BannerPatternType;

public class ItemSkullBannerPattern extends ItemBannerPattern {
    /**
     * @deprecated 
     */
    
    public ItemSkullBannerPattern() {
        super(SKULL_BANNER_PATTERN);
    }

    @Override
    public BannerPatternType getPatternType() {
        return BannerPatternType.SKULL;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int damage) {
    }
}