package cn.nukkit.network.protocol.types;

import cn.nukkit.api.Since;

@Since("1.3.0.0-PN")
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
