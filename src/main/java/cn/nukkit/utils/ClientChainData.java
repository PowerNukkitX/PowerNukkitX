package cn.nukkit.utils;

import cn.nukkit.Server;
import cn.nukkit.network.process.login.LoginData;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Container for client chain data sent during login.
 * Provides access to device and authentication information such as UUID, XUID, and server address.
 * Use player.getLoginChainData() to retrieve an instance.
 *
 * @author boybook (Nukkit Project)
 */
@Slf4j
public final class ClientChainData implements LoginChainData {
    private static final int MAX_CLIENT_DATA_KEYS = 256;

    /**
     * Whitelist of keys produced by vanilla Bedrock clients (and read by this server or by skin
     * deserialization). Anything outside is logged once and ignored, mirroring the
     * "extraData CPU exhaustion" hardening in upstream forks.
     */
    private static final Set<String> KNOWN_CLIENT_DATA_KEYS = Set.of(
            // device / session
            "ClientRandomId", "ServerAddress", "DeviceModel", "DeviceOS", "DeviceId",
            "GameVersion", "GuiScale", "LanguageCode", "CurrentInputMode", "DefaultInputMode",
            "UIProfile", "MaxViewDistance", "MemoryTier", "PartyId", "PlatformOnlineId",
            "PlatformOfflineId", "PlatformUserId", "PlayFabId", "PlayFabID", "SelfSignedId",
            "ThirdPartyName", "ThirdPartyNameOnly", "TenantId", "GraphicsMode", "TrustedSkin",
            "CompatibleVersions", "IsEditorMode", "OverrideSkin",
            // proxy
            "Waterdog_IP", "Waterdog_XUID",
            // skin / persona
            "SkinId", "SkinData", "SkinImageWidth", "SkinImageHeight", "CapeData",
            "CapeImageWidth", "CapeImageHeight", "CapeId", "PremiumSkin", "PersonaSkin",
            "CapeOnClassicSkin", "SkinResourcePatch", "SkinGeometryData",
            "SkinGeometryDataEngineVersion", "SkinAnimationData", "AnimatedImageData",
            "SkinColor", "ArmSize", "PersonaPieces", "PieceTintColors"
    );

    public static ClientChainData of(LoginData data) {
        return new ClientChainData(data);
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
    public String getPartyId() {
        return this.partyId;
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
    // Internal
    ///////////////////////////////////////////////////////////////////////////

    private String username;
    private UUID clientUUID;
    private String xuid;
    private String titleId;

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

    private String partyId;

    private JsonObject rawData;

    private ClientChainData(LoginData data) {
        loadAuthData(data);
        loadClientData(data.clientData());
    }

    @Override
    public boolean isXboxAuthed() {
        return xboxAuthed;
    }

    private void loadClientData(JsonObject skinToken) {
        if (skinToken == null) return;
        validateClientDataKeys(skinToken);
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
        if (skinToken.has("PartyId")) this.partyId = skinToken.get("PartyId").getAsString();

        if (this.isWaterdog()) {
            xboxAuthed = true;
        }

        this.rawData = skinToken;
    }

    /**
     * Reject JWT bodies with absurd key counts and warn (once) about unknown keys.
     * Bedrock clients have a stable schema; thousands of keys means a forged token.
     */
    private static void validateClientDataKeys(JsonObject skinToken) {
        int size = skinToken.size();
        if (size > MAX_CLIENT_DATA_KEYS) {
            throw new IllegalArgumentException("ClientData has too many keys: " + size);
        }
        int unknown = 0;
        for (Map.Entry<String, ?> entry : skinToken.entrySet()) {
            if (!KNOWN_CLIENT_DATA_KEYS.contains(entry.getKey())) {
                unknown++;
            }
        }
        if (unknown > 0) {
            log.debug("ClientData contains {} unknown key(s), size={}", unknown, size);
        }
    }

    public static JsonObject decodeToken(String token) {
        String[] base = token.split("\\.");
        if (base.length < 2) return null;
        String json = new String(Base64.getDecoder().decode(base[1]), StandardCharsets.UTF_8);
        return JSONUtils.from(json, JsonObject.class);
    }

    private void loadAuthData(cn.nukkit.network.process.login.LoginData data) {
        xboxAuthed = data.xboxAuthed();

        username = data.displayName();
        clientUUID = data.uuid();
        xuid = data.xuid();
        identityPublicKey = data.rawIdentityPublicKey();
        /*titleId = data.;


                JsonElement titleIdElement = extra.get("titleId");
                if (titleIdElement != null && !titleIdElement.isJsonNull()) {
                    this.titleId = titleIdElement.getAsString();
                }

         */
    }
}
