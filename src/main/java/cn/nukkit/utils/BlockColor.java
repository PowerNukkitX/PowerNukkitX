package cn.nukkit.utils;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.ItemRegistry;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Snake1999
 * @since 2016/1/10
 */
public class BlockColor implements Cloneable {

    private static final CompoundTag tint_tag;

    static {
        try (var stream = ItemRegistry.class.getClassLoader().getResourceAsStream("tint_map.nbt")) {
            tint_tag = NBTIO.readCompressed(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final BlockColor TRANSPARENT_BLOCK_COLOR = new BlockColor(0x00, 0x00, 0x00, 0x00);
    public static final BlockColor VOID_BLOCK_COLOR = TRANSPARENT_BLOCK_COLOR;
    public static final BlockColor AIR_BLOCK_COLOR = TRANSPARENT_BLOCK_COLOR;

    public static final BlockColor GRASS_BLOCK_COLOR = new BlockColor(0x7f, 0xb2, 0x38);
    public static final BlockColor SAND_BLOCK_COLOR = new BlockColor(0xf7, 0xe9, 0xa3);
    public static final BlockColor CLOTH_BLOCK_COLOR = new BlockColor(0xc7, 0xc7, 0xc7);
    public static final BlockColor TNT_BLOCK_COLOR = new BlockColor(0xff, 0x00, 0x00);
    public static final BlockColor ICE_BLOCK_COLOR = new BlockColor(0xa0, 0xa0, 0xff);
    public static final BlockColor IRON_BLOCK_COLOR = new BlockColor(0xa7, 0xa7, 0xa7);
    public static final BlockColor FOLIAGE_BLOCK_COLOR = new BlockColor(0x00, 0x7c, 0x00);
    public static final BlockColor SNOW_BLOCK_COLOR = new BlockColor(0xff, 0xff, 0xff);
    public static final BlockColor CLAY_BLOCK_COLOR = new BlockColor(0xa4, 0xa8, 0xb8);
    public static final BlockColor DIRT_BLOCK_COLOR = new BlockColor(0x97, 0x6d, 0x4d);
    public static final BlockColor STONE_BLOCK_COLOR = new BlockColor(0x70, 0x70, 0x70);
    public static final BlockColor WATER_BLOCK_COLOR = new BlockColor(64, 64, 160);


    public static final BlockColor FLOWING_WATER_BLOCK_COLOR = new BlockColor(30, 90, 245, 0);
    public static final BlockColor LAVA_BLOCK_COLOR = TNT_BLOCK_COLOR;
    public static final BlockColor WOOD_BLOCK_COLOR = new BlockColor(0x8f, 0x77, 0x48);
    public static final BlockColor QUARTZ_BLOCK_COLOR = new BlockColor(0xff, 0xfc, 0xf5);
    public static final BlockColor ADOBE_BLOCK_COLOR = new BlockColor(0xd8, 0x7f, 0x33);

    public static final BlockColor WHITE_BLOCK_COLOR = SNOW_BLOCK_COLOR;
    public static final BlockColor ORANGE_BLOCK_COLOR = ADOBE_BLOCK_COLOR;
    public static final BlockColor MAGENTA_BLOCK_COLOR = new BlockColor(0xb2, 0x4c, 0xd8);
    public static final BlockColor LIGHT_BLUE_BLOCK_COLOR = new BlockColor(0x66, 0x99, 0xd8);
    public static final BlockColor YELLOW_BLOCK_COLOR = new BlockColor(0xe5, 0xe5, 0x33);
    public static final BlockColor LIME_BLOCK_COLOR = new BlockColor(0x7f, 0xcc, 0x19);
    public static final BlockColor PINK_BLOCK_COLOR = new BlockColor(0xf2, 0x7f, 0xa5);
    public static final BlockColor GRAY_BLOCK_COLOR = new BlockColor(0x4c, 0x4c, 0x4c);
    public static final BlockColor LIGHT_GRAY_BLOCK_COLOR = new BlockColor(0x99, 0x99, 0x99);
    public static final BlockColor CYAN_BLOCK_COLOR = new BlockColor(0x4c, 0x7f, 0x99);
    public static final BlockColor PURPLE_BLOCK_COLOR = new BlockColor(0x7f, 0x3f, 0xb2);
    public static final BlockColor BLUE_BLOCK_COLOR = new BlockColor(0x33, 0x4c, 0xb2);
    public static final BlockColor BROWN_BLOCK_COLOR = new BlockColor(0x66, 0x4c, 0x33);
    public static final BlockColor GREEN_BLOCK_COLOR = new BlockColor(0x66, 0x7f, 0x33);
    public static final BlockColor RED_BLOCK_COLOR = new BlockColor(0x99, 0x33, 0x33);
    public static final BlockColor BLACK_BLOCK_COLOR = new BlockColor(0x19, 0x19, 0x19);

    public static final BlockColor GOLD_BLOCK_COLOR = new BlockColor(0xfa, 0xee, 0x4d);
    public static final BlockColor DIAMOND_BLOCK_COLOR = new BlockColor(0x5c, 0xdb, 0xd5);
    public static final BlockColor LAPIS_BLOCK_COLOR = new BlockColor(0x4a, 0x80, 0xff);
    public static final BlockColor EMERALD_BLOCK_COLOR = new BlockColor(0x00, 0xd9, 0x3a);
    public static final BlockColor OBSIDIAN_BLOCK_COLOR = new BlockColor(0x15, 0x14, 0x1f);
    public static final BlockColor SPRUCE_BLOCK_COLOR = new BlockColor(0x81, 0x56, 0x31);
    public static final BlockColor NETHERRACK_BLOCK_COLOR = new BlockColor(0x70, 0x02, 0x00);
    public static final BlockColor REDSTONE_BLOCK_COLOR = TNT_BLOCK_COLOR;

    public static final BlockColor WHITE_TERRACOTA_BLOCK_COLOR = new BlockColor(0xd1, 0xb1, 0xa1);
    public static final BlockColor ORANGE_TERRACOTA_BLOCK_COLOR = new BlockColor(0x9f, 0x52, 0x24);
    public static final BlockColor MAGENTA_TERRACOTA_BLOCK_COLOR = new BlockColor(0x95, 0x57, 0x6c);
    public static final BlockColor LIGHT_BLUE_TERRACOTA_BLOCK_COLOR = new BlockColor(0x70, 0x6c, 0x8a);
    public static final BlockColor YELLOW_TERRACOTA_BLOCK_COLOR = new BlockColor(0xba, 0x85, 0x24);
    public static final BlockColor LIME_TERRACOTA_BLOCK_COLOR = new BlockColor(0x67, 0x75, 0x35);
    public static final BlockColor PINK_TERRACOTA_BLOCK_COLOR = new BlockColor(0xa0, 0x4d, 0x4e);
    public static final BlockColor GRAY_TERRACOTA_BLOCK_COLOR = new BlockColor(0x39, 0x29, 0x23);
    public static final BlockColor LIGHT_GRAY_TERRACOTA_BLOCK_COLOR = new BlockColor(0x87, 0x6b, 0x62);
    public static final BlockColor CYAN_TERRACOTA_BLOCK_COLOR = new BlockColor(0x57, 0x5c, 0x5c);
    public static final BlockColor PURPLE_TERRACOTA_BLOCK_COLOR = new BlockColor(0x7a, 0x49, 0x58);
    public static final BlockColor BLUE_TERRACOTA_BLOCK_COLOR = new BlockColor(0x4c, 0x3e, 0x5c);
    public static final BlockColor BROWN_TERRACOTA_BLOCK_COLOR = new BlockColor(0x4c, 0x32, 0x23);
    public static final BlockColor GREEN_TERRACOTA_BLOCK_COLOR = new BlockColor(0x4c, 0x52, 0x2a);
    public static final BlockColor RED_TERRACOTA_BLOCK_COLOR = new BlockColor(0x8e, 0x3c, 0x2e);
    public static final BlockColor BLACK_TERRACOTA_BLOCK_COLOR = new BlockColor(0x25, 0x16, 0x10);


    public static final BlockColor CRIMSON_NYLIUM_BLOCK_COLOR = new BlockColor(0xBD, 0x30, 0x31);


    public static final BlockColor CRIMSON_STEM_BLOCK_COLOR = new BlockColor(0x94, 0x3F, 0x61);


    public static final BlockColor CRIMSON_HYPHAE_BLOCK_COLOR = new BlockColor(0x5C, 0x19, 0x1D);


    public static final BlockColor WARPED_NYLIUM_BLOCK_COLOR = new BlockColor(0x16, 0x7E, 0x86);


    public static final BlockColor WARPED_STEM_BLOCK_COLOR = new BlockColor(0x3A, 0x8E, 0x8C);


    public static final BlockColor WARPED_HYPHAE_BLOCK_COLOR = new BlockColor(0x56, 0x2C, 0x3E);


    public static final BlockColor WARPED_WART_BLOCK_COLOR = new BlockColor(0x14, 0xB4, 0x85);


    public static final BlockColor SCULK_BLOCK_COLOR = new BlockColor(0x0d, 0x12, 0x17);


    public static final BlockColor DEEPSLATE_GRAY = new BlockColor(0x64, 0x64, 0x64);


    public static final BlockColor RAW_IRON_BLOCK_COLOR = new BlockColor(0xd8, 0xaf, 0x93);


    public static final BlockColor LICHEN_GREEN = new BlockColor(0x7F, 0xA7, 0x96);


    public static final BlockColor BROWNISH_RED = new BlockColor(0x8E, 0x2F, 0x2F);


    public static final BlockColor SMALL_AMETHYST_BUD_COLOR = new BlockColor(153, 90, 205);


    public static final BlockColor CORAL_FAN_COLOR = new BlockColor(146, 188, 88, 0);


    public static final BlockColor REPEATING_COMMAND_BLOCK_COLOR = new BlockColor(153, 90, 205);

    private int red;
    private int green;
    private int blue;
    private int alpha;
    private Tint tint;

    public BlockColor(int red, int green, int blue, int alpha) {
        this(red, green, blue, alpha, Tint.NONE);
    }

    public BlockColor(int red, int green, int blue, int alpha, Tint tint) {
        this.red = red & 0xff;
        this.green = green & 0xff;
        this.blue = blue & 0xff;
        this.alpha = alpha & 0xff;
        this.tint = tint;
    }

    public BlockColor(int red, int green, int blue) {
        this(red, green, blue, 0xff);
    }

    public BlockColor(int rgb) {
        this(rgb, false);
    }

    public BlockColor(int rgb, boolean hasAlpha) {
        this.red = (rgb >> 16) & 0xff;
        this.green = (rgb >> 8) & 0xff;
        this.blue = rgb & 0xff;
        this.alpha = hasAlpha ? (rgb >> 24) & 0xff : 0xff;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockColor)) {
            return false;
        }
        BlockColor other = (BlockColor) obj;
        return this.red == other.red && this.green == other.green &&
                this.blue == other.blue && this.alpha == other.alpha;
    }

    @Override
    public String toString() {
        return "BlockColor[r=" + this.red + ",g=" + this.green + ",b=" + this.blue + ",a=" + this.alpha + "]";
    }

    public int getRed() {
        return this.red;
    }

    public int getGreen() {
        return this.green;
    }

    public int getBlue() {
        return this.blue;
    }

    public int getAlpha() {
        return this.alpha;
    }

    public Tint getTint() {
        return tint;
    }

    public int getRGB() {
        return (this.red << 16 | this.green << 8 | this.blue) & 0xffffff;
    }

    public int getARGB() {
        return this.alpha << 24 | this.red << 16 | this.green << 8 | this.blue;
    }

    public Color toAwtColor() {
        return new Color(this.red, this.green, this.blue, this.alpha);
    }

    public static BlockColor getDyeColor(int dyeColorMeta) {
        return DyeColor.getByDyeData(dyeColorMeta).getColor();
    }

    public void applyTint(int biomeId) {
        if(tint != Tint.NONE) {
            try {
                String hexString = tint_tag.getCompound(String.valueOf(biomeId)).getString(tint.texture);
                red =  Integer.parseInt(hexString.substring(0,2), 16);
                green = Integer.parseInt(hexString.substring(2,4), 16);
                blue = Integer.parseInt(hexString.substring(4,6), 16);
                alpha = Integer.parseInt(hexString.substring(6,8), 16);
            }catch (Exception e) {
                e.printStackTrace();
                System.out.println(tint_tag.getCompound(String.valueOf(biomeId)).getString(tint.texture));
            }
        }
    }

    @Override
    public BlockColor clone() {
        return new BlockColor(red, green, blue, alpha, tint);
    }


    public enum Tint {

        NONE("None"),
        DRY_FOLIAGE("DryFoliage", "dry_foliage"),
        DEFAULT_FOLIAGE("DefaultFoliage", "foliage"),
        BIRCH_FOLIAGE("BirchFoliage", "birch"),
        //REDSTONE_WIRE("RedStoneWire"),
        EVERGREEN_FOLIAGE("EvergreenFoliage", "evergreen"),
        //WATER("Water"),
        //STEM("Stem"),
        GRASS("Grass", "grass");

        String name;
        String texture;

        public static Tint[] VALUES = values();

        Tint(String name) {
            this(name, "foliage");
        }

        Tint(String name, String texture) {
            this.name = name;
            this.texture = texture;
        }

        public static Tint get(String name) {
            return Arrays.stream(VALUES).filter(tint -> tint.name.equals(name)).findFirst().orElse(NONE);
        }
    }
}
