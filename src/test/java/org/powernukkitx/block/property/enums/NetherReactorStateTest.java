package org.powernukkitx.block.property.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NetherReactorStateTest {

    @Test
    void roundTrip() {
        Assertions.assertEquals(3, NetherReactorState.values().length);
        for (NetherReactorState s : NetherReactorState.values()) {
            Assertions.assertSame(s, NetherReactorState.valueOf(s.name()));
        }
    }

    @Test
    void getFromData() {
        for (NetherReactorState s : NetherReactorState.values()) {
            Assertions.assertSame(s, NetherReactorState.getFromData(s.ordinal()));
        }
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> NetherReactorState.getFromData(99));
    }
}
