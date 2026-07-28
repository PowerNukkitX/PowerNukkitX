package org.powernukkitx.network.process.auth;

import org.jose4j.jwt.JwtClaims;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientChainDataTest {

    @Test
    void returnsNullWhenClaimsAreEmpty() {
        assertNull(ClientChainData.from(new JwtClaims()));
    }

    @Test
    void parsesFullyPopulatedClaims() {
        assertNotNull(ClientChainData.from(validClaims()));
    }

    @Test
    void returnsNullWhenClientRandomIdHasWrongType() {
        JwtClaims claims = validClaims();
        claims.setClaim("ClientRandomId", "not-a-number");

        assertNull(ClientChainData.from(claims));
    }

    @Test
    void acceptsClientRandomIdSentAsInteger() {
        JwtClaims claims = validClaims();
        claims.setClaim("ClientRandomId", 42);

        ClientChainData data = ClientChainData.from(claims);

        assertNotNull(data);
        assertEquals(42L, data.getClientRandomId());
    }

    @Test
    void returnsNullWhenInputModeOrdinalIsInvalid() {
        JwtClaims claims = validClaims();
        claims.setClaim("CurrentInputMode", -1);

        assertNull(ClientChainData.from(claims));
    }

    @Test
    void defaultsClientEditorConnectionIntentWhenAbsent() {
        JwtClaims claims = validClaims();
        claims.unsetClaim("ClientEditorConnectionIntent");

        ClientChainData data = ClientChainData.from(claims);

        assertNotNull(data);
        assertEquals(0, data.getClientEditorConnectionIntent());
    }

    @Test
    void returnsNullWhenClientEditorConnectionIntentHasWrongType() {
        JwtClaims claims = validClaims();
        claims.setClaim("ClientEditorConnectionIntent", "editor");

        assertNull(ClientChainData.from(claims));
    }

    @Test
    void exposesRawClaims() {
        ClientChainData data = ClientChainData.from(validClaims());

        assertNotNull(data);
        assertEquals("en_US", data.getRawClaims().get("LanguageCode"));
    }

    private static JwtClaims validClaims() {
        JwtClaims claims = new JwtClaims();
        claims.setClaim("ClientRandomId", 123L);
        claims.setClaim("CompatibleWithClientSideChunkGen", false);
        claims.setClaim("CurrentInputMode", 0);
        claims.setClaim("DefaultInputMode", 0);
        claims.setClaim("DeviceId", "device-id");
        claims.setClaim("DeviceModel", "device-model");
        claims.setClaim("DeviceOS", 0);
        claims.setClaim("GameVersion", "1.21.0");
        claims.setClaim("GraphicsMode", 0);
        claims.setClaim("GuiScale", 0);
        claims.setClaim("ClientIsEditorCapable", false);
        claims.setClaim("LanguageCode", "en_US");
        claims.setClaim("MaxViewDistance", 16);
        claims.setClaim("MemoryTier", 0);
        claims.setClaim("PlatformOfflineId", "");
        claims.setClaim("PlatformOnlineId", "");
        claims.setClaim("PlatformType", 0);
        claims.setClaim("SelfSignedId", UUID.randomUUID().toString());
        claims.setClaim("ServerAddress", "127.0.0.1:19132");
        claims.setClaim("ThirdPartyName", "Steve");
        claims.setClaim("TrustedSkin", true);
        claims.setClaim("UIProfile", 0);
        claims.setClaim("ClientEditorConnectionIntent", 0);
        return claims;
    }
}
