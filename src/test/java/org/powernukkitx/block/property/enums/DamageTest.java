package org.powernukkitx.block.property.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DamageTest {

    @Test
    void roundTrip() {
        Assertions.assertEquals(4, Damage.values().length);
        for (Damage d : Damage.values()) {
            Assertions.assertSame(d, Damage.valueOf(d.name()));
        }
    }

    @Test
    void next() {
        Assertions.assertSame(Damage.SLIGHTLY_DAMAGED, Damage.UNDAMAGED.next());
        Assertions.assertSame(Damage.BROKEN, Damage.VERY_DAMAGED.next());
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, Damage.BROKEN::next);
    }
}
