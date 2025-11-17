package cn.nukkit.level;

import java.util.Optional;

public enum GameRule {
    COMMAND_BLOCK_OUTPUT("commandBlockOutput"),
    COMMAND_BLOCKS_ENABLED("commandBlocksEnabled"),
    DO_DAYLIGHT_CYCLE("doDaylightCycle"),
    DO_ENTITY_DROPS("doEntityDrops"),
    DO_FIRE_TICK("doFireTick"),
    DO_IMMEDIATE_RESPAWN("doImmediateRespawn"),
    DO_INSOMNIA("doInsomnia"),
    DO_LIMITED_CRAFTING("doLimitedCrafting"),
    DO_MOB_LOOT("doMobLoot"),
    DO_MOB_SPAWNING("doMobSpawning"),
    DO_TILE_DROPS("doTileDrops"),
    DO_WEATHER_CYCLE("doWeatherCycle"),
    DROWNING_DAMAGE("drowningDamage"),
    EXPERIMENTAL_GAMEPLAY("experimentalGameplay"),
    FALL_DAMAGE("fallDamage"),
    FIRE_DAMAGE("fireDamage"),
    FREEZE_DAMAGE("freezeDamage"),
    FUNCTION_COMMAND_LIMIT("functionCommandLimit"),
    KEEP_INVENTORY("keepInventory"),
    LOCATOR_BAR("locatorBar"),
    MAX_COMMAND_CHAIN_LENGTH("maxCommandChainLength"),
    MOB_GRIEFING("mobGriefing"),
    NATURAL_REGENERATION("naturalRegeneration"),
    PLAYERS_SLEEPING_PERCENTAGE("playersSleepingPercentage"),
    PROJECTILES_CAN_BREAK_BLOCKS("projectilesCanBreakBlocks"),
    PVP("pvp"),
    RANDOM_TICK_SPEED("randomTickSpeed"),
    RECIPES_UNLOCK("recipesUnlock"),
    RESPAWN_BLOCKS_EXPLODE("respawnBlocksExplode"),
    SEND_COMMAND_FEEDBACK("sendCommandFeedback"),
    SHOW_BORDER_EFFECT("showBorderEffect"),
    SHOW_COORDINATES("showCoordinates"),
    SHOW_DAYS_PLAYED("showDaysPlayed"),
    SHOW_DEATH_MESSAGES("showDeathMessages"),
    SHOW_DEATH_MESSAGE(SHOW_DEATH_MESSAGES.name, true),
    SHOW_TAGS("showTags"),
    SPAWN_RADIUS("spawnRadius"),
    TNT_EXPLODES("tntExplodes"),
    TNT_EXPLOSION_DROP_DECAY("tntExplosionDropDecay");


    public static final GameRule[] EMPTY_ARRAY = new GameRule[0];
    private final String name;
    private final boolean deprecated;

    GameRule(String name) {
        this.name = name;
        this.deprecated = false;
    }

    GameRule(String name, boolean deprecated) {
        this.name = name;
        this.deprecated = deprecated;
    }

    public static Optional<GameRule> parseString(String gameRuleString) {
        //Backward compatibility
        if ("showDeathMessage".equalsIgnoreCase(gameRuleString)) {
            gameRuleString = "showDeathMessages";
        }

        for (GameRule gameRule : values()) {
            if (gameRule.getName().equalsIgnoreCase(gameRuleString)) {
                return Optional.of(gameRule);
            }
        }
        return Optional.empty();
    }

    public static String[] getNames() {
        String[] stringValues = new String[values().length];

        for (int i = 0; i < values().length; i++) {
            stringValues[i] = values()[i].getName();
        }
        return stringValues;
    }

    public String getName() {
        return name;
    }

    public boolean isDeprecated() {
        return deprecated;
    }
}
