package org.powernukkitx.block.property.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TurtleEggCountTest {

    @Test
    void roundTrip() {
        Assertions.assertEquals(4, TurtleEggCount.values().length);
        for (TurtleEggCount c : TurtleEggCount.values()) {
            Assertions.assertSame(c, TurtleEggCount.valueOf(c.name()));
        }
    }

    @Test
    void nextAndBefore() {
        Assertions.assertSame(TurtleEggCount.TWO_EGG, TurtleEggCount.ONE_EGG.next());
        Assertions.assertSame(TurtleEggCount.ONE_EGG, TurtleEggCount.TWO_EGG.before());
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, TurtleEggCount.ONE_EGG::before);
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, TurtleEggCount.FOUR_EGG::next);
    }
}
