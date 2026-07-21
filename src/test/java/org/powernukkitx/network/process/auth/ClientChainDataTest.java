package org.powernukkitx.network.process.auth;

import org.jose4j.jwt.JwtClaims;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class ClientChainDataTest {

    @Test
    void returnsNullWhenClaimsAreEmpty() {
        assertNull(ClientChainData.from(new JwtClaims()));
    }

    @Test
    void returnsNullWhenClientRandomIdHasWrongType() {
        JwtClaims claims = new JwtClaims();
        claims.setClaim("ClientRandomId", "not-a-number");

        assertNull(ClientChainData.from(claims));
    }

    @Test
    void returnsNullWhenInputModeOrdinalIsInvalid() {
        JwtClaims claims = new JwtClaims();
        claims.setClaim("ClientRandomId", 123L);
        claims.setClaim("CompatibleWithClientSideChunkGen", false);
        claims.setClaim("CurrentInputMode", -1);

        assertNull(ClientChainData.from(claims));
    }
}
