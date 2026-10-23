package org.powernukkitx.network.process;

import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.packet.ClientCacheStatusPacket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.network.process.cache.ClientBlobCacheManager;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class NetworkPacketHandlerTest {

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    @Test
    void clientCacheStatus_isHandledBeforeThePlayerExists() {
        final BedrockServerSession session = mock(BedrockServerSession.class);
        // ok so this is a session mid login: no player yet, nothing else wrong with it
        final PlayerSessionHolder holder = mock(PlayerSessionHolder.class);
        doReturn(session).when(holder).getSession();
        doReturn(true).when(holder).checkRateLimits();

        final ClientCacheStatusPacket packet = new ClientCacheStatusPacket();
        packet.setCacheSupported(true);

        try {
            new NetworkPacketHandler(ServerMockFixture.server, holder).handlePacket(packet);
            Assertions.assertTrue(ClientBlobCacheManager.isEnabled(session));
        } finally {
            ClientBlobCacheManager.removeSession(session);
        }
    }
}
