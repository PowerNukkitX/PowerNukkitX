package org.powernukkitx.network.process;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SessionStateTest {

    @Test
    void hasExpectedConstants() {
        Assertions.assertEquals(7, SessionState.values().length);
        Assertions.assertNotNull(SessionState.valueOf("INITIAL"));
        Assertions.assertNotNull(SessionState.valueOf("REQUESTED_NETWORK_SETTINGS"));
        Assertions.assertNotNull(SessionState.valueOf("LOGIN"));
        Assertions.assertNotNull(SessionState.valueOf("ENCRYPTION"));
        Assertions.assertNotNull(SessionState.valueOf("RESOURCE_PACK"));
        Assertions.assertNotNull(SessionState.valueOf("BEFORE_SPAWN"));
        Assertions.assertNotNull(SessionState.valueOf("CHUNKS"));
    }

    @Test
    void initialIsFirstConstant() {
        Assertions.assertEquals(0, SessionState.INITIAL.ordinal());
    }

    @Test
    void valueOf_roundTripsForEveryConstant() {
        for (SessionState state : SessionState.values()) {
            Assertions.assertSame(state, SessionState.valueOf(state.name()));
        }
    }
}
