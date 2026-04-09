package cn.nukkit.network.process.auth;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimatedTextureType;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimationData;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimationExpressionType;
import org.cloudburstmc.protocol.bedrock.data.skin.ImageData;
import org.cloudburstmc.protocol.bedrock.data.skin.PersonaPieceData;
import org.cloudburstmc.protocol.bedrock.data.skin.PersonaPieceTintData;
import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;
import org.jose4j.jwt.JwtClaims;

import java.util.Base64;
import java.util.List;
import java.util.Map;

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
        final SerializedSkin.Builder builder = SerializedSkin.builder();
        try {
            if (map.containsKey("AnimatedImageData")) {
                final List<AnimationData> animations = new ObjectArrayList<>();
                final List<Map<String, Object>> animatedImageData = (List<Map<String, Object>>) map.get("AnimatedImageData");

                for (Map<String, Object> animationDataObject : animatedImageData) {
                    final ImageData imageData = readImageData(animationDataObject);
                    if (imageData == null) {
                        return null;
                    }
                    if (!animationDataObject.containsKey("AnimationExpression") || !(animationDataObject.get("AnimationExpression") instanceof Number animationExpression)) {
                        return null;
                    }
                    if (!animationDataObject.containsKey("Type") || !(animationDataObject.get("Type") instanceof Number type)) {
                        return null;
                    }
                    if (!animationDataObject.containsKey("Frames") || !(animationDataObject.get("Frames") instanceof Number frames)) {
                        return null;
                    }
                    animations.add(new AnimationData(
                                    imageData,
                                    AnimatedTextureType.from(type.intValue()),
                                    frames.floatValue(),
                                    AnimationExpressionType.from(animationExpression.intValue())
                            )
                    );
                }
                builder.animations(animations);
            }
            if (map.containsKey("ArmSize")) {
                builder.armSize(map.get("ArmSize").toString());
            }
            if (map.containsKey("CapeData")) {
                final Map<String, Object> capeImageDataMap = new Object2ObjectOpenHashMap<>();
                capeImageDataMap.put("Image", map.get("CapeData"));
                capeImageDataMap.put("ImageWidth", map.get("CapeImageWidth"));
                capeImageDataMap.put("ImageHeight", map.get("CapeImageHeight"));
                final ImageData imageData = readImageData(capeImageDataMap);
                if (imageData == null) {
                    return null;
                }
                builder.capeData(imageData);
            }
            if (map.containsKey("CapeId")) {
                if (!(map.get("CapeId") instanceof String capeId)) {
                    return null;
                }
                builder.capeId(capeId);
            }
            if (map.containsKey("CapeOnClassicSkin")) {
                if (!(map.get("CapeOnClassicSkin") instanceof Boolean capeOnClassicSkin)) {
                    return null;
                }
                builder.capeOnClassic(capeOnClassicSkin);
            }
            if (map.containsKey("OverrideSkin")) {
                if (!(map.get("OverrideSkin") instanceof Boolean overrideSkin)) {
                    return null;
                }
                builder.overridingPlayerAppearance(overrideSkin);
            }
            if (map.containsKey("PersonaPieces")) {
                final List<Map<String, Object>> personaPieces = (List<Map<String, Object>>) map.get("PersonaPieces");
                final List<PersonaPieceData> personaPieceDataList = new ObjectArrayList<>();
                for (Map<String, Object> personaPiece : personaPieces) {
                    personaPieceDataList.add(
                            new PersonaPieceData(
                                    personaPiece.get("PieceId").toString(),
                                    personaPiece.get("PieceType").toString(),
                                    personaPiece.get("PackId").toString(),
                                    Boolean.parseBoolean(personaPiece.get("IsDefault").toString()),
                                    personaPiece.get("ProductId").toString()
                            )
                    );
                }
                builder.personaPieces(personaPieceDataList);
            }
            if (map.containsKey("PersonaSkin")) {
                if (!(map.get("PersonaSkin") instanceof Boolean personaSkin)) {
                    return null;
                }
                builder.persona(personaSkin);
            }
            if (map.containsKey("PieceTintColors")) {
                final List<Map<String, Object>> pieceTintColors = (List<Map<String, Object>>) map.get("PieceTintColors");
                final List<PersonaPieceTintData> tintColors = new ObjectArrayList<>();
                for (Map<String, Object> pieceTintColor : pieceTintColors) {
                    tintColors.add(
                            new PersonaPieceTintData(
                                    pieceTintColor.get("PieceType").toString(),
                                    (List<String>) pieceTintColor.get("Colors")
                            )
                    );
                }
                builder.tintColors(tintColors);
            }
            if (map.containsKey("PremiumSkin")) {
                if (!(map.get("PremiumSkin") instanceof Boolean premiumSkin)) {
                    return null;
                }
                builder.premium(premiumSkin);
            }
            if (map.containsKey("SkinAnimationData")) {
                builder.animationData(new String(DECODER.decode(map.get("SkinAnimationData").toString())));
            }
            if (map.containsKey("SkinColor")) {
                builder.skinColor(map.get("SkinColor").toString());
            }
            if (map.containsKey("SkinData")) {
                final Map<String, Object> skinImageDataMap = new Object2ObjectOpenHashMap<>();
                skinImageDataMap.put("Image", map.get("SkinData"));
                skinImageDataMap.put("ImageWidth", map.get("SkinImageWidth"));
                skinImageDataMap.put("ImageHeight", map.get("SkinImageHeight"));
                final ImageData imageData = readImageData(skinImageDataMap);
                if (imageData == null) {
                    return null;
                }
                builder.skinData(imageData);
            }
            if (map.containsKey("SkinGeometryData")) {
                if (!(map.get("SkinGeometryData") instanceof String skinGeometryData)) {
                    return null;
                }
                builder.geometryData(new String(DECODER.decode(skinGeometryData)));
            }
            if (map.containsKey("SkinGeometryDataEngineVersion")) {
                if (!(map.get("SkinGeometryDataEngineVersion") instanceof String skinGeometryDataEngineVersion)) {
                    return null;
                }
                builder.geometryDataEngineVersion(new String(DECODER.decode(skinGeometryDataEngineVersion)));
            }
            if (map.containsKey("SkinId")) {
                builder.skinId(map.get("SkinId").toString());
            }
            if (map.containsKey("SkinResourcePatch")) {
                if (!(map.get("SkinResourcePatch") instanceof String skinResourcePatch)) {
                    return null;
                }
                builder.skinResourcePatch(new String(DECODER.decode(skinResourcePatch)));
            }
        } catch (Exception e) {
            return null;
        }
        return builder.build();
    }

    private ImageData readImageData(Map<String, Object> map) {
        if (!map.containsKey("ImageWidth") || !(map.get("ImageWidth") instanceof Number imageWidth)) {
            return null;
        }
        if (!map.containsKey("ImageHeight") || !(map.get("ImageHeight") instanceof Number imageHeight)) {
            return null;
        }
        if (!map.containsKey("Image") || !(map.get("Image") instanceof String imageBase64)) {
            return null;
        }
        try {
            if (imageBase64.isEmpty() && imageWidth.intValue() == 0 && imageHeight.intValue() == 0) {
                return ImageData.EMPTY;
            }
            final byte[] decoded = DECODER.decode(imageBase64);
            // TODO protocol check image size
            return ImageData.of(
                    imageWidth.intValue(),
                    imageHeight.intValue(),
                    decoded
            );
        } catch (Exception e) {
            return null;
        }
    }
}