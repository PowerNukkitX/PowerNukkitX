package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Exclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class GameplaySettings extends OkaeriConfig {
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.enablecommandblocks")
    boolean enableCommandBlocks = true;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.allowbeta")
    boolean allowBeta = false;
    @Comment("pnx.settings.gameplay.enableredstone")
    boolean enableRedstone = true;
    @Comment("pnx.settings.gameplay.tickRedstone")
    boolean tickRedstone = true;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.viewDistance")
    int viewDistance = 8;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.achivements")
    boolean achievements = true;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.announceAchievements")
    boolean announceAchievements = true;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.spawnProtection")
    int spawnProtection = 16;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.gamemode")
    int gamemode = 0;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.forceGamemode")
    boolean forceGamemode = false;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.hardcore")
    boolean hardcore = false;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.pvp")
    boolean pvp = true;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.difficulty")
    int difficulty = 1;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.allowNether")
    boolean allowNether = true;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.allowEnd")
    boolean allowTheEnd = true;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.forceResources")
    boolean forceResources = false;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.allowClientPacks")
    boolean allowClientPacks = true;
    @Exclude // Configured in server.properties
    @Comment("pnx.settings.gameplay.serverAuthoritativeMovement")
    String serverAuthoritativeMovement = "server-auth";
    @Comment("pnx.settings.gameplay.allowVibrantVisuals")
    boolean allowVibrantVisuals = true;
    @Comment("pnx.settings.gameplay.experiments")
    ArrayList<String> experiments = new ArrayList<>(List.of(
            "data_driven_biomes",
            "experimental_creator_cameras",
            "gametest",
            "jigsaw_structures",
            "upcoming_creator_features",
            "villager_trades_rebalance"
    ));
    @Comment("pnx.settings.gameplay.cacheStructures")
    boolean cacheStructures = false;
}