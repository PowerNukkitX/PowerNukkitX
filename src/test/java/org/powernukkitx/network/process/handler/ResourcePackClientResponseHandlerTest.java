package org.powernukkitx.network.process.handler;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
}
