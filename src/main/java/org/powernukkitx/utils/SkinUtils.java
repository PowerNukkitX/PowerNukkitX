package org.powernukkitx.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.experimental.UtilityClass;
import org.cloudburstmc.protocol.bedrock.data.skin.AnimationData;
import org.cloudburstmc.protocol.bedrock.data.skin.ImageData;
import org.cloudburstmc.protocol.bedrock.data.skin.Skin;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@UtilityClass
public class SkinUtils {

    /**
     * Upper bound for a single skin, cape or animation image. The largest vanilla layout is
     * {@link ImageData#SKIN_PERSONA_SIZE} (256x256 pixels, 256 KB), so this leaves ample room for
     * non-standard HD skins while keeping one packet from pinning a large array on the player.
     */
    private static final int MAX_SKIN_IMAGE_BYTES = 1024 * 1024;

    /**
     * Upper bound for all images of one skin combined. An animated skin is made of up to 100 frames
     * of {@link #MAX_SKIN_IMAGE_BYTES} each, which must not let a single skin hold hundreds of
     * megabytes for the lifetime of a session.
     */
    private static final int MAX_SKIN_TOTAL_BYTES = 8 * 1024 * 1024;

    public SerializedImage getImage(JsonObject token, String name) {
        if (token.has(name + "Data")) {
            byte[] skinImage = Base64.getDecoder().decode(token.get(name + "Data").getAsString());
            if (token.has(name + "ImageHeight") && token.has(name + "ImageWidth")) {
                int width = token.get(name + "ImageWidth").getAsInt();
                int height = token.get(name + "ImageHeight").getAsInt();
                return new SerializedImage(width, height, skinImage);
            } else {
                return SerializedImage.fromLegacy(skinImage);
            }
        }
        return SerializedImage.EMPTY;
    }

    public PersonaPiece getPersonaPiece(JsonObject object) {
        String pieceId = object.get("PieceId").getAsString();
        String pieceType = object.get("PieceType").getAsString();
        String packId = object.get("PackId").getAsString();
        boolean isDefault = object.get("IsDefault").getAsBoolean();
        String productId = object.get("ProductId").getAsString();
        return new PersonaPiece(pieceId, pieceType, packId, isDefault, productId);
    }

    public PersonaPieceTint getTint(JsonObject object) {
        String pieceType = object.get("PieceType").getAsString();
        List<String> colors = new ArrayList<>();
        for (JsonElement element : object.get("Colors").getAsJsonArray()) {
            colors.add(element.getAsString()); // remove #
        }
        return new PersonaPieceTint(pieceType, colors);
    }

    public boolean isValid(Skin skin) {
        return skin.getSkinId() != null && !skin.getSkinId().trim().isEmpty() && skin.getSkinId().length() < 100 &&
                skin.getSkinData() != null && skin.getSkinData().getWidth() >= 32 && skin.getSkinData().getHeight() >= 32 &&
                skin.getSkinData().getImage().length >= Skin.SINGLE_SKIN_SIZE &&
                isImageWithinLimit(skin.getSkinData()) &&
                isImageWithinLimit(skin.getCapeData()) &&
                skin.getAnimations().stream().allMatch(animation -> isImageWithinLimit(animation.getImage())) &&
                getTotalImageBytes(skin) <= MAX_SKIN_TOTAL_BYTES &&
                (skin.getPlayFabId() == null || skin.getPlayFabId().length() < 100) &&
                (skin.getCapeId() == null || skin.getCapeId().length() < 100) &&
                (skin.getSkinColor() == null || skin.getSkinColor().length() < 100) &&
                (skin.getArmSize() == null || skin.getArmSize().length() < 100) &&
                (skin.getFullSkinId() == null || skin.getFullSkinId().length() < 200) &&
                (skin.getGeometryDataEngineVersion() == null || skin.getGeometryDataEngineVersion().length() < 100) &&
                (skin.getAnimationData() == null || skin.getAnimationData().length() < 1000) &&
                skin.getAnimations().size() <= 100 &&
                skin.getPersonaPieces().size() <= 100 &&
                skin.getTintColors().size() <= 100 && isValidResourcePatch(skin.getSkinResourcePatch());
    }

    private boolean isImageWithinLimit(ImageData image) {
        return image == null || image.getImage().length <= MAX_SKIN_IMAGE_BYTES;
    }

    private long getTotalImageBytes(Skin skin) {
        long total = skin.getSkinData().getImage().length;
        if (skin.getCapeData() != null) {
            total += skin.getCapeData().getImage().length;
        }
        for (AnimationData animation : skin.getAnimations()) {
            if (animation.getImage() != null) {
                total += animation.getImage().getImage().length;
            }
        }
        return total;
    }

    public boolean isValidResourcePatch(String skinResourcePatch) {
        if (skinResourcePatch == null || skinResourcePatch.length() > 1000) {
            return false;
        }
        try {
            JsonObject object = Utils.GSON.fromJson(skinResourcePatch, JsonObject.class);
            JsonObject geometry = object.getAsJsonObject("geometry");
            return geometry.has("default") && geometry.get("default").isJsonPrimitive() && geometry.get("default").getAsJsonPrimitive().isString();
        } catch (ClassCastException | NullPointerException | JsonSyntaxException e) {
            return false;
        }
    }

    public static String convertGeometryName(String geometryName) {
        return "{\"geometry\" : {\"default\" : \"" + geometryName + "\"}}";
    }
}
