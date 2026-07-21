package org.powernukkitx.network.process.auth;

import org.jose4j.jwt.JwtClaims;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientSkinDataTest {

    @Test
    void returnsNullWhenAnimatedImageDataIsNotAListOfMaps() {
        JwtClaims claims = new JwtClaims();
        claims.setClaim("AnimatedImageData", List.of("not-a-map"));

        assertNull(ClientSkinData.readSkin(claims));
    }

    @Test
    void returnsNullWhenPersonaPieceIsMissingRequiredValues() {
        JwtClaims claims = new JwtClaims();
        claims.setClaim("PersonaPieces", List.of(Map.of("PieceId", "piece-id")));

        assertNull(ClientSkinData.readSkin(claims));
    }

    @Test
    void returnsNullWhenTintColorsAreNotStrings() {
        JwtClaims claims = new JwtClaims();
        claims.setClaim("PieceTintColors", List.of(Map.of(
                "PieceType", "body",
                "Colors", List.of(1, 2, 3)
        )));

        assertNull(ClientSkinData.readSkin(claims));
    }

    @Test
    void acceptsValidTintColorPayload() {
        JwtClaims claims = new JwtClaims();
        claims.setClaim("PieceTintColors", List.of(Map.of(
                "PieceType", "body",
                "Colors", List.of("#111111", "#222222")
        )));

        assertNotNull(ClientSkinData.readSkin(claims));
    }
}
