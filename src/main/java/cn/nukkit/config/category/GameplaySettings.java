package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(fluent = true)
public class GameplaySettings extends OkaeriConfig {
    @Comment("pnx.settings.gameplay.enablecommandblocks")
    boolean enableCommandBlocks = true;
    @Comment("pnx.settings.gameplay.allowbeta")
    boolean allowBeta = false;
    @Comment("pnx.settings.gameplay.enableredstone")
    boolean enableRedstone = true;
    @Comment("pnx.settings.gameplay.tickRedstone")
    boolean tickRedstone = true;
    @Comment("pnx.settings.gameplay.viewDistance")
    int viewDistance = 8;
    @Comment("pnx.settings.gameplay.achivements")
    boolean achievements = true;
    @Comment("pnx.settings.gameplay.announceAchievements")
    boolean announceAchievements = true;
    @Comment("pnx.settings.gameplay.spawnProtection")
    int spawnProtection = 16;
    @Comment("pnx.settings.gameplay.allowFlight")
    boolean allowFlight = false;
    @Comment("pnx.settings.gameplay.spawnMobs")
    boolean spawnMobs = true;
    @Comment("pnx.settings.gameplay.spawnAnimals")
    boolean spawnAnimals = true;
    @Comment("pnx.settings.gameplay.gamemode")
    int gamemode = 0;
    @Comment("pnx.settings.gameplay.forceGamemode")
    boolean forceGamemode = false;
    @Comment("pnx.settings.gameplay.hardcore")
    boolean hardcore = false;
    @Comment("pnx.settings.gameplay.pvp")
    boolean pvp = true;
    @Comment("pnx.settings.gameplay.difficulty")
    int difficulty = 1;
    @Comment("pnx.settings.gameplay.allowNether")
    boolean allowNether = true;
    @Comment("pnx.settings.gameplay.allowEnd")
    boolean allowTheEnd = true;
    @Comment("pnx.settings.gameplay.forceResources")
    boolean forceResources = false;
    @Comment("pnx.settings.gameplay.allowClientPacks")
    boolean allowClientPacks = true;
    @Comment("pnx.settings.gameplay.serverAuthoritativeMovement")
    String serverAuthoritativeMovement = "server-auth";
}