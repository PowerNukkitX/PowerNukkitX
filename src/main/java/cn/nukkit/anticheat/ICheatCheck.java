package cn.nukkit.anticheat;

import cn.nukkit.Player;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.event.player.PlayerKickEvent;

public interface ICheatCheck {
    
    String getName();
    
    boolean check(Player player);
    
    default void onViolation(Player player, String reason, PlayerHackDetectedEvent.HackType type) {
        PlayerHackDetectedEvent event = new PlayerHackDetectedEvent(player, type, reason);
        player.getServer().getPluginManager().callEvent(event);
        if (event.isKick()) {
            player.kick(PlayerKickEvent.Reason.FLYING_DISABLED, reason);
        }
    }
}
