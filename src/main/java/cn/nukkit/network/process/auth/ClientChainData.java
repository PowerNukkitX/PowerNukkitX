package cn.nukkit.network.process.auth;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import lombok.Value;
import org.cloudburstmc.protocol.bedrock.data.BuildPlatform;
import org.cloudburstmc.protocol.bedrock.data.GraphicsMode;
import org.cloudburstmc.protocol.bedrock.data.InputMode;
import org.cloudburstmc.protocol.bedrock.data.MemoryTier;
import org.cloudburstmc.protocol.bedrock.data.PlatformType;
import org.cloudburstmc.protocol.bedrock.data.UserInterfaceProfile;
import org.jose4j.jwt.JwtClaims;

import java.util.Map;
import java.util.UUID;

/**
 * @author Kaooot
 */
@Value
public class ClientChainData {

    long clientRandomId;
    boolean compatibleWithClientSideChunkGen;
    InputMode currentInputMode;
    InputMode defaultInputMode;
    String deviceId;
    String deviceModel;
    BuildPlatform deviceOS;
    String gameVersion;
    GraphicsMode graphicsMode;
    int guiScale;
    boolean isEditorMode;
    String languageCode;
    int maxViewDistance;
    MemoryTier memoryTier;
    String partyId;
    String platformOfflineId;
    String platformOnlineId;
    PlatformType platformType;
    UUID selfSignedId;
    String serverAddress;
    String thirdPartyName;
    boolean trustedSkin;
    UserInterfaceProfile uiProfile;
    boolean isEduMode;

    public static ClientChainData from(JwtClaims claims) {
        final Map<String, Object> map = claims.getClaimsMap();
        if (map.isEmpty()) {
            return null;
        }
        if (!map.containsKey("ClientRandomId") || !(map.get("ClientRandomId") instanceof Long clientRandomId)) {
            return null;
        }
        if (!map.containsKey("CompatibleWithClientSideChunkGen") ||
                !(map.get("CompatibleWithClientSideChunkGen") instanceof Boolean compatibleWithClientSideChunkGen)) {
            return null;
        }
        final InputMode currentInputMode;
        if ((currentInputMode = validateEnum("CurrentInputMode", map, InputMode::from)) == null) {
            return null;
        }
        final InputMode defaultInputMode;
        if ((defaultInputMode = validateEnum("DefaultInputMode", map, InputMode::from)) == null) {
            return null;
        }
        if (!map.containsKey("DeviceId") || !(map.get("DeviceId") instanceof String deviceId)) {
            return null;
        }
        if (!map.containsKey("DeviceModel") || !(map.get("DeviceModel") instanceof String deviceModel)) {
            return null;
        }
        final BuildPlatform deviceOS;
        if ((deviceOS = validateEnum("DeviceOS", map, BuildPlatform::from)) == null) {
            return null;
        }
        if (!map.containsKey("GameVersion") || !(map.get("GameVersion") instanceof String gameVersion)) {
            return null;
        }
        final GraphicsMode graphicsMode;
        if ((graphicsMode = validateEnum("GraphicsMode", map, GraphicsMode::from)) == null) {
            return null;
        }
        if (!map.containsKey("GuiScale") || !(map.get("GuiScale") instanceof Number guiScale)) {
            return null;
        }
        if (!map.containsKey("IsEditorMode") || !(map.get("IsEditorMode") instanceof Boolean isEditorMode)) {
            return null;
        }
        if (!map.containsKey("LanguageCode") || !(map.get("LanguageCode") instanceof String languageCode)) {
            return null;
        }
        if (!map.containsKey("MaxViewDistance") || !(map.get("MaxViewDistance") instanceof Number maxViewDistance)) {
            return null;
        }
        final MemoryTier memoryTier;
        if ((memoryTier = validateEnum("MemoryTier", map, MemoryTier::from)) == null) {
            return null;
        }
        if (!map.containsKey("PartyId") || !(map.get("PartyId") instanceof String partyId)) {
            return null;
        }
        if (!map.containsKey("PlatformOfflineId") || !(map.get("PlatformOfflineId") instanceof String platformOfflineId)) {
            return null;
        }
        if (!map.containsKey("PlatformOnlineId") || !(map.get("PlatformOnlineId") instanceof String platformOnlineId)) {
            return null;
        }
        final PlatformType platformType;
        if ((platformType = validateEnum("PlatformType", map, PlatformType::from)) == null) {
            return null;
        }
        if (!map.containsKey("SelfSignedId") || !(map.get("SelfSignedId") instanceof String selfSignedIdStr)) {
            return null;
        }
        if (!map.containsKey("ServerAddress") || !(map.get("ServerAddress") instanceof String serverAddress)) {
            return null;
        }
        if (!map.containsKey("ThirdPartyName") || !(map.get("ThirdPartyName") instanceof String thirdPartyName)) {
            return null;
        }
        if (!map.containsKey("TrustedSkin") || !(map.get("TrustedSkin") instanceof Boolean trustedSkin)) {
            return null;
        }
        final UserInterfaceProfile uiProfile;
        if ((uiProfile = validateEnum("UIProfile", map, UserInterfaceProfile::from)) == null) {
            return null;
        }
        UUID selfSignedId;
        try {
            selfSignedId = UUID.fromString(selfSignedIdStr);
        } catch (Exception e) {
            return null;
        }
        return new ClientChainData(
                clientRandomId,
                compatibleWithClientSideChunkGen,
                currentInputMode,
                defaultInputMode,
                deviceId,
                deviceModel,
                deviceOS,
                gameVersion,
                graphicsMode,
                guiScale.intValue(),
                isEditorMode,
                languageCode,
                maxViewDistance.intValue(),
                memoryTier,
                partyId,
                platformOfflineId,
                platformOnlineId,
                platformType,
                selfSignedId,
                serverAddress,
                thirdPartyName,
                trustedSkin,
                uiProfile,
                map.containsKey("IsEduMode")
        );
    }

    private static <T> T validateEnum(String key, Map<String, Object> map, Int2ObjectFunction<T> mapper) {
        if (!map.containsKey(key) || !(map.get(key) instanceof Number ordinal)) {
            return null;
        } else {
            try {
                return mapper.apply(ordinal.intValue());
            } catch (Exception e) {
                return null;
            }
        }
    }
}