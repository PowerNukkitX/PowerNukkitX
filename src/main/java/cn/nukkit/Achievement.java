package cn.nukkit;

import cn.nukkit.utils.TextFormat;

import java.util.HashMap;

/**
 * @author CreeperFace
 * @since 9. 11. 2016
 */
public class Achievement {

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

    public static boolean broadcast(Player player, String achievementId) {
        if (!achievements.containsKey(achievementId)) {
            return false;
        }
        String translation = Server.getInstance().getLanguage().tr("chat.type.achievement", player.getDisplayName(), TextFormat.GREEN + achievements.get(achievementId).getMessage() + TextFormat.RESET);

        if (Server.getInstance().getSettings().gameplaySettings().announceAchievements()) {
            Server.getInstance().broadcastMessage(translation);
        } else {
            player.sendMessage(translation);
        }
        return true;
    }

    public static boolean add(String name, Achievement achievement) {
        if (achievements.containsKey(name)) {
            return false;
        }

        achievements.put(name, achievement);
        return true;
    }

    public final String message;
    public final String[] requires;

    public Achievement(String message, String... requires) {
        this.message = message;
        this.requires = requires;
    }

    public String getMessage() {
        return message;
    }

    public void broadcast(Player player) {
        String translation = Server.getInstance().getLanguage().tr("chat.type.achievement", player.getDisplayName(), TextFormat.GREEN + this.getMessage());

        if (Server.getInstance().getSettings().gameplaySettings().announceAchievements()) {
            Server.getInstance().broadcastMessage(translation);
        } else {
            player.sendMessage(translation);
        }
    }
}
