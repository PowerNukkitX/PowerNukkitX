package org.powernukkitx.network.process.auth;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.data.payload.skin.AnimatedImageData;
import org.cloudburstmc.protocol.bedrock.data.payload.skin.AnimatedTextureType;
import org.cloudburstmc.protocol.bedrock.data.payload.skin.AnimationExpression;
import org.cloudburstmc.protocol.bedrock.data.payload.skin.ArmSizeType;
import org.cloudburstmc.protocol.bedrock.data.payload.skin.PieceType;
import org.cloudburstmc.protocol.bedrock.data.payload.skin.SerializedPersonaPieceHandle;
import org.cloudburstmc.protocol.bedrock.data.payload.skin.SerializedSkin;
import org.cloudburstmc.protocol.bedrock.data.payload.skin.SkinImage;
import org.cloudburstmc.protocol.bedrock.data.payload.skin.TintMapColor;
import org.jetbrains.annotations.Nullable;
import org.jose4j.jwt.JwtClaims;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Kaooot
 */
@UtilityClass
public class ClientSkinData {

    private final Base64.Decoder DECODER = Base64.getDecoder();

    public SerializedSkin readSkin(JwtClaims claims) {
        final Map<String, Object> map = claims.getClaimsMap();
        if (map.isEmpty()) {
            return null;
        }
        final SerializedSkin serialized = new SerializedSkin();
        try {
            if (map.containsKey("AnimatedImageData")) {
                final List<? extends Map<?, ?>> animatedImageData = readMapList(map, "AnimatedImageData");
                if (animatedImageData == null) {
                    return null;
                }
                for (Map<?, ?> animationDataObject : animatedImageData) {
                    final SkinImage imageData = readImageData(animationDataObject);
                    if (imageData == null) {
                        return null;
                    }
                    if (!(animationDataObject.get("AnimationExpression") instanceof Number animationExpression)) {
                        return null;
                    }
                    if (!(animationDataObject.get("Type") instanceof Number type)) {
                        return null;
                    }
                    if (!(animationDataObject.get("Frames") instanceof Number frames)) {
                        return null;
                    }
                    final AnimatedImageData animation = new AnimatedImageData();
                    animation.setSkinImage(imageData);
                    animation.setAnimatedTextureType(AnimatedTextureType.from(type.intValue()));
                    animation.setFrames(frames.floatValue());
                    animation.setAnimationExpression(AnimationExpression.from(animationExpression.intValue()));
                    serialized.getAnimatedImageData().add(animation);
                }
            }
            if (map.containsKey("ArmSize")) {
                final String armSize = map.get("ArmSize").toString();
                serialized.setArmSize("slim".equalsIgnoreCase(armSize) ? ArmSizeType.SLIM : ArmSizeType.WIDE);
            }
            if (map.containsKey("CapeData")) {
                final Map<String, Object> capeImageDataMap = new Object2ObjectOpenHashMap<>();
                capeImageDataMap.put("Image", map.get("CapeData"));
                capeImageDataMap.put("ImageWidth", map.get("CapeImageWidth"));
                capeImageDataMap.put("ImageHeight", map.get("CapeImageHeight"));
                final SkinImage imageData = readImageData(capeImageDataMap);
                if (imageData == null) {
                    return null;
                }
                serialized.setCapeImageData(imageData);
            }
            if (map.containsKey("CapeId")) {
                if (!(map.get("CapeId") instanceof String capeId)) {
                    return null;
                }
                serialized.setCapeID(capeId);
            }
            if (map.containsKey("CapeOnClassicSkin")) {
                if (!(map.get("CapeOnClassicSkin") instanceof Boolean capeOnClassicSkin)) {
                    return null;
                }
                serialized.setPersonaCapeOnClassicSkin(capeOnClassicSkin);
            }
            if (map.containsKey("OverrideSkin")) {
                if (!(map.get("OverrideSkin") instanceof Boolean overrideSkin)) {
                    return null;
                }
                serialized.setOverridesPlayerAppearance(overrideSkin);
            }
            if (map.containsKey("PersonaPieces")) {
                final List<? extends Map<?, ?>> personaPieces = readMapList(map, "PersonaPieces");
                if (personaPieces == null) {
                    return null;
                }
                for (Map<?, ?> personaPiece : personaPieces) {
                    final SerializedPersonaPieceHandle handle = new SerializedPersonaPieceHandle();
                    handle.setPieceId(personaPiece.get("PieceId").toString());
                    handle.setPieceType(PieceType.from(personaPiece.get("PieceType").toString()));
                    handle.setPackId(parseUuid(personaPiece.get("PackId").toString()));
                    handle.setDefaultPiece(Boolean.parseBoolean(personaPiece.get("IsDefault").toString()));
                    handle.setProductId(personaPiece.get("ProductId").toString());
                    serialized.getPersonaPieces().add(handle);
                }
            }
            if (map.containsKey("PersonaSkin")) {
                if (!(map.get("PersonaSkin") instanceof Boolean personaSkin)) {
                    return null;
                }
                serialized.setPersona(personaSkin);
            }
            if (map.containsKey("PieceTintColors")) {
                final List<? extends Map<?, ?>> pieceTintColors = readMapList(map, "PieceTintColors");
                if (pieceTintColors == null) {
                    return null;
                }
                for (Map<?, ?> pieceTintColor : pieceTintColors) {
                    final TintMapColor color = new TintMapColor();
                    final Object colorsValue = pieceTintColor.get("Colors");
                    if (!(colorsValue instanceof List<?> colorsList)) {
                        return null;
                    }
                    for (Object element : colorsList) {
                        if (!(element instanceof String value)) {
                            return null;
                        }
                        color.getColors().add(parseColor(value));
                    }
                    serialized.getPieceTintColors().put(PieceType.from(pieceTintColor.get("PieceType").toString()), color);
                }
            }
            if (map.containsKey("PremiumSkin")) {
                if (!(map.get("PremiumSkin") instanceof Boolean premiumSkin)) {
                    return null;
                }
                serialized.setPremium(premiumSkin);
            }
            if (map.containsKey("SkinAnimationData")) {
                serialized.setAnimationData(new String(DECODER.decode(map.get("SkinAnimationData").toString())));
            }
            if (map.containsKey("SkinColor")) {
                serialized.setSkinColor(parseColor(map.get("SkinColor").toString()));
            }
            if (map.containsKey("SkinData")) {
                final Map<String, Object> skinImageDataMap = new Object2ObjectOpenHashMap<>();
                skinImageDataMap.put("Image", map.get("SkinData"));
                skinImageDataMap.put("ImageWidth", map.get("SkinImageWidth"));
                skinImageDataMap.put("ImageHeight", map.get("SkinImageHeight"));
                final SkinImage imageData = readImageData(skinImageDataMap);
                if (imageData == null) {
                    return null;
                }
                serialized.setImageData(imageData);
            }
            if (map.containsKey("SkinGeometryData")) {
                if (!(map.get("SkinGeometryData") instanceof String skinGeometryData)) {
                    return null;
                }
                serialized.setGeometryData(new String(DECODER.decode(skinGeometryData)));
            }
            if (map.containsKey("SkinGeometryDataEngineVersion")) {
                if (!(map.get("SkinGeometryDataEngineVersion") instanceof String skinGeometryDataEngineVersion)) {
                    return null;
                }
                serialized.setGeometryDataMinEngineVersion(new String(DECODER.decode(skinGeometryDataEngineVersion)));
            }
            if (map.containsKey("SkinId")) {
                serialized.setID(map.get("SkinId").toString());
            }
            if (map.containsKey("SkinResourcePatch")) {
                if (!(map.get("SkinResourcePatch") instanceof String skinResourcePatch)) {
                    return null;
                }
                serialized.setResourcePatch(new String(DECODER.decode(skinResourcePatch)));
            }
        } catch (Exception e) {
            return null;
        }
        return serialized;
    }

    /**
     * Reads a claim that is expected to hold a list of JSON objects. Returns {@code null} when the claim is missing,
     * not a list, or contains an element that is not a map, so callers can reject the whole skin.
     */
    private @Nullable List<? extends Map<?, ?>> readMapList(Map<String, Object> map, String key) {
        final Object value = map.get(key);
        if (!(value instanceof List<?> list)) {
            return null;
        }
        final List<Map<?, ?>> result = new ObjectArrayList<>();
        for (Object element : list) {
            if (!(element instanceof Map<?, ?> mapElement)) {
                return null;
            }
            result.add(mapElement);
        }
        return result;
    }

    private SkinImage readImageData(Map<?, ?> map) {
        if (!(map.get("ImageWidth") instanceof Number imageWidth)) {
            return null;
        }
        if (!(map.get("ImageHeight") instanceof Number imageHeight)) {
            return null;
        }
        if (!(map.get("Image") instanceof String imageBase64)) {
            return null;
        }
        try {
            if (imageBase64.isEmpty() && imageWidth.intValue() == 0 && imageHeight.intValue() == 0) {
                return new SkinImage(0, 0, new byte[0]);
            }
            final byte[] decoded = DECODER.decode(imageBase64);
            return new SkinImage(imageWidth.intValue(), imageHeight.intValue(), decoded);
        } catch (Exception e) {
            return null;
        }
    }

    private int parseColor(String color) {
        if (color == null || color.isEmpty()) {
            return 0;
        }
        final String hex = color.startsWith("#") ? color.substring(1) : color;
        try {
            return (int) Long.parseLong(hex, 16);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return new UUID(0, 0);
        }
    }
}
