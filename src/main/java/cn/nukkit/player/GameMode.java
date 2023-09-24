package cn.nukkit.player;

import cn.nukkit.network.protocol.types.GameType;
import lombok.Getter;

@Getter
public enum GameMode {
    SURVIVAL("Survival", "%gameMode.survival", new String[] {"s", "0"}, GameType.SURVIVAL),
    CREATIVE("Creative", "%gameMode.creative", new String[] {"c", "1"}, GameType.CREATIVE),
    ADVENTURE("Adventure", "%gameMode.adventure", new String[] {"a", "2"}, GameType.ADVENTURE),
    SPECTATOR("Spectator", "%gameMode.spectator", new String[] {"spc", "view", "v", "3"}, GameType.ADVENTURE);

    private final String name;
    private final String translatableName;
    private final String[] aliases;
    private final int networkGamemode;

    private static final GameMode[] VALUES = values();

    GameMode(String name, String translatableName, String[] aliases, GameType networkGamemode) {
        this.name = name;
        this.translatableName = translatableName;
        this.aliases = aliases;
        this.networkGamemode = networkGamemode.ordinal();
    }

    public boolean isSurvival() {
        return (ordinal() & 0x01) == 0;
    }

    public static GameMode fromOrdinal(int ordinal) {
        return ordinal < 0 || ordinal >= VALUES.length ? null : VALUES[ordinal];
    }

    public static GameMode fromNetwork(GameType network) {
        return fromNetwork(network.ordinal());
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
