package cn.nukkit.network.protocol;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.network.connection.util.HandleByteBuf;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.JSONUtils;
import cn.nukkit.utils.PersonaPiece;
import cn.nukkit.utils.PersonaPieceTint;
import cn.nukkit.utils.SerializedImage;
import cn.nukkit.utils.SkinAnimation;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @since on 15-10-13
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginPacket extends DataPacket {
    public static final int NETWORK_ID = ProtocolInfo.LOGIN_PACKET;

    public String username;
    public int protocol;
    public UUID clientUUID;
    public long clientId;
    public Skin skin;
    public long issueUnixTime = -1;

    private BinaryStream buffer;

    @Override
    public int pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode(HandleByteBuf byteBuf) {
        this.protocol = byteBuf.readInt();
        if (protocol == 0) {
            byteBuf.readerIndex(byteBuf.readerIndex() + 2);
            this.protocol = byteBuf.readInt();
        }
        buffer = new BinaryStream(byteBuf.readByteArray(), 0);
        decodeChainData(buffer);
        decodeSkinData(buffer);
    }

    @Override
    public void encode(HandleByteBuf byteBuf) {
    }

    public int getProtocol() {
        return protocol;
    }

    private void decodeChainData(BinaryStream binaryStream) {
        Map<String, List<String>> map = JSONUtils.from(new String(binaryStream.get(binaryStream.getLInt()), StandardCharsets.UTF_8),
                new TypeToken<Map<String, List<String>>>() {
                });
        if (map.isEmpty() || !map.containsKey("chain") || map.get("chain").isEmpty()) return;
        List<String> chains = map.get("chain");
        for (String c : chains) {
            JsonObject chainMap = decodeToken(c);
            if (chainMap == null) continue;
            if (chainMap.has("extraData")) {
                if (chainMap.has("iat")) {
                    this.issueUnixTime = chainMap.get("iat").getAsLong() * 1000;
                }
                JsonObject extra = chainMap.get("extraData").getAsJsonObject();
                if (extra.has("displayName")) this.username = extra.get("displayName").getAsString();
                if (extra.has("identity")) this.clientUUID = UUID.fromString(extra.get("identity").getAsString());
            }
        }
    }

    private void decodeSkinData(BinaryStream binaryStream) {
        JsonObject skinToken = decodeToken(new String(binaryStream.get(binaryStream.getLInt())));
        if (skinToken.has("ClientRandomId")) this.clientId = skinToken.get("ClientRandomId").getAsLong();

        skin = new Skin();

        if (skinToken.has("PlayFabId")) {
            skin.setPlayFabId(skinToken.get("PlayFabId").getAsString());
        }

        if (skinToken.has("CapeId")) {
            skin.setCapeId(skinToken.get("CapeId").getAsString());
        }

        if (skinToken.has("SkinId")) {
            //这边获取到的"SkinId"是FullId
            //FullId = SkinId + CapeId
            //而Skin对象中的skinId不是FullId,我们需要减掉CapeId
            var fullSkinId = skinToken.get("SkinId").getAsString();
            skin.setFullSkinId(fullSkinId);
            if (skin.getCapeId() != null)
                skin.setSkinId(fullSkinId.substring(0, fullSkinId.length() - skin.getCapeId().length()));
            else
                skin.setSkinId(fullSkinId);
        }

        skin.setSkinData(getImage(skinToken, "Skin"));
        skin.setCapeData(getImage(skinToken, "Cape"));

        if (skinToken.has("PremiumSkin")) {
            skin.setPremium(skinToken.get("PremiumSkin").getAsBoolean());
        }

        if (skinToken.has("PersonaSkin")) {
            skin.setPersona(skinToken.get("PersonaSkin").getAsBoolean());
        }

        if (skinToken.has("CapeOnClassicSkin")) {
            skin.setCapeOnClassic(skinToken.get("CapeOnClassicSkin").getAsBoolean());
        }

        if (skinToken.has("SkinResourcePatch")) {
            skin.setSkinResourcePatch(new String(Base64.getDecoder().decode(skinToken.get("SkinResourcePatch").getAsString()), StandardCharsets.UTF_8));
        }

        if (skinToken.has("SkinGeometryData")) {
            skin.setGeometryData(new String(Base64.getDecoder().decode(skinToken.get("SkinGeometryData").getAsString()), StandardCharsets.UTF_8));
        }

        if (skinToken.has("SkinAnimationData")) {
            skin.setAnimationData(new String(Base64.getDecoder().decode(skinToken.get("SkinAnimationData").getAsString()), StandardCharsets.UTF_8));
        }

        if (skinToken.has("AnimatedImageData")) {
            for (JsonElement element : skinToken.get("AnimatedImageData").getAsJsonArray()) {
                skin.getAnimations().add(getAnimation(element.getAsJsonObject()));
            }
        }

        if (skinToken.has("SkinColor")) {
            skin.setSkinColor(skinToken.get("SkinColor").getAsString());
        }

        if (skinToken.has("ArmSize")) {
            skin.setArmSize(skinToken.get("ArmSize").getAsString());
        }

        if (skinToken.has("PersonaPieces")) {
            for (JsonElement object : skinToken.get("PersonaPieces").getAsJsonArray()) {
                skin.getPersonaPieces().add(getPersonaPiece(object.getAsJsonObject()));
            }
        }

        if (skinToken.has("PieceTintColors")) {
            for (JsonElement object : skinToken.get("PieceTintColors").getAsJsonArray()) {
                skin.getTintColors().add(getTint(object.getAsJsonObject()));
            }
        }
    }

    private JsonObject decodeToken(String token) {
        String[] base = token.split("\\.");
        if (base.length < 2) return null;
        return new Gson().fromJson(new String(Base64.getDecoder().decode(base[1]), StandardCharsets.UTF_8), JsonObject.class);
    }

    private static SkinAnimation getAnimation(JsonObject element) {
        float frames = element.get("Frames").getAsFloat();
        int type = element.get("Type").getAsInt();
        byte[] data = Base64.getDecoder().decode(element.get("Image").getAsString());
        int width = element.get("ImageWidth").getAsInt();
        int height = element.get("ImageHeight").getAsInt();
        int expression = element.get("AnimationExpression").getAsInt();
        return new SkinAnimation(new SerializedImage(width, height, data), type, frames, expression);
    }

    private static SerializedImage getImage(JsonObject token, String name) {
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

    private static PersonaPiece getPersonaPiece(JsonObject object) {
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

    public BinaryStream getBuffer() {
        return buffer;
    }

    public void handle(PacketHandler handler) {
        handler.handle(this);
    }
}
