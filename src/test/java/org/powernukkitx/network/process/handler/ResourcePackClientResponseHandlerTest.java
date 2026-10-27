package org.powernukkitx.network.process.handler;

import org.cloudburstmc.protocol.bedrock.data.DisconnectFailReason;
import org.cloudburstmc.protocol.bedrock.data.ResourcePackResponse;
import org.cloudburstmc.protocol.bedrock.packet.ResourcePackClientResponsePacket;
import org.junit.jupiter.api.Test;
import org.powernukkitx.Server;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.powernukkitx.network.process.SessionState;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * The entries of the client's downloading list are strings it chose, read while the session is
 * still logging in, so an entry that does not hold a pack id has to be reported rather than thrown.
 */
class ResourcePackClientResponseHandlerTest {

    private static final UUID PACK = UUID.fromString("e8a1f1d4-7f1a-4b2c-9c3d-0a1b2c3d4e5f");

    @Test
    void readsABarePackId() {
        assertEquals(PACK, ResourcePackClientResponseHandler.parsePackId(PACK.toString()));
    }

    @Test
    void readsAPackIdFollowedByItsVersion() {
        assertEquals(PACK, ResourcePackClientResponseHandler.parsePackId(PACK + "_1.0.0"));
        assertEquals(PACK, ResourcePackClientResponseHandler.parsePackId(PACK + "_"));
        assertEquals(PACK, ResourcePackClientResponseHandler.parsePackId(PACK + "_1_2_3"));
    }

    @Test
    void reportsAnEntryThatHoldsNoPackId() {
        assertNull(ResourcePackClientResponseHandler.parsePackId(null));
        assertNull(ResourcePackClientResponseHandler.parsePackId(""));
        assertNull(ResourcePackClientResponseHandler.parsePackId("_" + PACK));
        assertNull(ResourcePackClientResponseHandler.parsePackId("not a uuid"));
        assertNull(ResourcePackClientResponseHandler.parsePackId("../../etc/passwd"));
        assertNull(ResourcePackClientResponseHandler.parsePackId("e8a1f1d4-7f1a-4b2c-9c3d"));
        assertNull(ResourcePackClientResponseHandler.parsePackId("zzzzzzzz-zzzz-zzzz-zzzz-zzzzzzzzzzzz"));
    }

    @Test
    void disconnectsOnADownloadingEntryThatHoldsNoPackId() {
        final PlayerSessionHolder holder = mock(PlayerSessionHolder.class);
        when(holder.getState()).thenReturn(SessionState.RESOURCE_PACK);
        final Server server = mock(Server.class);

        final ResourcePackClientResponsePacket packet = new ResourcePackClientResponsePacket();
        packet.setResponse(ResourcePackResponse.DOWNLOADING);
        packet.getDownloadingPacks().add("../../etc/passwd");

        new ResourcePackClientResponseHandler().handle(packet, holder, server);

        verify(holder).disconnect(DisconnectFailReason.RESOURCE_PACK_LOADING_FAILED);
        // the malformed entry is rejected before the pack manager is ever asked for an id
        verifyNoInteractions(server);
    }
}
