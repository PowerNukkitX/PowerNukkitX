package org.powernukkitx.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BossBarColorTest {

    @Test
    void valuesNotEmpty() {
        Assertions.assertTrue(BossBarColor.values().length > 0);
        Assertions.assertEquals(8, BossBarColor.values().length);
    }

    @Test
    void roundTrip() {
        for (BossBarColor c : BossBarColor.values()) {
            Assertions.assertSame(c, BossBarColor.valueOf(c.name()));
        }
    }

    @Test
    void toNetwork() {
        for (BossBarColor c : BossBarColor.values()) {
            Assertions.assertNotNull(c.toNetwork());
        }
    }
}
