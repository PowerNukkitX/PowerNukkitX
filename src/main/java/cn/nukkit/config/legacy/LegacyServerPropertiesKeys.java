package cn.nukkit.config.legacy;

public enum LegacyServerPropertiesKeys {
    MOTD("motd"),
    SUB_MOTD("sub-motd"),
    SERVER_PORT("server-port"),
    SERVER_IP("server-ip"),
    VIEW_DISTANCE("view-distance"),
    WHITE_LIST("white-list"),
    ACHIEVEMENTS("achievements"),
    ANNOUNCE_PLAYER_ACHIEVEMENTS("announce-player-achievements"),
    SPAWN_PROTECTION("spawn-protection"),
    MAX_PLAYERS("max-players"),
    ALLOW_FLIGHT("allow-flight"),
    SPAWN_ANIMALS("spawn-animals"),
    SPAWN_MOBS("spawn-mobs"),
    GAMEMODE("gamemode"),
    FORCE_GAMEMODE("force-gamemode"),
    HARDCORE("hardcore"),
    PVP("pvp"),
    DIFFICULTY("difficulty"),
    LEVEL_NAME("level-name"),
    LEVEL_SEED("level-seed"),
    ALLOW_NETHER("allow-nether"),
    ALLOW_THE_END("allow-the_end"),
    USE_TERRA("use-terra"),
    ENABLE_QUERY("enable-query"),
    ENABLE_RCON("enable-rcon"),
    RCON_PASSWORD("rcon.password"),
    AUTO_SAVE("auto-save"),
    FORCE_RESOURCES("force-resources"),
    FORCE_RESOURCES_ALLOW_CLIENT_PACKS("force-resources-allow-client-packs"),
    XBOX_AUTH("xbox-auth"),
    CHECK_LOGIN_TIME("check-login-time"),
    SERVER_AUTHORITATIVE_MOVEMENT("server-authoritative-movement"),
    NETWORK_ENCRYPTION("network-encryption");

    private final String key;

    LegacyServerPropertiesKeys(String key) {
        this.key = key;
    }

    @Override
    public String toString() {
        return key;
    }
}