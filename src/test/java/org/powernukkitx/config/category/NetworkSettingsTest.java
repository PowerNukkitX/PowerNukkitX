package org.powernukkitx.config.category;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NetworkSettingsTest {

    private static NetworkSettings.TransportType transport(String configured) {
        NetworkSettings settings = new NetworkSettings();
        settings.transport(configured);
        return settings.resolvedTransport();
    }

    @Test
    void parsesBothTransportsCaseInsensitively() {
        assertEquals(NetworkSettings.TransportType.RAKNET, transport("raknet"));
        assertEquals(NetworkSettings.TransportType.NETHERNET, transport(" NetherNet "));
    }

    @Test
    void fallsBackToRakNetForGarbage() {
        // Anything unreadable has to land on the transport ordinary clients can actually reach
        assertEquals(NetworkSettings.TransportType.RAKNET, transport("both"));
    }

    @Test
    void defaultsToRakNet() {
        assertEquals(NetworkSettings.TransportType.RAKNET, new NetworkSettings().resolvedTransport());
    }
}
