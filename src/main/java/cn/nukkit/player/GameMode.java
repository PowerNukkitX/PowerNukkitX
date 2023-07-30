package cn.nukkit.player;

import lombok.Getter;

@Getter
public enum GameMode {
    SURVIVAL("Survival", "%gameMode.survival", new String[] {"s", "0"}, 0),
    CREATIVE("Creative", "%gameMode.creative", new String[] {"c", "1"}, 1),
    ADVENTURE("Adventure", "%gameMode.adventure", new String[] {"a", "2"}, 2),
    SPECTATOR("Spectator", "%gameMode.spectator", new String[] {"spc", "view", "v", "3"}, 6);

    private final String name;
    private final String translatableName;
    private final String[] aliases;
    private final int networkGamemode;

    private static final GameMode[] VALUES = values();

    GameMode(String name, String translatableName, String[] aliases, int networkGamemode) {
        this.name = name;
        this.translatableName = translatableName;
        this.aliases = aliases;
        this.networkGamemode = networkGamemode;
    }

    public boolean isSurvival() {
        return (ordinal() & 0x01) == 0;
    }

    public static GameMode fromOrdinal(int ordinal) {
        return VALUES.length > ordinal ? VALUES[ordinal] : null;
    }

    public static GameMode fromNetwork(int network) {
        for (GameMode mode : VALUES) {
            if (mode.getNetworkGamemode() == network) {
                return mode;
            }
        }
        return null;
    }

    public static GameMode fromString(String gamemode) {
        for (GameMode mode : VALUES) {
            if (mode.getName().equalsIgnoreCase(gamemode)) {
                return mode;
            }
            for (String alias : mode.getAliases()) {
                if (alias.equalsIgnoreCase(gamemode)) {
                    return mode;
                }
            }
        }
        return null;
    }
}
