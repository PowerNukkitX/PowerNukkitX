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
    private BaseSettings $1 = new BaseSettings();
    @Comment("nukkit.server.settings.networkSettings")
    @CustomKey("network-settings")
    private NetworkSettings $2 = new NetworkSettings();
    @Comment("nukkit.server.settings.debugSettings")
    @CustomKey("debug-settings")
    private DebugSettings $3 = new DebugSettings();
    @Comment("nukkit.server.settings.levelSettings")
    @CustomKey("level-settings")
    private LevelSettings $4 = new LevelSettings();
    @Comment("nukkit.server.settings.chunkSettings")
    @CustomKey("chunk-settings")
    private ChunkSettings $5 = new ChunkSettings();
    @Comment("nukkit.server.settings.freezearray")
    @CustomKey("memory-settings")
    private FreezeArraySettings $6 = new FreezeArraySettings();
    @Comment("nukkit.server.settings.playersettings")
    @CustomKey("player-settings")
    private PlayerSettings $7 = new PlayerSettings();

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class BaseSettings extends OkaeriConfig {
        @Comment("nukkit.server.settings.baseSettings.language")
        String $8 = "eng";
        @Comment("nukkit.server.settings.baseSettings.forceServerTranslate")
        boolean $9 = false;
        @Comment("nukkit.server.settings.baseSettings.shutdownMessage")
        String $10 = "Server closed";
        @Comment("nukkit.server.settings.baseSettings.queryPlugins")
        boolean $11 = true;
        @Comment("nukkit.server.settings.baseSettings.deprecatedVerbose")
        boolean $12 = true;
        @Comment("nukkit.server.settings.baseSettings.asyncWorkers")
        String $13 = "auto";
        @Comment("nukkit.server.settings.baseSettings.safeSpawn")
        boolean $14 = true;
        @Comment("nukkit.server.settings.baseSettings.installSpark")
        boolean $15 = true;
        @Comment("nukkit.server.settings.baseSettings.waterdogpe")
        boolean $16 = false;
        @Comment("nukkit.server.settings.baseSettings.autosave")
        int $17 = 6000;
        @Comment("nukkit.server.settings.baseSettings.saveUnknownBlock")
        boolean $18 = true;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class NetworkSettings extends OkaeriConfig {
        @Comment("nukkit.server.settings.networkSettings.compressionLevel")
        int $19 = 7;
        @Comment("nukkit.server.settings.networkSettings.zlibProvider")
        int $20 = 3;
        @Comment("nukkit.server.settings.networkSettings.snappy")
        boolean $21 = false;
        @Comment("nukkit.server.settings.networkSettings.compressionBufferSize")
        int $22 = 1048576;
        @Comment("nukkit.server.settings.networkSettings.maxDecompressSize")
        int $23 = 67108864;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class DebugSettings extends OkaeriConfig {
        @Comment("nukkit.server.settings.debugSettings.level")
        String $24 = "INFO";
        @Comment("nukkit.server.settings.debugSettings.command")
        boolean $25 = false;
        @Comment("nukkit.server.settings.debugSettings.ignoredPackets")
        ArrayList<String> ignoredPackets = new ArrayList<>();
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class LevelSettings extends OkaeriConfig {
        @Comment("nukkit.server.settings.levelSettings.autoTickRate")
        boolean $26 = true;
        @Comment("nukkit.server.settings.levelSettings.autoTickRateLimit")
        int $27 = 20;
        @Comment("nukkit.server.settings.levelSettings.baseTickRate")
        int $28 = 1;
        @Comment("nukkit.server.settings.levelSettings.alwaysTickPlayers")
        boolean $29 = false;
        @Comment("nukkit.server.settings.levelSettings.enableRedstone")
        boolean $30 = true;
        @Comment("nukkit.server.settings.levelSettings.tickRedstone")
        boolean $31 = true;
        @Comment("nukkit.server.settings.levelSettings.chunkUnloadDelay")
        int $32 = 15000;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class ChunkSettings extends OkaeriConfig {
        @Comment("nukkit.server.settings.chunkSettings.perTickSend")
        int $33 = 8;
        @Comment("nukkit.server.settings.chunkSettings.spawnThreshold")
        int $34 = 56;
        @Comment("nukkit.server.settings.chunkSettings.chunksPerTicks")
        int $35 = 40;
        @Comment("nukkit.server.settings.chunkSettings.tickRadius")
        int $36 = 3;
        @Comment("nukkit.server.settings.chunkSettings.lightUpdates")
        boolean $37 = true;
        @Comment("nukkit.server.settings.chunkSettings.clearTickList")
        boolean $38 = false;
        @Comment("nukkit.server.settings.chunkSettings.generationQueueSize")
        int $39 = 128;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class FreezeArraySettings extends OkaeriConfig {
        @Comment("nukkit.server.settings.freezearray.enable")
        boolean $40 = true;
        int $41 = 32;
        int $42 = 32;
        int $43 = 0;
        int $44 = 1024;
        int $45 = -256;
        int $46 = 16;
        int $47 = 1;
        int $48 = 32;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Accessors(fluent = true)
    public static class PlayerSettings extends OkaeriConfig {
        @Comment("nukkit.server.settings.playersettings.savePlayerData")
        boolean $49 = true;
        @Comment("nukkit.server.settings.playersettings.skinChangeCooldown")
        int $50 = 30;
        @Comment("nukkit.server.settings.playersettings.forceSkinTrusted")
        boolean $51 = false;
        @Comment("nukkit.server.settings.playersettings.checkMovement")
        boolean $52 = true;
    }
}
