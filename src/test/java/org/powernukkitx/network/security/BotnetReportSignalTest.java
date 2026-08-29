package org.powernukkitx.network.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BotnetReportSignalTest {

    @Test
    void hasExpectedSignals() {
        Assertions.assertEquals(5, BotnetReport.Signal.values().length);
        Assertions.assertNotNull(BotnetReport.Signal.valueOf("VOLUME"));
        Assertions.assertNotNull(BotnetReport.Signal.valueOf("TIMING"));
        Assertions.assertNotNull(BotnetReport.Signal.valueOf("PACKET_TYPE"));
        Assertions.assertNotNull(BotnetReport.Signal.valueOf("SUBNET"));
        Assertions.assertNotNull(BotnetReport.Signal.valueOf("FRESH_SESSIONS"));
    }

    @Test
    void valueOf_roundTripsForEveryConstant() {
        for (BotnetReport.Signal signal : BotnetReport.Signal.values()) {
            Assertions.assertSame(signal, BotnetReport.Signal.valueOf(signal.name()));
        }
    }
}
