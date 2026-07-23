package org.powernukkitx.scoreboard.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DisplaySlotTest {

    @Test
    void slotNamesMatchProtocolStrings() {
        Assertions.assertEquals("sidebar", DisplaySlot.SIDEBAR.getSlotName());
        Assertions.assertEquals("list", DisplaySlot.LIST.getSlotName());
        Assertions.assertEquals("belowname", DisplaySlot.BELOW_NAME.getSlotName());
    }

    @Test
    void valueOf_roundTripsForEveryConstant() {
        for (DisplaySlot slot : DisplaySlot.values()) {
            Assertions.assertSame(slot, DisplaySlot.valueOf(slot.name()));
        }
    }

    @Test
    void slotNamesAreUnique() {
        long distinct = java.util.Arrays.stream(DisplaySlot.values())
                .map(DisplaySlot::getSlotName)
                .distinct()
                .count();
        Assertions.assertEquals(DisplaySlot.values().length, distinct);
    }
}
