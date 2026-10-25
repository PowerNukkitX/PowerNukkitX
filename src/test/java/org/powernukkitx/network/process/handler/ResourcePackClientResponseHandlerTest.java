package org.powernukkitx.network.process.handler;

import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.cloudburstmc.protocol.bedrock.data.ResourcePackResponse;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackClientResponsePacket;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.network.process.SessionState;
import org.powernukkitx.resourcepacks.ResourcePackManager;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResourcePackClientResponseHandlerTest {

    private final ResourcePackClientResponseHandler handler = new ResourcePackClientResponseHandler();

    @BeforeAll
    void setup() {
        ServerMockFixture.boot();
        doReturn(mock(ResourcePackManager.class)).when(ServerMockFixture.server).getResourcePackManager();
    }

    private ResourcePackClientResponsePacket downloading(String packId) {
        ResourcePackClientResponsePacket packet = new ResourcePackClientResponsePacket();
        packet.setResponse(ResourcePackResponse.DOWNLOADING);
        packet.getDownloadingPacks().add(packId);
        return packet;
    }

    private PlayerSessionHolder holderInResourcePackState() {
        PlayerSessionHolder holder = mock(PlayerSessionHolder.class);
        doReturn(SessionState.RESOURCE_PACK).when(holder).getState();
        return holder;
    }

    @Test
    void malformedPackIdDisconnectsInsteadOfThrowing() {
        PlayerSessionHolder holder = holderInResourcePackState();

        assertDoesNotThrow(() -> handler.handle(downloading("not-a-uuid_pack"), holder, ServerMockFixture.server));

        verify(holder).disconnect(DisconnectFailReason.UNEXPECTED_PACKET);
    }

    @Test
    void emptyPackIdDisconnectsInsteadOfThrowing() {
        PlayerSessionHolder holder = holderInResourcePackState();

        assertDoesNotThrow(() -> handler.handle(downloading(""), holder, ServerMockFixture.server));

        verify(holder).disconnect(DisconnectFailReason.UNEXPECTED_PACKET);
    }

    @Test
    void packIdWithSeparatorIsAccepted() {
        PlayerSessionHolder holder = holderInResourcePackState();

        assertDoesNotThrow(() -> handler.handle(
                downloading("11111111-1111-1111-1111-111111111111_1.0.0"),
                holder,
                ServerMockFixture.server
        ));

        verify(holder, never()).disconnect(DisconnectFailReason.UNEXPECTED_PACKET);
    }
}
