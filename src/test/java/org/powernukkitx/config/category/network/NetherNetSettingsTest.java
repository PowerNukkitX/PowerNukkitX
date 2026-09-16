package org.powernukkitx.config.category.network;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NetherNetSettingsTest {

    private static NetherNetSettings.SignalingMode mode(String configured) {
        NetherNetSettings settings = new NetherNetSettings();
        settings.signalingMode(configured);
        return settings.resolvedSignalingMode();
    }

    @Test
    void parsesEveryModeCaseInsensitively() {
        assertEquals(NetherNetSettings.SignalingMode.BUILTIN, mode("builtin"));
        assertEquals(NetherNetSettings.SignalingMode.NXS, mode(" NXS "));
        assertEquals(NetherNetSettings.SignalingMode.HYBRID, mode("Hybrid"));
        assertEquals(NetherNetSettings.SignalingMode.PLUGIN, mode("plugin"));
    }

    @Test
    void fallsBackToBuiltinForGarbage() {
        assertEquals(NetherNetSettings.SignalingMode.BUILTIN, mode("nonsense"));
    }

    @Test
    void hybridDoesBothAndNxsBindsNothing() {
        assertTrue(NetherNetSettings.SignalingMode.HYBRID.builtin());
        assertTrue(NetherNetSettings.SignalingMode.HYBRID.nxs());
        assertTrue(NetherNetSettings.SignalingMode.HYBRID.binds());

        assertFalse(NetherNetSettings.SignalingMode.NXS.builtin());
        assertFalse(NetherNetSettings.SignalingMode.NXS.binds());

        // plugin binds a channel so acceptOffer works, it just serves no endpoint
        assertTrue(NetherNetSettings.SignalingMode.PLUGIN.binds());
        assertFalse(NetherNetSettings.SignalingMode.PLUGIN.builtin());
    }
}
