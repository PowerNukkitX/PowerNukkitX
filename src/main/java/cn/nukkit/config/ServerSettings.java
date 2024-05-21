package cn.nukkit.config;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = true)
@Getter
@Accessors(fluent = true)
public final class ServerSettings extends OkaeriConfig {
    @Comment("nukkit.server.settings.baseSettings")
    @CustomKey("settings")
    private BaseSettings baseSettings = new BaseSettings();
    @Comment("nukkit.server.settings.networkSettings")
    @CustomKey("network-settings")
    private NetworkSettings networkSettings = new NetworkSettings();
    @Comment("nukkit.server.settings.debugSettings")
    @CustomKey("debug-settings")
    private DebugSettings debugSettings = new DebugSettings();
    @Comment("nukkit.server.settings.levelSettings")
    @CustomKey("level-settings")
    private LevelSettings levelSettings = new LevelSettings();
    @Comment("nukkit.server.settings.chunkSettings")
    @CustomKey("chunk-settings")
    private ChunkSettings chunkSettings = new ChunkSettings();
    @Comment("nukkit.server.settings.freezearray")
    @CustomKey("memory-settings")
    private FreezeArraySettings freezeArraySettings = new FreezeArraySettings();
    @Comment("nukkit.server.settings.playersettings")
    @CustomKey("player-settings")
    private PlayerSettings playerSettings = new PlayerSettings();

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class BaseSettings extends OkaeriConfig {
        @Comment("nukkit.server.settings.baseSettings.language")
        String language = "eng";
        boolean forceServerTranslate = false;
        String shutdownMessage = "Server closed";
        boolean queryPlugins = true;
        boolean deprecatedVerbose = true;
        String asyncWorkers = "auto";
        boolean safeSpawn = true;
        boolean installSpark = true;
        boolean waterdogpe = false;
        int autosave = 6000;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class NetworkSettings extends OkaeriConfig {
        int compressionLevel = 7;
        int zlibProvider = 3;
        boolean snappy = false;
        int compressionBufferSize = 1048576;
        int maxDecompressSize = 67108864;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class DebugSettings extends OkaeriConfig {
        String level = "INFO";
        boolean command = false;
        ArrayList<String> ignoredPackets = new ArrayList<>();
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class LevelSettings extends OkaeriConfig {
        boolean autoTickRate = true;
        int autoTickRateLimit = 20;
        int baseTickRate = 1;
        boolean alwaysTickPlayers = false;
        boolean enableRedstone = true;
        boolean tickRedstone = true;
        @Comment("This chunk will be unloaded after how many milliseconds It is not used,define whether is used through Level#isChunkInUse(long)")
        int chunkUnloadDelay = 15000;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class ChunkSettings extends OkaeriConfig {
        int perTickSend = 8;
        int spawnThreshold = 56;
        int chunksPerTicks = 40;
        int tickRadius = 3;
        boolean lightUpdates = true;
        boolean clearTickList = false;
        int generationQueueSize = 16;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class FreezeArraySettings extends OkaeriConfig {
        boolean enable = true;
        int slots = 32;
        int defaultTemperature = 32;
        int freezingPoint = 0;
        int boilingPoint = 1024;
        int absoluteZero = -256;
        int melting = 16;
        int singleOperation = 1;
        int batchOperation = 32;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class PlayerSettings extends OkaeriConfig {
        boolean savePlayerData = true;
        int skinChangeCooldown = 30;
        boolean forceSkinTrusted = false;
        boolean checkMovement = true;
    }
}
