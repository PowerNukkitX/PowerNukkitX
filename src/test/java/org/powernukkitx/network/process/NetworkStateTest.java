package org.powernukkitx.network.process;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NetworkStateTest {

    @Test
    void hasExpectedConstants() {
        Assertions.assertEquals(3, NetworkState.values().length);
        Assertions.assertNotNull(NetworkState.valueOf("STARTING"));
        Assertions.assertNotNull(NetworkState.valueOf("STARTED"));
        Assertions.assertNotNull(NetworkState.valueOf("STOPPING"));
    }

    @Test
    void valueOf_roundTripsForEveryConstant() {
        for (NetworkState state : NetworkState.values()) {
            Assertions.assertSame(state, NetworkState.valueOf(state.name()));
        }
    }
}
