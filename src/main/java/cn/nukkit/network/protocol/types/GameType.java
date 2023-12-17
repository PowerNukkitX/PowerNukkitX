package cn.nukkit.network.protocol.types;

import cn.nukkit.api.Since;


public enum GameType {
    SURVIVAL,
    CREATIVE,
    ADVENTURE,
    SURVIVAL_VIEWER,
    CREATIVE_VIEWER,
    DEFAULT,
    SPECTATOR;

    private static final GameType[] VALUES = values();

    public static GameType from(int id) {
        return VALUES[id];
    }
}
