package cn.nukkit.item;

import cn.nukkit.utils.BannerPattern;

import java.util.Objects;


public class ItemBannerPattern extends Item {
    public static final int PATTERN_CREEPER_CHARGE = 0;
    public static final int PATTERN_SKULL_CHARGE = 1;
    public static final int PATTERN_FLOWER_CHARGE = 2;
    public static final int PATTERN_THING = 3;
    public static final int PATTERN_FIELD_MASONED = 4;
    public static final int PATTERN_BORDURE_INDENTED = 5;
    public static final int PATTERN_SNOUT = 6;
    public static final int PATTERN_GLOBE = 7;

    public ItemBannerPattern() {
        this(0, 1);
    }

    public ItemBannerPattern(Integer meta) {
        this(meta, 1);
    }

    public ItemBannerPattern(Integer meta, int count) {
        super(BANNER_PATTERN, meta, count, "Bone");
        updateName();
    }

    public ItemBannerPattern(String id) {
        super(id);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void setAux(Integer aux) {
        super.setAux(aux);
        updateName();
    }

    public BannerPattern.Type getPatternType() {
        if (!Objects.equals(getId(), BANNER_PATTERN)) {
            return BannerPattern.Type.PATTERN_CREEPER;
        }
        return switch (getAux()) {
            default -> BannerPattern.Type.PATTERN_CREEPER;
            case PATTERN_SKULL_CHARGE -> BannerPattern.Type.PATTERN_SKULL;
            case PATTERN_FLOWER_CHARGE -> BannerPattern.Type.PATTERN_FLOWER;
            case PATTERN_THING -> BannerPattern.Type.PATTERN_MOJANG;
            case PATTERN_FIELD_MASONED -> BannerPattern.Type.PATTERN_BRICK;
            case PATTERN_BORDURE_INDENTED -> BannerPattern.Type.PATTERN_CURLY_BORDER;
            case PATTERN_SNOUT -> BannerPattern.Type.PATTERN_SNOUT;
            case PATTERN_GLOBE -> BannerPattern.Type.PATTERN_GLOBE;
        };
    }

    protected void updateName() {
        if (!Objects.equals(getId(), BANNER_PATTERN)) {
            return;
        }
        switch (super.aux) {
            case PATTERN_CREEPER_CHARGE -> name = "Creeper Charge Banner Pattern";
            case PATTERN_SKULL_CHARGE -> name = "Skull Charge Banner Pattern";
            case PATTERN_FLOWER_CHARGE -> name = "Flower Charge Banner Pattern";
            case PATTERN_THING -> name = "Thing Banner Pattern";
            case PATTERN_FIELD_MASONED -> name = "Field Banner Pattern";
            case PATTERN_BORDURE_INDENTED -> name = "Bordure Indented Banner Pattern";
            case PATTERN_SNOUT -> name = "Snout Banner Pattern";
            case PATTERN_GLOBE -> name = "Globe Banner Pattern";
            default -> name = "Banner Pattern";
        }
    }
}
