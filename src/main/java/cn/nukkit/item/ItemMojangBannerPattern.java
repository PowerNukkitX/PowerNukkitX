package cn.nukkit.item;

import cn.nukkit.network.protocol.types.BannerPatternType;

public class ItemMojangBannerPattern extends ItemBannerPattern {
    /**
     * @deprecated 
     */
    
    public ItemMojangBannerPattern() {
        super(MOJANG_BANNER_PATTERN);
    }

    @Override
    public BannerPatternType getPatternType() {
        return BannerPatternType.MOJANG;
    }

    @Override
    /**
     * @deprecated 
     */
    
    public void setDamage(int damage) {
    }
}