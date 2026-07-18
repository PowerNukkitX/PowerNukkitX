package org.powernukkitx.block.property.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CrackedStateTest {

    @Test
    void roundTrip() {
        Assertions.assertEquals(3, CrackedState.values().length);
        for (CrackedState s : CrackedState.values()) {
            Assertions.assertSame(s, CrackedState.valueOf(s.name()));
        }
    }

    @Test
    void next() {
        Assertions.assertSame(CrackedState.CRACKED, CrackedState.NO_CRACKS.next());
        Assertions.assertSame(CrackedState.MAX_CRACKED, CrackedState.CRACKED.next());
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, CrackedState.MAX_CRACKED::next);
    }
}
