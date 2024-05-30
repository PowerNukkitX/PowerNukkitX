package cn.nukkit.item;

import cn.nukkit.network.protocol.types.BannerPatternType;
import org.jetbrains.annotations.ApiStatus;


public class ItemBannerPattern extends Item {
    public ItemBannerPattern() {
        this(0, 1);
    }

    public ItemBannerPattern(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPattern(Integer meta, int count) {
        super(BANNER_PATTERN, meta, count, "Bone");
    }

    public ItemBannerPattern(String id) {
        super(id);
    }

    @ApiStatus.Internal
    public void internalAdjust() {
        BannerPatternType patternType = getPatternType();
        name = patternType.getName() + " Pattern";
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public BannerPatternType getPatternType() {
        return BannerPatternType.fromTypeId(getDamage());
    }
}
