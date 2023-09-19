package cn.nukkit.player;

import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.lang.LangCode;
import cn.nukkit.network.encryption.EncryptionUtils;
import cn.nukkit.network.protocol.LoginPacket;
import cn.nukkit.network.protocol.types.InputMode;
import cn.nukkit.player.data.Device;
import cn.nukkit.utils.BinaryStream;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public class PlayerInfo {

    private static final Gson GSON = new Gson();

    /* General */
    private String username;
    private long clientId;
    private boolean xboxAuthed;
    private String xuid;
    private UUID uuid;
    private String identityPublicKey;

    /* Skin */
    private Skin skin;
    private String capeData;

    /* Device */
    private String deviceModel;
    private Device deviceOs;
    private String deviceId;

    /* Input mode */
    private InputMode currentInputMode;
    private InputMode defaultInputMode;

    /* Other */
    private String serverAddress;
    private String gameVersion;
    private String languageCodeString;
    private LangCode languageCode;
    private int guiScale;
    private int UIProfile;

    /* Waterdog */
    private String waterdogIp;
    private String waterdogXuid;

    /* Raw data */
    private JsonObject extraData;
    private JsonObject rawData;

    @Getter(value = AccessLevel.PRIVATE)
    private final BinaryStream binaryStream = new BinaryStream();

    public PlayerInfo(LoginPacket packet) {
        this(packet.getBuffer(), packet.skin);
    }

    public PlayerInfo(byte[] buffer, Skin skin) {
        this.binaryStream.setBuffer(buffer, 0);
        this.skin = skin;
        this.decodeChainData();
        this.decodeData();
        this.languageCode = LangCode.valueOf(languageCodeString);
    }

    private void decodeChainData() {
        byte[] binaryData = binaryStream.get(binaryStream.getLInt());
        String jsonData = new String(binaryData, StandardCharsets.UTF_8);

        Map<String, List<String>> map =
                GSON.fromJson(jsonData, new TypeToken<Map<String, List<String>>>() {}.getType());
        if (map.isEmpty() || !map.containsKey("chain") || map.get("chain").isEmpty()) {
            return;
        }
        List<String> chains = map.get("chain");

        try {
            xboxAuthed = verifyChains(chains);
        } catch (Exception e) {
            xboxAuthed = false;
        }

        for (String chain : chains) {
            JsonObject chainMap = decodeToken(chain);
            if (chainMap == null) {
                continue;
            }
            if (chainMap.has("extraData")) {
                this.extraData = chainMap.get("extraData").getAsJsonObject();
                this.username = extraData.get("displayName").getAsString();
                this.uuid = UUID.fromString(extraData.get("identity").getAsString());
                this.xuid = extraData.get("XUID").getAsString();
            }
            this.identityPublicKey = chainMap.get("identityPublicKey").getAsString();
        }

        if (!xboxAuthed) {
            this.xuid = null;
        }
    }

    private void decodeData() {
        JsonObject token = decodeToken(new String(binaryStream.get(binaryStream.getLInt())));
        if (token == null) {
            return;
        }
        if (token.has("ClientRandomId")) {
            this.clientId = token.get("ClientRandomId").getAsLong();
        }
        if (token.has("ServerAddress")) {
            this.serverAddress = token.get("ServerAddress").getAsString();
        }
        if (token.has("DeviceModel")) {
            this.deviceModel = token.get("DeviceModel").getAsString();
        }
        if (token.has("DeviceOS")) {
            this.deviceOs = Device.fromId(token.get("DeviceOS").getAsInt());
        }
        if (token.has("DeviceId")) {
            this.deviceId = token.get("DeviceId").getAsString();
        }
        if (token.has("GameVersion")) {
            this.gameVersion = token.get("GameVersion").getAsString();
        }
        if (token.has("GuiScale")) {
            this.guiScale = token.get("GuiScale").getAsInt();
        }
        if (token.has("LanguageCode")) {
            this.languageCodeString = token.get("LanguageCode").getAsString();
        }
        if (token.has("CurrentInputMode")) {
            this.currentInputMode =
                    InputMode.fromOrdinal(token.get("CurrentInputMode").getAsInt());
        }
        if (token.has("DefaultInputMode")) {
            this.defaultInputMode =
                    InputMode.fromOrdinal(token.get("DefaultInputMode").getAsInt());
        }
        if (token.has("UIProfile")) {
            this.UIProfile = token.get("UIProfile").getAsInt();
        }
        if (token.has("CapeData")) {
            this.capeData = token.get("CapeData").getAsString();
        }
        if (token.has("Waterdog_IP")) {
            this.waterdogIp = token.get("Waterdog_IP").getAsString();
        }
        if (token.has("Waterdog_XUID")) {
            this.waterdogXuid = token.get("Waterdog_XUID").getAsString();
        }
        if (this.isWaterdogCapable()) {
            this.xboxAuthed = true;
        }
        this.rawData = token;
    }

    private JsonObject decodeToken(String token) {
        String[] base = token.split("\\.");
        if (base.length < 2) return null;
        String json = new String(Base64.getDecoder().decode(base[1]), StandardCharsets.UTF_8);
        return new Gson().fromJson(json, JsonObject.class);
    }

    private boolean verifyChains(List<String> chains) throws Exception {
        ECPublicKey lastKey = null;
        boolean mojangKeyVerified = false;
        long epoch = Instant.now().getEpochSecond();
        Iterator<String> iterator = chains.iterator();
        while (iterator.hasNext()) {
            JWSObject jws = JWSObject.parse(iterator.next());

            URI x5u = jws.getHeader().getX509CertURL();
            if (x5u == null) {
                return false;
            }

            ECPublicKey expectedKey = generateKey(x5u.toString());
            // First key is self-signed
            if (lastKey == null) {
                lastKey = expectedKey;
            } else if (!lastKey.equals(expectedKey)) {
                return false;
            }

            if (!verify(lastKey, jws)) {
                return false;
            }

            if (mojangKeyVerified) {
                return !iterator.hasNext();
            }

            if (lastKey.equals(EncryptionUtils.getMojangPublicKey())
                    || lastKey.equals(EncryptionUtils.getOldMojangPublicKey())) {
                mojangKeyVerified = true;
            }

            Map<String, Object> payload = jws.getPayload().toJSONObject();

            // chain expiry check
            Object chainExpiresObj = payload.get("exp");
            long chainExpires;
            if (chainExpiresObj instanceof Long) {
                chainExpires = (Long) chainExpiresObj;
            } else if (chainExpiresObj instanceof Integer) {
                chainExpires = (Integer) chainExpiresObj;
            } else {
                throw new RuntimeException("Unsupported expiry time format");
            }
            if (chainExpires < epoch) {
                // chain has already expires
                return false;
            }

            Object base64key = payload.get("identityPublicKey");
            if (!(base64key instanceof String)) {
                throw new RuntimeException("No key found");
            }
            lastKey = generateKey((String) base64key);
        }
        return mojangKeyVerified;
    }

    private boolean verify(ECPublicKey key, JWSObject object) throws JOSEException {
        return object.verify(new ECDSAVerifier(key));
    }

    private static ECPublicKey generateKey(String base64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return (ECPublicKey) KeyFactory.getInstance("EC")
                .generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(base64)));
    }

    /* Waterdog */

    private boolean isWaterdogCapable() {
        if (waterdogXuid == null || Server.getInstance() == null) {
            return false;
        }
        return Server.getInstance().isWaterdogCapable();
    }
}
