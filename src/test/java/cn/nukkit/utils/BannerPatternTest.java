package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.ItemBannerPattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author joserobjr
 * @since 2021-12-19
 */
@PowerNukkitOnly
@Since("FUTURE")
@ExtendWith(PowerNukkitExtension.class)
class BannerPatternTest {
    ItemBannerPattern item;
    @Test
    void defaultName() {
        item = new ItemBannerPattern(1000);
        assertEquals("Banner Pattern", item.getName());
    }

    @Test
    void types() {
        item = new ItemBannerPattern();
        assertEquals(BannerPattern.Type.PATTERN_CREEPER, setAndReturnType(ItemBannerPattern.PATTERN_CREEPER_CHARGE));
        assertEquals(BannerPattern.Type.PATTERN_SKULL, setAndReturnType(ItemBannerPattern.PATTERN_SKULL_CHARGE));
        assertEquals(BannerPattern.Type.PATTERN_FLOWER, setAndReturnType(ItemBannerPattern.PATTERN_FLOWER_CHARGE));
        assertEquals(BannerPattern.Type.PATTERN_MOJANG, setAndReturnType(ItemBannerPattern.PATTERN_THING));
        assertEquals(BannerPattern.Type.PATTERN_BRICK, setAndReturnType(ItemBannerPattern.PATTERN_FIELD_MASONED));
        assertEquals(BannerPattern.Type.PATTERN_CURLY_BORDER, setAndReturnType(ItemBannerPattern.PATTERN_BORDURE_INDENTED));
        assertEquals(BannerPattern.Type.PATTERN_SNOUT, setAndReturnType(ItemBannerPattern.PATTERN_SNOUT));
    }

    BannerPattern.Type setAndReturnType(int damage) {
        item.setDamage(damage);
        return item.getPatternType();
    }
}
