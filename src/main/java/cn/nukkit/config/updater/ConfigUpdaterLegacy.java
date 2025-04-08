package cn.nukkit.config.updater;

import cn.nukkit.Server;
import cn.nukkit.config.ServerSettings;
import cn.nukkit.config.YamlSnakeYamlConfigurer;
import cn.nukkit.config.category.BaseSettings;
import cn.nukkit.config.category.ChunkSettings;
import cn.nukkit.config.category.DebugSettings;
import cn.nukkit.config.category.GameplaySettings;
import cn.nukkit.config.category.LevelSettings;
import cn.nukkit.config.category.MiscSettings;
import cn.nukkit.config.category.NetworkSettings;
import cn.nukkit.config.category.PerformanceSettings;
import cn.nukkit.config.category.PlayerSettings;
import cn.nukkit.config.legacy.LegacyServerProperties;
import cn.nukkit.config.legacy.LegacyServerPropertiesKeys;
import cn.nukkit.config.legacy.LegacyServerSettings;
import cn.nukkit.lang.BaseLang;
import eu.okaeri.configs.ConfigManager;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.lang.reflect.Field;

@Slf4j
public class ConfigUpdaterLegacy implements ConfigUpdater.Updater {
    private final String version = "2.0.0";

    @Override
    public int getVersion() {
        return Integer.parseInt(version.replaceAll("\\.", ""));
    }

    @Override
    public void update(Server server) {

        try {
            //For the settings comments, we need a language first. Otherwise, we will get an error. That is why we're injecting a default language.
            Field baseLang = server.getClass().getDeclaredField("baseLang");
            baseLang.setAccessible(true);
            baseLang.set(server, new BaseLang("eng"));
            baseLang.setAccessible(false);

            //We need the new server settings. Therefore, we're generating the default settings here and injecting them into the server.
            Field settings = server.getClass().getDeclaredField("settings");
            settings.setAccessible(true);
            settings.set(server, ConfigManager.create(ServerSettings.class, it -> {
                File config = new File(server.getDataPath() + "pnx.yml");
                it.withConfigurer(new YamlSnakeYamlConfigurer());
                it.withBindFile(config);
                it.withRemoveOrphans(true);
                it.saveDefaults();
                it.load(true);
            })
            );
            settings.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        LegacyServerSettings legacyNukkit = ConfigManager.create(LegacyServerSettings.class, it -> {
            it.withConfigurer(new YamlSnakeYamlConfigurer());
            it.withBindFile(new File(server.getDataPath() + "nukkit.yml"));
            it.withRemoveOrphans(true);
            it.saveDefaults();
            it.load(true);
        });

        ServerSettings settings = server.getSettings();

        BaseSettings base = settings.baseSettings();
        LegacyServerSettings.BaseSettings baseOld = legacyNukkit.baseSettings();
        base.language(baseOld.language())
                .autosaveDelay(baseOld.autosave())
                .saveUnknownBlock(baseOld.saveUnknownBlock())
                .forceServerTranslate(baseOld.forceServerTranslate())
                .safeSpawn(baseOld.safeSpawn())
                .waterdogpe(baseOld.waterdogpe());

        ChunkSettings chunk = settings.chunkSettings();
        LegacyServerSettings.ChunkSettings chunkOld = legacyNukkit.chunkSettings();
        chunk.perTickSend(chunkOld.perTickSend())
                .spawnThreshold(chunkOld.spawnThreshold())
                .chunksPerTicks(chunkOld.chunksPerTicks())
                .tickRadius(chunkOld.tickRadius())
                .lightUpdates(chunkOld.lightUpdates())
                .clearTickList(chunkOld.clearTickList())
                .generationQueueSize(chunkOld.generationQueueSize());

        DebugSettings debug = settings.debugSettings();
        LegacyServerSettings.DebugSettings debugOld = legacyNukkit.debugSettings();
        debug.deprecatedVerbose(legacyNukkit.baseSettings().deprecatedVerbose())
                .level(debugOld.level())
                .command(debugOld.command())
                .ignoredPackets(debugOld.ignoredPackets());

        GameplaySettings game = settings.gameplaySettings();
        LegacyServerSettings.GameplaySettings gameOld = legacyNukkit.gameplaySettings();
        game.enableCommandBlocks(gameOld.enableCommandBlocks())
                .allowBeta(debugOld.allowBeta())
                .enableRedstone(legacyNukkit.levelSettings().enableRedstone())
                .tickRedstone(legacyNukkit.levelSettings().tickRedstone());

        LevelSettings level = settings.levelSettings();
        LegacyServerSettings.LevelSettings levelOld = legacyNukkit.levelSettings();
        level.levelThread(levelOld.levelThread())
                .autoTickRate(levelOld.autoTickRate())
                .autoTickRateLimit(levelOld.autoTickRateLimit())
                .baseTickRate(levelOld.baseTickRate())
                .alwaysTickPlayers(levelOld.alwaysTickPlayers())
                .chunkUnloadDelay(levelOld.chunkUnloadDelay());

        MiscSettings misc = settings.miscSettings();
        misc.shutdownMessage(baseOld.shutdownMessage())
                .installSpark(baseOld.installSpark());

        NetworkSettings net = settings.networkSettings();
        LegacyServerSettings.NetworkSettings netOld = legacyNukkit.networkSettings();
        net.compressionLevel(netOld.compressionLevel())
                .zlibProvider(netOld.zlibProvider())
                .snappy(netOld.snappy())
                .compressionBufferSize(netOld.compressionBufferSize())
                .maxDecompressSize(netOld.maxDecompressSize())
                .packetLimit(netOld.packetLimit())
                .queryPlugins(baseOld.queryPlugins());

        PerformanceSettings perf = settings.performanceSettings();
        LegacyServerSettings.FreezeArraySettings perfOld = legacyNukkit.freezeArraySettings();
        perf.asyncWorkers(baseOld.asyncWorkers())
                .enable(perfOld.enable())
                .slots(perfOld.slots())
                .defaultTemperature(perfOld.defaultTemperature())
                .freezingPoint(perfOld.freezingPoint())
                .boilingPoint(perfOld.boilingPoint())
                .absoluteZero(perfOld.absoluteZero())
                .melting(perfOld.melting())
                .singleOperation(perfOld.singleOperation())
                .batchOperation(perfOld.batchOperation());

        PlayerSettings pl = settings.playerSettings();
        LegacyServerSettings.PlayerSettings plOld = legacyNukkit.playerSettings();
        pl.savePlayerData(plOld.savePlayerData())
                .skinChangeCooldown(plOld.skinChangeCooldown())
                .forceSkinTrusted(plOld.forceSkinTrusted())
                .checkMovement(plOld.checkMovement())
                .spawnRadius(plOld.spawnRadius());

        LegacyServerProperties oldProp = new LegacyServerProperties(server.getDataPath());
        base.motd(oldProp.get(LegacyServerPropertiesKeys.MOTD, base.motd()))
                .subMotd(oldProp.get(LegacyServerPropertiesKeys.SUB_MOTD, base.subMotd()))
                .ip(oldProp.get(LegacyServerPropertiesKeys.SERVER_IP, base.ip()))
                .port(oldProp.get(LegacyServerPropertiesKeys.SERVER_PORT, base.port()))
                .maxPlayers(oldProp.get(LegacyServerPropertiesKeys.MAX_PLAYERS, base.maxPlayers()))
                .defaultLevelName(oldProp.get(LegacyServerPropertiesKeys.LEVEL_NAME, base.defaultLevelName()))
                .allowList(oldProp.get(LegacyServerPropertiesKeys.WHITE_LIST, base.allowList()))
                .xboxAuth(oldProp.get(LegacyServerPropertiesKeys.XBOX_AUTH, base.xboxAuth()));

        game.viewDistance(oldProp.get(LegacyServerPropertiesKeys.VIEW_DISTANCE, game.viewDistance()))
                .achievements(oldProp.get(LegacyServerPropertiesKeys.ACHIEVEMENTS, game.achievements()))
                .announceAchievements(oldProp.get(LegacyServerPropertiesKeys.ANNOUNCE_PLAYER_ACHIEVEMENTS, game.announceAchievements()))
                .spawnProtection(oldProp.get(LegacyServerPropertiesKeys.SPAWN_PROTECTION, game.spawnProtection()))
                .allowNether(oldProp.get(LegacyServerPropertiesKeys.ALLOW_NETHER, game.allowNether()))
                .allowTheEnd(oldProp.get(LegacyServerPropertiesKeys.ALLOW_THE_END, game.allowTheEnd()))
                .gamemode(parseGamemode(oldProp, game.gamemode()))
                .forceGamemode(oldProp.get(LegacyServerPropertiesKeys.FORCE_GAMEMODE, game.forceGamemode()))
                .hardcore(oldProp.get(LegacyServerPropertiesKeys.HARDCORE, game.hardcore()))
                .pvp(oldProp.get(LegacyServerPropertiesKeys.PVP, game.pvp()))
                .difficulty(oldProp.get(LegacyServerPropertiesKeys.DIFFICULTY, game.difficulty()))
                .forceResources(oldProp.get(LegacyServerPropertiesKeys.FORCE_RESOURCES, game.forceResources()))
                .allowClientPacks(oldProp.get(LegacyServerPropertiesKeys.FORCE_RESOURCES_ALLOW_CLIENT_PACKS, game.allowClientPacks()))
                .serverAuthoritativeMovement(oldProp.get(LegacyServerPropertiesKeys.SERVER_AUTHORITATIVE_MOVEMENT, game.serverAuthoritativeMovement()));

        misc.enableTerra(oldProp.get(LegacyServerPropertiesKeys.USE_TERRA, misc.enableTerra()));

        net.enableQuery(oldProp.get(LegacyServerPropertiesKeys.ENABLE_QUERY, net.enableQuery()))
                .networkEncryption(oldProp.get(LegacyServerPropertiesKeys.NETWORK_ENCRYPTION, net.networkEncryption()))
                .checkLoginTime(oldProp.get(LegacyServerPropertiesKeys.CHECK_LOGIN_TIME, net.checkLoginTime()));
        settings.save();
    }

    public int parseGamemode(LegacyServerProperties properties, int def) {
        try {
            return properties.get(LegacyServerPropertiesKeys.GAMEMODE, def) & 0b11;
        } catch (NumberFormatException exception) {
            return Server.getGamemodeFromString(properties.get(LegacyServerPropertiesKeys.GAMEMODE, Server.getGamemodeString(def))) & 0b11;
        }
    }
}
