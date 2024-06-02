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
        @Comment("nukkit.server.settings.baseSettings.forceServerTranslate")
        boolean forceServerTranslate = false;
        @Comment("nukkit.server.settings.baseSettings.shutdownMessage")
        String shutdownMessage = "Server closed";
        @Comment("nukkit.server.settings.baseSettings.queryPlugins")
        boolean queryPlugins = true;
        @Comment("nukkit.server.settings.baseSettings.deprecatedVerbose")
        boolean deprecatedVerbose = true;
        @Comment("nukkit.server.settings.baseSettings.asyncWorkers")
        String asyncWorkers = "auto";
        @Comment("nukkit.server.settings.baseSettings.safeSpawn")
        boolean safeSpawn = true;
        @Comment("nukkit.server.settings.baseSettings.installSpark")
        boolean installSpark = true;
        @Comment("nukkit.server.settings.baseSettings.waterdogpe")
        boolean waterdogpe = false;
        @Comment("nukkit.server.settings.baseSettings.autosave")
        int autosave = 6000;
        @Comment("nukkit.server.settings.baseSettings.saveUnknownBlock")
        boolean saveUnknownBlock = true;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class NetworkSettings extends OkaeriConfig {
        @Comment("nukkit.server.settings.networkSettings.compressionLevel")
        int compressionLevel = 7;
        @Comment("nukkit.server.settings.networkSettings.zlibProvider")
        int zlibProvider = 3;
        @Comment("nukkit.server.settings.networkSettings.snappy")
        boolean snappy = false;
        @Comment("nukkit.server.settings.networkSettings.compressionBufferSize")
        int compressionBufferSize = 1048576;
        @Comment("nukkit.server.settings.networkSettings.maxDecompressSize")
        int maxDecompressSize = 67108864;
        @Comment("nukkit.server.settings.networkSettings.packetLimit")
        int packetLimit = 240;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class DebugSettings extends OkaeriConfig {
        @Comment("nukkit.server.settings.debugSettings.level")
        String level = "INFO";
        @Comment("nukkit.server.settings.debugSettings.command")
        boolean command = false;
        @Comment("nukkit.server.settings.debugSettings.ignoredPackets")
        ArrayList<String> ignoredPackets = new ArrayList<>();
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class LevelSettings extends OkaeriConfig {
        @Comment("nukkit.server.settings.levelSettings.autoTickRate")
        boolean autoTickRate = true;
        @Comment("nukkit.server.settings.levelSettings.autoTickRateLimit")
        int autoTickRateLimit = 20;
        @Comment("nukkit.server.settings.levelSettings.baseTickRate")
        int baseTickRate = 1;
        @Comment("nukkit.server.settings.levelSettings.alwaysTickPlayers")
        boolean alwaysTickPlayers = false;
        @Comment("nukkit.server.settings.levelSettings.enableRedstone")
        boolean enableRedstone = true;
        @Comment("nukkit.server.settings.levelSettings.tickRedstone")
        boolean tickRedstone = true;
        @Comment("nukkit.server.settings.levelSettings.chunkUnloadDelay")
        int chunkUnloadDelay = 15000;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class ChunkSettings extends OkaeriConfig {
        @Comment("nukkit.server.settings.chunkSettings.perTickSend")
        int perTickSend = 8;
        @Comment("nukkit.server.settings.chunkSettings.spawnThreshold")
        int spawnThreshold = 56;
        @Comment("nukkit.server.settings.chunkSettings.chunksPerTicks")
        int chunksPerTicks = 40;
        @Comment("nukkit.server.settings.chunkSettings.tickRadius")
        int tickRadius = 3;
        @Comment("nukkit.server.settings.chunkSettings.lightUpdates")
        boolean lightUpdates = true;
        @Comment("nukkit.server.settings.chunkSettings.clearTickList")
        boolean clearTickList = false;
        @Comment("nukkit.server.settings.chunkSettings.generationQueueSize")
        int generationQueueSize = 128;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class FreezeArraySettings extends OkaeriConfig {
        @Comment("nukkit.server.settings.freezearray.enable")
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
        @Comment("nukkit.server.settings.playersettings.savePlayerData")
        boolean savePlayerData = true;
        @Comment("nukkit.server.settings.playersettings.skinChangeCooldown")
        int skinChangeCooldown = 30;
        @Comment("nukkit.server.settings.playersettings.forceSkinTrusted")
        boolean forceSkinTrusted = false;
        @Comment("nukkit.server.settings.playersettings.checkMovement")
        boolean checkMovement = true;
    }
}
