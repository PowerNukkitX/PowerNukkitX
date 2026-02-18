package cn.nukkit.item;

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
        name = getPatternCode() + " Pattern";
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public String getPatternCode() {
        return switch (getDamage()) {
            case 0 -> "base";
            case 1 -> "bl";
            case 2 -> "br";
            case 3 -> "tl";
            case 4 -> "tr";
            case 5 -> "bs";
            case 6 -> "ts";
            case 7 -> "ls";
            case 8 -> "rs";
            case 9 -> "cs";
            case 10 -> "ms";
            case 11 -> "drs";
            case 12 -> "dls";
            case 13 -> "ss";
            case 14 -> "cr";
            case 15 -> "sc";
            default -> "base";
        };
    }
}
