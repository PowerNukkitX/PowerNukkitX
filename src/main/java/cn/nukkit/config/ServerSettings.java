package cn.nukkit.config;

import cn.nukkit.config.category.*;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Getter
@Accessors(fluent = true)
public final class ServerSettings extends OkaeriConfig {
    @Comment("pnx.settings.base")
    @CustomKey("settings")
    private BaseSettings baseSettings = new BaseSettings();

    @Comment("pnx.settings.player")
    @CustomKey("player-settings")
    private PlayerSettings playerSettings = new PlayerSettings();

    @Comment("pnx.settings.gameplay")
    @CustomKey("gameplay-settings")
    private GameplaySettings gameplaySettings = new GameplaySettings();

    @Comment("pnx.settings.misc")
    @CustomKey("misc-settings")
    private MiscSettings miscSettings = new MiscSettings();

    @Comment("pnx.settings.level")
    @CustomKey("level-settings")
    private LevelSettings levelSettings = new LevelSettings();

    @Comment("pnx.settings.chunk")
    @CustomKey("chunk-settings")
    private ChunkSettings chunkSettings = new ChunkSettings();

    @Comment("pnx.settings.network")
    @CustomKey("network-settings")
    private NetworkSettings networkSettings = new NetworkSettings();

    @Comment("pnx.settings.debug")
    @CustomKey("debug-settings")
    private DebugSettings debugSettings = new DebugSettings();

    @Comment("pnx.settings.performance")
    @CustomKey("performance-settings")
    private PerformanceSettings performanceSettings = new PerformanceSettings();

    @Comment("pnx.settings.version")
    @CustomKey("config")
    private ConfigSettings configSettings = new ConfigSettings();
}
