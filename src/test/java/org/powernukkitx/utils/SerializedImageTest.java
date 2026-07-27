package org.powernukkitx.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SerializedImageTest {

    @Test
    void constructorStoresDimensionsAndData() {
        byte[] data = {1, 2, 3};
        SerializedImage img = new SerializedImage(64, 32, data);
        assertEquals(64, img.width);
        assertEquals(32, img.height);
        assertArrayEquals(data, img.data);
    }

    @Test
    void emptyConstantHasZeroDimensions() {
        assertEquals(0, SerializedImage.EMPTY.width);
        assertEquals(0, SerializedImage.EMPTY.height);
        assertEquals(0, SerializedImage.EMPTY.data.length);
    }

    @Test
    void fromLegacySingleSkinSize() {
        SerializedImage img = SerializedImage.fromLegacy(new byte[64 * 32 * 4]);
        assertEquals(64, img.width);
        assertEquals(32, img.height);
    }

    @Test
    void fromLegacyDoubleSkinSize() {
        SerializedImage img = SerializedImage.fromLegacy(new byte[64 * 64 * 4]);
        assertEquals(64, img.width);
        assertEquals(64, img.height);
    }

    @Test
    void fromLegacyUnknownSizeFallsBackTo32() {
        SerializedImage img = SerializedImage.fromLegacy(new byte[7]);
        assertEquals(32, img.width);
        assertEquals(32, img.height);
    }

    @Test
    void fromLegacyNullThrows() {
        assertThrows(NullPointerException.class, () -> SerializedImage.fromLegacy(null));
    }
}
