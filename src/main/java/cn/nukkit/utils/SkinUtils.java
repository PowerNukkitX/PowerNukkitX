package cn.nukkit.utils;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class SkinUtils {
    /** Hard upper bound on declared image dimensions. 1024 covers every legitimate Bedrock skin/cape; anything larger is malicious. */
    public static final int MAX_IMAGE_DIMENSION = 1024;
    /** Hard upper bound on raw image bytes after base64 decode. 1024 * 1024 * 4 (RGBA). */
    public static final int MAX_IMAGE_BYTES = MAX_IMAGE_DIMENSION * MAX_IMAGE_DIMENSION * 4;
    /** Conservative cap on the base64 string itself, before decoding. */
    public static final int MAX_IMAGE_BASE64_LENGTH = ((MAX_IMAGE_BYTES + 2) / 3) * 4;

    public static SkinAnimation getAnimation(JsonObject element) {
        float frames = element.get("Frames").getAsFloat();
        int type = element.get("Type").getAsInt();
        String b64 = element.get("Image").getAsString();
        Preconditions.checkArgument(b64.length() <= MAX_IMAGE_BASE64_LENGTH, "Animation image base64 too large: %s", b64.length());
        byte[] data = Base64.getDecoder().decode(b64);
        int width = element.get("ImageWidth").getAsInt();
        int height = element.get("ImageHeight").getAsInt();
        validateImageDimensions(width, height, data.length);
        int expression = element.get("AnimationExpression").getAsInt();
        return new SkinAnimation(new SerializedImage(width, height, data), type, frames, expression);
    }

    public static SerializedImage getImage(JsonObject token, String name) {
        if (token.has(name + "Data")) {
            String b64 = token.get(name + "Data").getAsString();
            Preconditions.checkArgument(b64.length() <= MAX_IMAGE_BASE64_LENGTH, "%sData base64 too large: %s", name, b64.length());
            byte[] skinImage = Base64.getDecoder().decode(b64);
            if (token.has(name + "ImageHeight") && token.has(name + "ImageWidth")) {
                int width = token.get(name + "ImageWidth").getAsInt();
                int height = token.get(name + "ImageHeight").getAsInt();
                validateImageDimensions(width, height, skinImage.length);
                return new SerializedImage(width, height, skinImage);
            } else {
                Preconditions.checkArgument(skinImage.length <= MAX_IMAGE_BYTES, "%s legacy image too large: %s", name, skinImage.length);
                return SerializedImage.fromLegacy(skinImage);
            }
        }
        return SerializedImage.EMPTY;
    }

    public static PersonaPiece getPersonaPiece(JsonObject object) {
        String pieceId = object.get("PieceId").getAsString();
        String pieceType = object.get("PieceType").getAsString();
        String packId = object.get("PackId").getAsString();
        boolean isDefault = object.get("IsDefault").getAsBoolean();
        String productId = object.get("ProductId").getAsString();
        return new PersonaPiece(pieceId, pieceType, packId, isDefault, productId);
    }

    public static PersonaPieceTint getTint(JsonObject object) {
        String pieceType = object.get("PieceType").getAsString();
        List<String> colors = new ArrayList<>();
        for (JsonElement element : object.get("Colors").getAsJsonArray()) {
            colors.add(element.getAsString()); // remove #
        }
        return new PersonaPieceTint(pieceType, colors);
    }

    private static void validateImageDimensions(int width, int height, int dataLength) {
        if (width == 0 && height == 0 && dataLength == 0) {
            return;
        }
        Preconditions.checkArgument(width >= 0 && height >= 0 && width <= MAX_IMAGE_DIMENSION && height <= MAX_IMAGE_DIMENSION, "Invalid image dimensions: %sx%s", width, height);
        long expected = (long) width * (long) height * 4L;
        Preconditions.checkArgument(expected == dataLength, "Image data length %s does not match %sx%sx4", dataLength, width, height);
    }
}
