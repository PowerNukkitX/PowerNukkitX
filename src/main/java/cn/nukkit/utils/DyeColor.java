package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.MathHelper;

import java.util.Arrays;

public enum DyeColor {


    BLACK(0, 15, 16, "Black", "Ink Sac", BlockColor.BLACK_BLOCK_COLOR, new BlockColor(0x1D1D21), new BlockColor(0x00, 0x00, 0x00)),
    RED(1, 14, 1, "Red", "Red Dye", BlockColor.RED_BLOCK_COLOR, new BlockColor(0xB02E26), new BlockColor(0xb0, 0x2e, 0x26)),
    GREEN(2, 13, 2, "Green", "Green Dye", BlockColor.GREEN_BLOCK_COLOR, new BlockColor(0x5E7C16), new BlockColor(0x5e, 0x7c, 0x16)),
    BROWN(3, 12, 17, "Brown", "Cocoa Beans", BlockColor.BROWN_BLOCK_COLOR, new BlockColor(0x835432), new BlockColor(0x83, 0x54, 0x32)),
    BLUE(4, 11, 18, "Blue", "Lapis Lazuli", BlockColor.BLUE_BLOCK_COLOR, new BlockColor(0x3C44AA), new BlockColor(0x3c, 0x44, 0xaa)),
    PURPLE(5, 10, 5, "Purple", BlockColor.PURPLE_BLOCK_COLOR, new BlockColor(0x8932B8), new BlockColor(0x89, 0x32, 0xb8)),
    CYAN(6, 9, 6, "Cyan", BlockColor.CYAN_BLOCK_COLOR, new BlockColor(0x169C9C), new BlockColor(0x16, 0x9c, 0x9c)),
    LIGHT_GRAY(7, 8, 7, "Light Gray", BlockColor.LIGHT_GRAY_BLOCK_COLOR, new BlockColor(0x9D9D97), new BlockColor(0x9d, 0x9d, 0x97)),
    GRAY(8, 7, 8, "Gray", BlockColor.GRAY_BLOCK_COLOR, new BlockColor(0x474F52), new BlockColor(0x47, 0x4f, 0x52)),
    PINK(9, 6, 9, "Pink", BlockColor.PINK_BLOCK_COLOR, new BlockColor(0xF38BAA), new BlockColor(0xf3, 0x8b, 0xaa)),
    LIME(10, 5, 10, "Lime", BlockColor.LIME_BLOCK_COLOR, new BlockColor(0x80C71F), new BlockColor(0x80, 0xc7, 0x1f)),
    YELLOW(11, 4, 11, "Yellow", "Yellow Dye", BlockColor.YELLOW_BLOCK_COLOR, new BlockColor(0xFED83D), new BlockColor(0xfe, 0xd8, 0x3d)),
    LIGHT_BLUE(12, 3, 12, "Light Blue", BlockColor.LIGHT_BLUE_BLOCK_COLOR, new BlockColor(0x3AB3DA), new BlockColor(0x3a, 0xb3, 0xda)),
    MAGENTA(13, 2, 13, "Magenta", BlockColor.MAGENTA_BLOCK_COLOR, new BlockColor(0xC74EBD), new BlockColor(0xc7, 0x4e, 0xbd)),
    ORANGE(14, 1, 14, "Orange", BlockColor.ORANGE_BLOCK_COLOR, new BlockColor(0xFF9801), new BlockColor(0xf9, 0x80, 0x1d)),
    WHITE(15, 0, 19, "White", BlockColor.WHITE_BLOCK_COLOR, new BlockColor(0xF0F0F0), new BlockColor(0xf0, 0xf0, 0xf0)),
    BONE_MEAL(15, 0, 15, "White", "Bone Meal", BlockColor.WHITE_BLOCK_COLOR, new BlockColor(0xF0F0F0), new BlockColor(0xf0, 0xf0, 0xf0));


    private final int dyeColorMeta;
    private final int itemDyeMeta;
    private final int woolColorMeta;
    private final String colorName;
    private final String dyeName;
    private final BlockColor blockColor;
    private final BlockColor leatherColor;
    private final BlockColor signColor;


    private final static DyeColor[] BY_WOOL_DATA;
    private final static DyeColor[] BY_DYE_DATA;

    DyeColor(int dyeColorMeta, int woolColorMeta, String colorName, BlockColor blockColor, BlockColor signColor) {
        this(dyeColorMeta, woolColorMeta, woolColorMeta, colorName, colorName + " Dye", blockColor, signColor, blockColor);
    }

    DyeColor(int dyeColorMeta, int woolColorMeta, int itemDyeMeta, String colorName, BlockColor blockColor) {
        this(dyeColorMeta, woolColorMeta, itemDyeMeta, colorName, colorName + " Dye", blockColor, blockColor, blockColor);
    }

    DyeColor(int dyeColorMeta, int woolColorMeta, int itemDyeMeta, String colorName, BlockColor blockColor, BlockColor leatherColor, BlockColor signColor) {
        this(dyeColorMeta, woolColorMeta, itemDyeMeta, colorName, colorName + " Dye", blockColor, leatherColor, signColor);
    }

    DyeColor(int dyeColorMeta, int woolColorMeta, int itemDyeMeta, String colorName, String dyeName, BlockColor blockColor) {
        this(dyeColorMeta, woolColorMeta, itemDyeMeta, colorName, dyeName, blockColor, blockColor, blockColor);
    }

    DyeColor(int dyeColorMeta, int woolColorMeta, int itemDyeMeta, String colorName, String dyeName, BlockColor blockColor, BlockColor leatherColor, BlockColor signColor) {
        this.dyeColorMeta = dyeColorMeta;
        this.itemDyeMeta = itemDyeMeta;
        this.woolColorMeta = woolColorMeta;
        this.colorName = colorName;
        this.dyeName = dyeName;
        this.blockColor = blockColor;
        this.leatherColor = leatherColor;
        this.signColor = signColor;
    }

    public BlockColor getColor() {
        return this.blockColor;
    }

    public BlockColor getSignColor() {
        return this.signColor;
    }

    /**
     * The {@code minecraft:dye} meta from `0-15` that represents the source of a dye. Includes
     * ink_sac, bone_meal, cocoa_beans, and lapis_lazuli.
     */
    public int getDyeData() {
        return this.dyeColorMeta;
    }

    /**
     * The {@code minecraft:dye} meta that actually represents the item dye for that color.
     * Uses black_dye instead of ink_sac, white_dye instead of bone_meal, and so on.
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getItemDyeMeta() {
        return itemDyeMeta;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockColor getLeatherColor() {
        return leatherColor;
    }

    public int getWoolData() {
        return this.woolColorMeta;
    }

    public String getName() {
        return this.colorName;
    }

    public String getDyeName() {
        return this.dyeName;
    }

    static {
        BY_WOOL_DATA = values();
        BY_DYE_DATA = new DyeColor[Arrays.stream(BY_WOOL_DATA).mapToInt(DyeColor::getItemDyeMeta).max().orElse(0) + 1];

        for (DyeColor dyeColor : BY_WOOL_DATA) {
            BY_DYE_DATA[dyeColor.dyeColorMeta] = dyeColor;
            BY_DYE_DATA[dyeColor.itemDyeMeta] = dyeColor;
        }

        for (DyeColor color : values()) {
            BY_WOOL_DATA[color.woolColorMeta & 0x0f] = color;
        }
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "When overflowed, instead of wrapping, the meta will be clamped, accepts the new dye metas")
    public static DyeColor getByDyeData(int dyeColorMeta) {
        return BY_DYE_DATA[MathHelper.clamp(dyeColorMeta, 0, BY_DYE_DATA.length - 1)];
    }

    public static DyeColor getByWoolData(int woolColorMeta) {
        return BY_WOOL_DATA[woolColorMeta & 0x0f];
    }
}
