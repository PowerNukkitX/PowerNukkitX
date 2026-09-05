package org.powernukkitx.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class TerracottaColorTest {

    @Test
    void gettersReturnConstructorValues() {
        assertEquals(0, TerracottaColor.BLACK.getDyeData());
        assertEquals(15, TerracottaColor.BLACK.getTerracottaData());
        assertEquals("Black", TerracottaColor.BLACK.getName());
        assertEquals("Ink Sac", TerracottaColor.BLACK.getDyeName());
        assertSame(BlockColor.BLACK_TERRACOTA_BLOCK_COLOR, TerracottaColor.BLACK.getColor());
    }

    @Test
    void defaultDyeNameAppendsDye() {
        assertEquals("Purple Dye", TerracottaColor.PURPLE.getDyeName());
    }

    @Test
    void lookupByDyeDataRoundTrips() {
        for (TerracottaColor color : TerracottaColor.values()) {
            assertEquals(color, TerracottaColor.getByDyeData(color.getDyeData()));
        }
    }

    @Test
    void lookupByTerracottaDataRoundTrips() {
        for (TerracottaColor color : TerracottaColor.values()) {
            assertEquals(color, TerracottaColor.getByTerracottaData(color.getTerracottaData()));
        }
    }

    @Test
    void lookupMasksToFourBits() {
        assertSame(TerracottaColor.getByDyeData(1), TerracottaColor.getByDyeData(1 | 0x10));
    }

    @Test
    void everyColorHasBlockColor() {
        for (TerracottaColor color : TerracottaColor.values()) {
            assertNotNull(color.getColor());
        }
    }
}
