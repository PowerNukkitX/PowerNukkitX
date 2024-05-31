package cn.nukkit.item;

import cn.nukkit.network.protocol.types.BannerPatternType;
import org.jetbrains.annotations.ApiStatus;


public class ItemBannerPattern extends Item {
    /**
     * @deprecated 
     */
    
    public ItemBannerPattern() {
        this(0, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemBannerPattern(Integer meta) {
        this(meta, 1);
    }
    /**
     * @deprecated 
     */
    

    public ItemBannerPattern(Integer meta, int count) {
        super(BANNER_PATTERN, meta, count, "Bone");
    }
    /**
     * @deprecated 
     */
    

    public ItemBannerPattern(String id) {
        super(id);
    }

    @ApiStatus.Internal
    /**
     * @deprecated 
     */
    
    public void internalAdjust() {
        BannerPatternType $1 = getPatternType();
        name = patternType.getName() + " Pattern";
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getMaxStackSize() {
        return 1;
    }

    public BannerPatternType getPatternType() {
        return BannerPatternType.fromTypeId(getDamage());
    }
}
