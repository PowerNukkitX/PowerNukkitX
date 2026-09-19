package org.powernukkitx.level.format.palette;

import org.junit.jupiter.api.Test;
import org.powernukkitx.level.format.ChunkSection;
import org.powernukkitx.level.format.bitarray.BitArray;
import org.powernukkitx.level.format.bitarray.BitArrayVersion;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies palette resizing preserves the exact bit-array representation.
 *
 * @author Curse
 */
public class PaletteResizeTest {

    @Test
    void resizePreservesBitArrayLayout() {
        Palette<Integer> palette = new Palette<>(0, BitArrayVersion.V0);
        int[] expectedValues = new int[ChunkSection.SIZE];

        assertResize(palette, expectedValues, 1, BitArrayVersion.V1);
        assertResize(palette, expectedValues, 2, BitArrayVersion.V2);
        assertResize(palette, expectedValues, 4, BitArrayVersion.V3);
        assertResize(palette, expectedValues, 8, BitArrayVersion.V4);
        assertResize(palette, expectedValues, 16, BitArrayVersion.V5);
        assertResize(palette, expectedValues, 32, BitArrayVersion.V6);
        assertResize(palette, expectedValues, 64, BitArrayVersion.V8);
        assertResize(palette, expectedValues, 256, BitArrayVersion.V16);
    }

    private static void assertResize(
            Palette<Integer> palette,
            int[] expectedValues,
            int targetValue,
            BitArrayVersion expectedVersion
    ) {
        for (int value = palette.palette.size(); value <= targetValue; value++) {
            int index = value - 1;
            palette.set(index, value);
            expectedValues[index] = value;
        }

        assertEquals(expectedVersion, palette.bitArray.version());

        BitArray expected = expectedVersion.createArray(ChunkSection.SIZE);
        for (int index = 0; index < expectedValues.length; index++) {
            expected.set(index, expectedValues[index]);
        }

        assertArrayEquals(expected.words(), palette.bitArray.words());
        for (int index = 0; index < expectedValues.length; index++) {
            assertEquals(expectedValues[index], palette.get(index));
        }
    }
}
