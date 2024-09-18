package cn.nukkit;

import cn.nukkit.config.ServerPropertiesKeys;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;

/**
 * The Achievement class represents various achievements that players can earn.
 * It provides methods to broadcast achievements and manage the list of achievements.
 *
 * @author
 * CreeperFace
 * @since 9. 11. 2016
 */
public class Achievement {
    /**
     * A map of all achievements, keyed by their ID.
     */
    public static final HashMap<String, Achievement> achievements = new HashMap<>() {
        {
            put("mineWood", new Achievement("Getting Wood"));
            put("buildWorkBench", new Achievement("Benchmarking", "mineWood"));
            put("buildPickaxe", new Achievement("Time to Mine!", "buildWorkBench"));
            put("buildFurnace", new Achievement("Hot Topic", "buildPickaxe"));
            put("acquireIron", new Achievement("Acquire hardware", "buildFurnace"));
            put("buildHoe", new Achievement("Time to Farm!", "buildWorkBench"));
            put("makeBread", new Achievement("Bake Bread", "buildHoe"));
            put("bakeCake", new Achievement("The Lie", "buildHoe"));
            put("buildBetterPickaxe", new Achievement("Getting an Upgrade", "buildPickaxe"));
            put("buildSword", new Achievement("Time to Strike!", "buildWorkBench"));
            put("diamonds", new Achievement("DIAMONDS!", "acquireIron"));
        }
    };

    /**
     * Broadcasts an achievement to all players or to the player only, based on server settings.
     *
     * @param player The player who earned the achievement.
     * @param achievementId The ID of the achievement.
     * @return True if the achievement was successfully broadcasted, false otherwise.
     */
    public static boolean broadcast(Player player, String achievementId) {
        if (!achievements.containsKey(achievementId)) {
            return false;
        }
        String translation = Server.getInstance().getLanguage().tr("chat.type.achievement", player.getDisplayName(), TextFormat.GREEN + achievements.get(achievementId).getMessage() + TextFormat.RESET);

        if (Server.getInstance().getProperties().get(ServerPropertiesKeys.ANNOUNCE_PLAYER_ACHIEVEMENTS, true)) {
            Server.getInstance().broadcastMessage(translation);
        } else {
            player.sendMessage(translation);
        }
        return true;
    }

    /**
     * Adds a new achievement to the list of achievements.
     *
     * @param name The ID of the achievement.
     * @param achievement The Achievement object to add.
     * @return True if the achievement was successfully added, false otherwise.
     */
    public static boolean add(String name, Achievement achievement) {
        if (achievements.containsKey(name)) {
            return false;
        }

        achievements.put(name, achievement);
        return true;
    }

    /**
     * The message associated with the achievement.
     */
    public final String message;

    /**
     * The IDs of achievements that are required to earn this achievement.
     */
    public final String[] requires;

    /**
     * Constructs a new Achievement.
     *
     * @param message The message associated with the achievement.
     * @param requires The IDs of achievements that are required to earn this achievement.
     */
    public Achievement(String message, String... requires) {
        this.message = message;
        this.requires = requires;
    }

    /**
     * Gets the message associated with the achievement.
     *
     * @return The message associated with the achievement.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Broadcasts this achievement to all players or to the player only, based on server settings.
     *
     * @param player The player who earned the achievement.
     */
    public void broadcast(Player player) {
        String translation = Server.getInstance().getLanguage().tr("chat.type.achievement", player.getDisplayName(), TextFormat.GREEN + this.getMessage());

        if (Server.getInstance().getProperties().get(ServerPropertiesKeys.ANNOUNCE_PLAYER_ACHIEVEMENTS, true)) {
            Server.getInstance().broadcastMessage(translation);
        } else {
            player.sendMessage(translation);
        }
    }
}