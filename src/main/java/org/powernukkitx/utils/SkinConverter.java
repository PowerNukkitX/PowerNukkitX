package org.powernukkitx.utils;

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
import org.cloudburstmc.protocol.bedrock.data.payload.skin.TrustedSkinFlag;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimationData;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimationExpressionType;
import org.cloudburstmc.protocol.bedrock.data.skin.ImageData;
import org.cloudburstmc.protocol.bedrock.data.skin.PersonaPieceData;
import org.cloudburstmc.protocol.bedrock.data.skin.PersonaPieceTintData;
import org.cloudburstmc.protocol.bedrock.data.skin.Skin;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class SkinConverter {

    private final String PERSONA_PREFIX = "persona_";
    private final String EMPTY_GEOMETRY_DATA = "{}";
    private final int TINT_COLOR_COUNT = 4;

    public SerializedSkin toSerializedSkin(Skin skin, boolean trusted) {
        final SerializedSkin serialized = new SerializedSkin();
        serialized.setID(orEmpty(skin.getSkinId()));
        serialized.setPlayFabID(orEmpty(skin.getPlayFabId()));
        serialized.setResourcePatch(orEmpty(skin.getSkinResourcePatch()));
        serialized.setImageData(toSkinImage(skin.getSkinData()));
        serialized.setCapeImageData(toSkinImage(skin.getCapeData()));
        serialized.setGeometryData(geometryDataOrEmptyObject(skin.getGeometryData()));
        serialized.setGeometryDataMinEngineVersion(orDefault(skin.getGeometryDataEngineVersion(), "0.0.0"));
        serialized.setAnimationData(orEmpty(skin.getAnimationData()));
        serialized.setCapeID(orEmpty(skin.getCapeId()));
        serialized.setFullID(orEmpty(skin.getFullSkinId()));
        serialized.setArmSize("slim".equalsIgnoreCase(skin.getArmSize()) ? ArmSizeType.SLIM : ArmSizeType.WIDE);
        serialized.setSkinColor(parseColor(skin.getSkinColor()));
        serialized.setPremium(skin.isPremium());
        serialized.setPersona(skin.isPersona());
        serialized.setPersonaCapeOnClassicSkin(skin.isCapeOnClassic());
        serialized.setPrimaryUser(skin.isPrimaryUser());
        serialized.setOverridesPlayerAppearance(skin.isOverridingPlayerAppearance());
        serialized.setTrustedSkinFlag(trusted ? TrustedSkinFlag.TRUE : TrustedSkinFlag.FALSE);
        serialized.setProfileHash("");

        if (skin.getAnimations() != null) {
            for (AnimationData animation : skin.getAnimations()) {
                final AnimatedImageData imageData = new AnimatedImageData();
                imageData.setSkinImage(toSkinImage(animation.getImage()));
                imageData.setAnimatedTextureType(toPayloadTextureType(animation.getTextureType()));
                imageData.setFrames(animation.getFrames());
                imageData.setAnimationExpression(animation.getExpressionType() == AnimationExpressionType.BLINKING
                        ? AnimationExpression.BLINKING : AnimationExpression.LINEAR);
                serialized.getAnimatedImageData().add(imageData);
            }
        }

        if (skin.getPersonaPieces() != null) {
            for (PersonaPieceData piece : skin.getPersonaPieces()) {
                final PieceType pieceType = toPieceType(piece.getType());
                if (pieceType == null) {
                    continue;
                }
                final SerializedPersonaPieceHandle handle = new SerializedPersonaPieceHandle();
                handle.setPieceId(orEmpty(piece.getId()));
                handle.setPieceType(pieceType);
                handle.setPackId(parseUuid(piece.getPackId()));
                handle.setDefaultPiece(piece.isDefault());
                handle.setProductId(orEmpty(piece.getProductId()));
                serialized.getPersonaPieces().add(handle);
            }
        }

        if (skin.getTintColors() != null) {
            for (PersonaPieceTintData tint : skin.getTintColors()) {
                final PieceType pieceType = toPieceType(tint.getType());
                if (pieceType == null) {
                    continue;
                }
                final TintMapColor color = new TintMapColor();
                final List<String> colors = tint.getColors();
                for (int i = 0; i < TINT_COLOR_COUNT; i++) {
                    color.getColors().add(colors != null && i < colors.size() ? parseColor(colors.get(i)) : 0);
                }
                serialized.getPieceTintColors().put(pieceType, color);
            }
        }
        return serialized;
    }

    public Skin fromSerializedSkin(SerializedSkin serialized) {
        final List<AnimationData> animations = new ObjectArrayList<>();
        for (AnimatedImageData imageData : serialized.getAnimatedImageData()) {
            animations.add(new AnimationData(
                    toImageData(imageData.getSkinImage()),
                    fromPayloadTextureType(imageData.getAnimatedTextureType()),
                    imageData.getFrames(),
                    imageData.getAnimationExpression() == AnimationExpression.BLINKING
                            ? AnimationExpressionType.BLINKING : AnimationExpressionType.LINEAR
            ));
        }

        final List<PersonaPieceData> personaPieces = new ObjectArrayList<>();
        for (SerializedPersonaPieceHandle handle : serialized.getPersonaPieces()) {
            personaPieces.add(new PersonaPieceData(
                    handle.getPieceId(),
                    PERSONA_PREFIX + handle.getPieceType().getId(),
                    handle.getPackId() == null ? "" : handle.getPackId().toString(),
                    handle.isDefaultPiece(),
                    handle.getProductId()
            ));
        }

        final List<PersonaPieceTintData> tintColors = new ObjectArrayList<>();
        for (Map.Entry<PieceType, TintMapColor> entry : serialized.getPieceTintColors().entrySet()) {
            final List<String> colors = new ObjectArrayList<>();
            for (int color : entry.getValue().getColors()) {
                colors.add(String.format("#%08x", color));
            }
            tintColors.add(new PersonaPieceTintData(PERSONA_PREFIX + entry.getKey().getId(), colors));
        }

        return Skin.of(
                serialized.getID(),
                serialized.getPlayFabID(),
                serialized.getResourcePatch(),
                toImageData(serialized.getImageData()),
                animations,
                toImageData(serialized.getCapeImageData()),
                serialized.getGeometryData(),
                serialized.getGeometryDataMinEngineVersion(),
                serialized.getAnimationData(),
                serialized.isPremium(),
                serialized.isPersona(),
                serialized.isPersonaCapeOnClassicSkin(),
                serialized.isPrimaryUser(),
                serialized.getCapeID(),
                serialized.getFullID(),
                serialized.getArmSize() == ArmSizeType.SLIM ? "slim" : "wide",
                String.format("#%x", serialized.getSkinColor()),
                personaPieces,
                tintColors,
                serialized.isOverridesPlayerAppearance()
        );
    }

    public boolean isTrusted(SerializedSkin serialized) {
        return serialized.getTrustedSkinFlag() == TrustedSkinFlag.TRUE;
    }

    private @Nullable PieceType toPieceType(String type) {
        if (type == null) {
            return null;
        }
        String id = type.toLowerCase(Locale.ROOT);
        if (id.startsWith(PERSONA_PREFIX)) {
            id = id.substring(PERSONA_PREFIX.length());
        }
        final String flatId = id.replace("_", "");
        for (PieceType pieceType : PieceType.values()) {
            final String pieceId = pieceType.getId().toLowerCase(Locale.ROOT);
            if (pieceId.equals(id) || pieceId.replace("_", "").equals(flatId)) {
                return pieceType;
            }
        }
        return null;
    }

    private SkinImage toSkinImage(@Nullable ImageData image) {
        if (image == null) {
            return new SkinImage(0, 0, new byte[0]);
        }
        return new SkinImage(image.getWidth(), image.getHeight(), image.getImage());
    }

    private ImageData toImageData(@Nullable SkinImage image) {
        if (image == null) {
            return ImageData.EMPTY;
        }
        return ImageData.of(image.getWidth(), image.getHeight(), image.getImageBytes());
    }

    private AnimatedTextureType toPayloadTextureType(
            org.cloudburstmc.protocol.bedrock.data.skin.AnimatedTextureType type) {
        return switch (type) {
            case BODY_32X32 -> AnimatedTextureType.BODY_32X32;
            case BODY_128X128 -> AnimatedTextureType.BODY_128X128;
            default -> AnimatedTextureType.FACE;
        };
    }

    private org.cloudburstmc.protocol.bedrock.data.skin.AnimatedTextureType fromPayloadTextureType(
            AnimatedTextureType type) {
        return switch (type) {
            case BODY_32X32 -> org.cloudburstmc.protocol.bedrock.data.skin.AnimatedTextureType.BODY_32X32;
            case BODY_128X128 -> org.cloudburstmc.protocol.bedrock.data.skin.AnimatedTextureType.BODY_128X128;
            case FACE -> org.cloudburstmc.protocol.bedrock.data.skin.AnimatedTextureType.FACE;
            case NONE -> org.cloudburstmc.protocol.bedrock.data.skin.AnimatedTextureType.NONE;
        };
    }

    private int parseColor(@Nullable String color) {
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

    private UUID parseUuid(@Nullable String value) {
        if (value == null) {
            return new UUID(0, 0);
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return new UUID(0, 0);
        }
    }

    private String orEmpty(@Nullable String value) {
        return value == null ? "" : value;
    }

    private String orDefault(@Nullable String value, String defaultValue) {
        return value == null || value.isEmpty() ? defaultValue : value;
    }

    private String geometryDataOrEmptyObject(@Nullable String geometryData) {
        if (geometryData == null) {
            return EMPTY_GEOMETRY_DATA;
        }
        final String trimmed = geometryData.trim();
        return trimmed.startsWith("{") ? trimmed : EMPTY_GEOMETRY_DATA;
    }
}
