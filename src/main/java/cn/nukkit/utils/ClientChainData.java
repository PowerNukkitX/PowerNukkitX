package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.network.connection.util.EncryptionUtils;
import cn.nukkit.network.protocol.LoginPacket;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.lang.JoseException;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * ClientChainData is a container of chain data sent from clients.
 * <p>
 * Device information such as client UUID, xuid and serverAddress, can be
 * read from instances of this object.
 * <p>
 * To get chain data, you can use player.getLoginChainData() or read(loginPacket)
 * <p>
 * ===============
 *
 * @author boybook (Nukkit Project)
 * ===============
 */
public final class ClientChainData implements LoginChainData {

    public static ClientChainData of(BinaryStream buffer) {
        return new ClientChainData(buffer);
    }

    public static ClientChainData read(LoginPacket pk) {
        return of(pk.getBuffer());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public UUID getClientUUID() {
        return clientUUID;
    }

    @Override
    public String getIdentityPublicKey() {
        return identityPublicKey;
    }

    @Override
    public long getClientId() {
        return clientId;
    }

    @Override
    public String getServerAddress() {
        return serverAddress;
    }

    @Override
    public String getDeviceModel() {
        return deviceModel;
    }

    @Override
    public int getDeviceOS() {
        return deviceOS;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public String getGameVersion() {
        return gameVersion;
    }

    @Override
    public int getGuiScale() {
        return guiScale;
    }

    @Override
    public String getLanguageCode() {
        return languageCode;
    }

    @Override
    public String getXUID() {
        if (this.isWaterdog()) {
            return waterdogXUID;
        } else {
            return xuid;
        }
    }

    public String getTitleId() {
        return titleId;
    }

    private boolean xboxAuthed;

    @Override
    public int getCurrentInputMode() {
        return currentInputMode;
    }

    @Override
    public int getDefaultInputMode() {
        return defaultInputMode;
    }

    @Override
    public String getCapeData() {
        return capeData;
    }

    public final static int UI_PROFILE_CLASSIC = 0;
    public final static int UI_PROFILE_POCKET = 1;

    @Override
    public int getUIProfile() {
        return UIProfile;
    }

    @Override
    public String getWaterdogXUID() {
        return waterdogXUID;
    }

    @Override
    public String getWaterdogIP() {
        return waterdogIP;
    }

    @Override
    public int getMaxViewDistance() {
        return this.maxViewDistance;
    }

    @Override
    public int getMemoryTier() {
        return this.memoryTier;
    }

    @Override
    public JsonObject getRawData() {
        return rawData;
    }

    private boolean isWaterdog() {
        if (waterdogXUID == null || Server.getInstance() == null) {
            return false;
        }

        return Server.getInstance().getSettings().baseSettings().waterdogpe();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Override
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClientChainData && Objects.equals(bs, ((ClientChainData) obj).bs);
    }

    @Override
    public int hashCode() {
        return bs.hashCode();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Internal
    ///////////////////////////////////////////////////////////////////////////

    private String username;
    private UUID clientUUID;
    private String xuid;
    private String titleId;

    private static ECPublicKey generateKey(String base64) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return (ECPublicKey) KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(base64)));
    }

    private String identityPublicKey;

    private long clientId;
    private String serverAddress;
    private String deviceModel;
    private int deviceOS;
    private String deviceId;
    private String gameVersion;
    private int guiScale;
    private String languageCode;
    private int currentInputMode;
    private int defaultInputMode;
    private String waterdogIP;
    private String waterdogXUID;
    private int maxViewDistance;
    private int memoryTier;

    private int UIProfile;

    private String capeData;

    private JsonObject rawData;

    private final BinaryStream bs;

    private ClientChainData(BinaryStream buffer) {
        buffer.setOffset(0);
        bs = buffer;
        decodeChainData();
        decodeSkinData();
    }

    @Override
    public boolean isXboxAuthed() {
        return xboxAuthed;
    }

    private void decodeSkinData() {
        JsonObject skinToken = decodeToken(new String(bs.get(bs.getLInt())));
        if (skinToken == null) return;
        if (skinToken.has("ClientRandomId")) this.clientId = skinToken.get("ClientRandomId").getAsLong();
        if (skinToken.has("ServerAddress")) this.serverAddress = skinToken.get("ServerAddress").getAsString();
        if (skinToken.has("DeviceModel")) this.deviceModel = skinToken.get("DeviceModel").getAsString();
        if (skinToken.has("DeviceOS")) this.deviceOS = skinToken.get("DeviceOS").getAsInt();
        if (skinToken.has("DeviceId")) this.deviceId = skinToken.get("DeviceId").getAsString();
        if (skinToken.has("GameVersion")) this.gameVersion = skinToken.get("GameVersion").getAsString();
        if (skinToken.has("GuiScale")) this.guiScale = skinToken.get("GuiScale").getAsInt();
        if (skinToken.has("LanguageCode")) this.languageCode = skinToken.get("LanguageCode").getAsString();
        if (skinToken.has("CurrentInputMode")) this.currentInputMode = skinToken.get("CurrentInputMode").getAsInt();
        if (skinToken.has("DefaultInputMode")) this.defaultInputMode = skinToken.get("DefaultInputMode").getAsInt();
        if (skinToken.has("UIProfile")) this.UIProfile = skinToken.get("UIProfile").getAsInt();
        if (skinToken.has("CapeData")) this.capeData = skinToken.get("CapeData").getAsString();
        if (skinToken.has("Waterdog_IP")) this.waterdogIP = skinToken.get("Waterdog_IP").getAsString();
        if (skinToken.has("Waterdog_XUID")) this.waterdogXUID = skinToken.get("Waterdog_XUID").getAsString();
        if (skinToken.has("MaxViewDistance")) this.maxViewDistance = skinToken.get("MaxViewDistance").getAsInt();
        if (skinToken.has("MemoryTier")) this.memoryTier = skinToken.get("MemoryTier").getAsInt();

        if (this.isWaterdog()) {
            xboxAuthed = true;
        }

        this.rawData = skinToken;
    }

    private JsonObject decodeToken(String token) {
        String[] base = token.split("\\.");
        if (base.length < 2) return null;
        String json = new String(Base64.getDecoder().decode(base[1]), StandardCharsets.UTF_8);
        //Server.getInstance().getLogger().debug(json);
        return JSONUtils.from(json, JsonObject.class);
    }

    private void decodeChainData() {
        Map<String, List<String>> map = JSONUtils.from(new String(bs.get(bs.getLInt()), StandardCharsets.UTF_8),
                new TypeToken<Map<String, List<String>>>() {
                }.getType());
        if (map.isEmpty() || !map.containsKey("chain") || map.get("chain").isEmpty()) return;
        List<String> chains = map.get("chain");

        // Validate keys
        try {
            xboxAuthed = verifyChain(chains);
        } catch (Exception e) {
            xboxAuthed = false;
        }

        for (String c : chains) {
            JsonObject chainMap = decodeToken(c);
            if (chainMap == null) continue;
            if (chainMap.has("extraData")) {
                JsonObject extra = chainMap.get("extraData").getAsJsonObject();
                if (extra.has("displayName")) this.username = extra.get("displayName").getAsString();
                if (extra.has("identity")) this.clientUUID = UUID.fromString(extra.get("identity").getAsString());
                if (extra.has("XUID")) this.xuid = extra.get("XUID").getAsString();
                if (extra.has("titleId") && extra.get("titleId").isJsonPrimitive()) this.titleId = extra.get("titleId").getAsString();
            }
            if (chainMap.has("identityPublicKey"))
                this.identityPublicKey = chainMap.get("identityPublicKey").getAsString();
        }

        if (!xboxAuthed) {
            xuid = null;
        }
    }

    private boolean verifyChain(List<String> chains) throws Exception {
        ECPublicKey lastKey = null;
        boolean mojangKeyVerified = false;
        Iterator<String> iterator = chains.iterator();
        long epoch = Instant.now().getEpochSecond();
        while (iterator.hasNext()) {
            JsonWebSignature jws = (JsonWebSignature) JsonWebSignature.fromCompactSerialization(iterator.next());
            String x5us = jws.getHeader("x5u");
            if (x5us == null) {
                return false;
            }
            ECPublicKey expectedKey = generateKey(x5us);
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

            if (lastKey.equals(EncryptionUtils.getMojangPublicKey()) || lastKey.equals(EncryptionUtils.getOldMojangPublicKey())) {
                mojangKeyVerified = true;
            }

            Map<String, Object> payload = JSONUtils.from(jws.getPayload(), new TypeToken<Map<String, Object>>() {
            });

            // chain expiry check
            Object chainExpiresObj = payload.get("exp");
            long chainExpires;
            if (chainExpiresObj instanceof Number number) {
                chainExpires = number.longValue();
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

    private boolean verify(ECPublicKey key, JsonWebSignature jws) {
        try {
            if (key == null || jws == null) {
                return false;
            }
            jws.setKey(key);
            return jws.verifySignature();
        } catch (JoseException e) {
            throw new RuntimeException(e);
        }
    }
}
