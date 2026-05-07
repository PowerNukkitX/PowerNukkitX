package cn.nukkit.network.process.login;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.connection.util.ChainValidationResult;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.utils.EncryptionUtils;
import cn.nukkit.utils.SkinUtils;
import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

import java.nio.charset.StandardCharsets;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;
import java.util.UUID;

public record LoginData(ECPublicKey identityPublicKey, String rawIdentityPublicKey, JsonObject clientData, String xuid, UUID uuid, String displayName, boolean xboxAuthed, boolean isChainPayload, ChainValidationResult.IdentityData identityData) {
    private static final int MAX_RESOURCE_PATCH_LENGTH = 4 * 1024;
    private static final int MAX_GEOMETRY_BASE64_LENGTH = 2 * 1024 * 1024;
    private static final int MAX_ANIMATION_BASE64_LENGTH = 1024 * 1024;
    private static final int MAX_PERSONA_PIECES = 128;
    private static final int MAX_TINT_COLORS = 128;
    private static final int MAX_ANIMATIONS = 32;

    public static LoginData processHandshake(LoginPacket packet, boolean strict) throws Exception {
        ChainValidationResult result = EncryptionUtils.validatePayload(packet.authPayload);
        boolean xboxAuth = result.signed();
        ChainValidationResult.IdentityClaims identityClaims = result.identityClaims();
        ChainValidationResult.IdentityData identityData = identityClaims.extraData;
        ECPublicKey identityPublicKey = (ECPublicKey) identityClaims.parsedIdentityPublicKey();
        String xuid = identityData.xuid;
        UUID uuid = UUID.nameUUIDFromBytes(("pocket-auth-1-xuid:" + xuid).getBytes(StandardCharsets.UTF_8));

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setVerificationKey(identityPublicKey)
                .setJwsAlgorithmConstraints(new AlgorithmConstraints(
                        AlgorithmConstraints.ConstraintType.PERMIT,
                        AlgorithmIdentifiers.ECDSA_USING_P384_CURVE_AND_SHA384))
                .build();

        JwtClaims clientClaims;
        try {
            clientClaims = jwtConsumer.processToClaims(packet.clientPayload);
        } catch (InvalidJwtException e) {
            throw new SecurityException("Invalid clientData JWT", e);
        }
        JsonObject clientData = parseClientData(clientClaims, xuid);
        String displayName = identityData.displayName;

        return new LoginData(identityPublicKey, identityClaims.identityPublicKey, clientData, xuid, uuid, displayName, xboxAuth,
                packet.getAuthPayload() instanceof CertificateChainPayload, identityData);
    }

    public static JsonObject parseClientData(JwtClaims claims, String xuid) throws Exception {
        JsonObject clientData = (JsonObject) JsonParser.parseString(claims.toJson());
        return clientData;
    }

    public Skin skin() {
        JsonObject skinToken = clientData;
        Skin skin = new Skin();

        if (skinToken.has("PlayFabId"))
            skin.setPlayFabId(skinToken.get("PlayFabId").getAsString());

        if (skinToken.has("CapeId"))
            skin.setCapeId(skinToken.get("CapeId").getAsString());

        if (skinToken.has("SkinId")) {
            //The "SkinId" obtained here is FullId
            //FullId = SkinId + CapeId
            //The skinId in the Skin object is not FullId, we need to subtract the CapeId

            var fullSkinId = skinToken.get("SkinId").getAsString();
            skin.setFullSkinId(fullSkinId);
            if (skin.getCapeId() != null)
                skin.setSkinId(fullSkinId.substring(0, fullSkinId.length() - skin.getCapeId().length()));
            else
                skin.setSkinId(fullSkinId);
        }

        skin.setSkinData(SkinUtils.getImage(skinToken, "Skin"));
        skin.setCapeData(SkinUtils.getImage(skinToken, "Cape"));

        if (skinToken.has("PremiumSkin"))
            skin.setPremium(skinToken.get("PremiumSkin").getAsBoolean());

        if (skinToken.has("PersonaSkin"))
            skin.setPersona(skinToken.get("PersonaSkin").getAsBoolean());

        if (skinToken.has("CapeOnClassicSkin"))
            skin.setCapeOnClassic(skinToken.get("CapeOnClassicSkin").getAsBoolean());

        if (skinToken.has("SkinResourcePatch")) {
            String resourcePatch = skinToken.get("SkinResourcePatch").getAsString();
            Preconditions.checkArgument(resourcePatch.length() <= MAX_RESOURCE_PATCH_LENGTH, "SkinResourcePatch base64 too large: %s", resourcePatch.length());
            skin.setSkinResourcePatch(new String(Base64.getDecoder().decode(resourcePatch), StandardCharsets.UTF_8));
        }

        if (skinToken.has("SkinGeometryData")) {
            String geometry = skinToken.get("SkinGeometryData").getAsString();
            Preconditions.checkArgument(geometry.length() <= MAX_GEOMETRY_BASE64_LENGTH, "SkinGeometryData base64 too large: %s", geometry.length());
            skin.setGeometryData(new String(Base64.getDecoder().decode(geometry), StandardCharsets.UTF_8));
        }

        if (skinToken.has("SkinAnimationData")) {
            String animationData = skinToken.get("SkinAnimationData").getAsString();
            Preconditions.checkArgument(animationData.length() <= MAX_ANIMATION_BASE64_LENGTH, "SkinAnimationData base64 too large: %s", animationData.length());
            skin.setAnimationData(new String(Base64.getDecoder().decode(animationData), StandardCharsets.UTF_8));
        }

        if (skinToken.has("AnimatedImageData")) {
            var array = skinToken.get("AnimatedImageData").getAsJsonArray();
            Preconditions.checkArgument(array.size() <= MAX_ANIMATIONS, "AnimatedImageData array too large: %s", array.size());
            for (JsonElement element : array) {
                skin.getAnimations().add(SkinUtils.getAnimation(element.getAsJsonObject()));
            }
        }

        if (skinToken.has("SkinColor"))
            skin.setSkinColor(skinToken.get("SkinColor").getAsString());

        if (skinToken.has("ArmSize"))
            skin.setArmSize(skinToken.get("ArmSize").getAsString());

        if (skinToken.has("PersonaPieces")) {
            var array = skinToken.get("PersonaPieces").getAsJsonArray();
            Preconditions.checkArgument(array.size() <= MAX_PERSONA_PIECES, "PersonaPieces array too large: %s", array.size());
            for (JsonElement object : array) {
                skin.getPersonaPieces().add(SkinUtils.getPersonaPiece(object.getAsJsonObject()));
            }
        }

        if (skinToken.has("PieceTintColors")) {
            var array = skinToken.get("PieceTintColors").getAsJsonArray();
            Preconditions.checkArgument(array.size() <= MAX_TINT_COLORS, "PieceTintColors array too large: %s", array.size());
            for (JsonElement object : array) {
                skin.getTintColors().add(SkinUtils.getTint(object.getAsJsonObject()));
            }
        }

        return skin;
    }
}
