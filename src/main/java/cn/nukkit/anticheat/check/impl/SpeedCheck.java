package cn.nukkit.anticheat.check.impl;

import cn.nukkit.Player;
import cn.nukkit.anticheat.ICheatCheck;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.math.Vector3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpeedCheck implements ICheatCheck {

    private final Map<UUID, Vector3> lastPositions = new HashMap<>();

    @Override
    public String getName() {
        return "Speed";
    }

    @Override
    public boolean check(Player player) {
        if (!cn.nukkit.Server.getInstance().getSettings().antiCheatSettings().speedEnabled()) return true;

        if (player.getGamemode() == Player.CREATIVE || 
            player.getGamemode() == Player.SPECTATOR ||
            player.isSpectator() ||
            player.getRiding() != null ||
            player.isGliding()) {
            lastPositions.remove(player.getUniqueId());
            return true;
        }

        Vector3 currentPos = player.getPosition();
        Vector3 lastPos = lastPositions.get(player.getUniqueId());
        lastPositions.put(player.getUniqueId(), currentPos);

        if (lastPos == null || player.level != player.getLevel()) {
            return true;
        }

        double distance = currentPos.distance(lastPos);
        double maxDistance = cn.nukkit.Server.getInstance().getSettings().antiCheatSettings().speedThreshold();

        if (player.isSprinting()) {
            maxDistance *= 1.3;
        }
        
        if (player.isSwimming()) {
            maxDistance *= 0.5;
        }

        if (distance > maxDistance) {
            onViolation(player, "Moving too fast (" + String.format("%.2f", distance) + " > " + String.format("%.2f", maxDistance) + ")", PlayerHackDetectedEvent.HackType.SPEED);
            return false;
        }

        return true;
    }
}
