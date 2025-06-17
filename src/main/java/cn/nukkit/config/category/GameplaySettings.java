package cn.nukkit.config.category;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

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
    @Comment("pnx.settings.gameplay.allowVibrantVisuals")
    boolean allowVibrantVisuals = true;
    @Comment("pnx.settings.gameplay.experiments")
    ArrayList<String> experiments = new ArrayList<>(List.of(
            "data_driven_items",
            "data_driven_blocks_and_items",
            "data_driven_biomes",
            "upcoming_creator_features",
            "gametest",
            "experimental_molang_features",
            "cameras"
    ));
}