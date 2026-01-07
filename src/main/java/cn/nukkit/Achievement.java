package cn.nukkit;

import cn.nukkit.utils.TextFormat;

import java.util.HashMap;

/**
 * @author CreeperFace
 * @since 9/11/2016
 */
public class Achievement {

    public static final HashMap<String, Achievement> achievements;

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
        Server server = Server.getInstance();

        String name = TextFormat.GREEN + this.getMessage();

        String translation = server.getLanguage()
                .tr("chat.type.achievement", player.getDisplayName(), name);

        if (server.getSettings().gameplaySettings().announceAchievements()) {
            server.broadcastMessage(translation);
        } else {
            player.sendMessage(translation);
        }
    }

    static {
        achievements = new HashMap<>();
        achievements.put("mineWood", new Achievement("Getting Wood"));
        achievements.put("buildWorkBench", new Achievement("Benchmarking", "mineWood"));
        achievements.put("buildPickaxe", new Achievement("Time to Mine!", "buildWorkBench"));
        achievements.put("buildFurnace", new Achievement("Hot Topic", "buildPickaxe"));
        achievements.put("acquireIron", new Achievement("Acquire hardware", "buildFurnace"));
        achievements.put("buildHoe", new Achievement("Time to Farm!", "buildWorkBench"));
        achievements.put("makeBread", new Achievement("Bake Bread", "buildHoe"));
        achievements.put("bakeCake", new Achievement("The Lie", "buildHoe"));
        achievements.put("buildBetterPickaxe", new Achievement("Getting an Upgrade", "buildPickaxe"));
        achievements.put("buildSword", new Achievement("Time to Strike!", "buildWorkBench"));
        achievements.put("diamonds", new Achievement("DIAMONDS!", "acquireIron"));
    }

    public static boolean broadcast(Player player, String achievementId) {
        if (!achievements.containsKey(achievementId)) {
            return false;
        }

        Achievement achievement = achievements.get(achievementId);
        achievement.broadcast(player);

        return true;
    }

    public static boolean add(String name, Achievement achievement) {
        if (achievements.containsKey(name)) {
            return false;
        }

        achievements.put(name, achievement);
        return true;
    }
}
