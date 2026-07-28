package org.powernukkitx.network.process.auth;

import org.cloudburstmc.protocol.bedrock.data.skin.Skin;
import org.jose4j.jwt.JwtClaims;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void acceptsPersonaPieceWithBooleanIsDefault() {
        Skin skin = ClientSkinData.readSkin(claimsWithPersonaPiece(personaPiece(true)));

        assertNotNull(skin);
        assertEquals(1, skin.getPersonaPieces().size());
        assertTrue(skin.getPersonaPieces().getFirst().isDefault());
    }

    @Test
    void acceptsPersonaPieceWithQuotedIsDefault() {
        Skin skin = ClientSkinData.readSkin(claimsWithPersonaPiece(personaPiece("true")));

        assertNotNull(skin);
        assertTrue(skin.getPersonaPieces().getFirst().isDefault());
    }

    @Test
    void returnsNullWhenIsDefaultIsNotABoolean() {
        assertNull(ClientSkinData.readSkin(claimsWithPersonaPiece(personaPiece(1))));
    }

    @Test
    void returnsNullWhenPersonaPieceIdIsNotAString() {
        Map<String, Object> piece = new HashMap<>(personaPiece(true));
        piece.put("PieceId", 1234);

        assertNull(ClientSkinData.readSkin(claimsWithPersonaPiece(piece)));
    }

    private static JwtClaims claimsWithPersonaPiece(Map<String, Object> piece) {
        JwtClaims claims = new JwtClaims();
        // the builder drops persona pieces unless a resource patch is present
        claims.setClaim("SkinResourcePatch", Base64.getEncoder().encodeToString(
                "{\"geometry\":{\"default\":\"geometry.humanoid\"}}".getBytes(StandardCharsets.UTF_8)
        ));
        claims.setClaim("PersonaPieces", List.of(piece));
        return claims;
    }

    private static Map<String, Object> personaPiece(Object isDefault) {
        return Map.of(
                "PieceId", "piece-id",
                "PieceType", "body",
                "PackId", "pack-id",
                "IsDefault", isDefault,
                "ProductId", "product-id"
        );
    }
}
