package org.powernukkitx.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class BlockColorTest {

    @Test
    void rgbConstructorSetsComponents() {
        BlockColor c = new BlockColor(0x10, 0x20, 0x30);
        assertEquals(0x10, c.getRed());
        assertEquals(0x20, c.getGreen());
        assertEquals(0x30, c.getBlue());
        assertEquals(0xff, c.getAlpha());
    }

    @Test
    void componentsAreMaskedToByte() {
        BlockColor c = new BlockColor(0x1ff, 0x2ff, 0x3ff, 0x4ff);
        assertEquals(0xff, c.getRed());
        assertEquals(0xff, c.getGreen());
        assertEquals(0xff, c.getBlue());
        assertEquals(0xff, c.getAlpha());
    }

    @Test
    void getRgbPacksComponents() {
        BlockColor c = new BlockColor(0x12, 0x34, 0x56);
        assertEquals(0x123456, c.getRGB());
    }

    @Test
    void getArgbPacksAlphaAndComponents() {
        BlockColor c = new BlockColor(0x12, 0x34, 0x56, 0x78);
        assertEquals(0x78123456, c.getARGB());
    }

    @Test
    void singleIntRgbConstructorUnpacks() {
        BlockColor c = new BlockColor(0x123456);
        assertEquals(0x12, c.getRed());
        assertEquals(0x34, c.getGreen());
        assertEquals(0x56, c.getBlue());
        assertEquals(0xff, c.getAlpha());
    }

    @Test
    void singleIntWithAlpha() {
        BlockColor c = new BlockColor(0x78123456, true);
        assertEquals(0x78, c.getAlpha());
        assertEquals(0x12, c.getRed());
    }

    @Test
    void equalsIgnoresTintAndComparesComponents() {
        BlockColor a = new BlockColor(1, 2, 3, 4);
        BlockColor b = new BlockColor(1, 2, 3, 4);
        assertEquals(a, b);
        assertNotEquals(a, new BlockColor(1, 2, 3, 5));
    }

    @Test
    void cloneProducesEqualDistinctInstance() {
        BlockColor a = new BlockColor(10, 20, 30, 40);
        BlockColor clone = a.clone();
        assertNotSame(a, clone);
        assertEquals(a, clone);
        assertEquals(a.getTint(), clone.getTint());
    }

    @Test
    void toAwtColorMatchesComponents() {
        BlockColor c = new BlockColor(10, 20, 30, 40);
        java.awt.Color awt = c.toAwtColor();
        assertEquals(10, awt.getRed());
        assertEquals(20, awt.getGreen());
        assertEquals(30, awt.getBlue());
        assertEquals(40, awt.getAlpha());
    }

    @Test
    void tintGetResolvesKnownAndFallsBackToNone() {
        assertEquals(BlockColor.Tint.GRASS, BlockColor.Tint.get("Grass"));
        assertEquals(BlockColor.Tint.NONE, BlockColor.Tint.get("does-not-exist"));
    }
}
