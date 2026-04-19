package cn.nukkit.anticheat.check.impl;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.anticheat.ICheatCheck;
import cn.nukkit.event.player.PlayerHackDetectedEvent;

public class WaterFlyCheck implements ICheatCheck {

    @Override
    public String getName() {
        return "WaterFly";
    }

    @Override
    public boolean check(Player player) {
        if (!Server.getInstance().getSettings().antiCheatSettings().waterEnabled()) return true;

        if (player.getAdventureSettings().get(AdventureSettings.Type.FLYING) || 
            player.getAllowFlight() || 
            player.getGamemode() == Player.CREATIVE || 
            player.getGamemode() == Player.SPECTATOR ||
            player.getRiding() != null) {
            return true;
        }

        if (player.isSwimming() || player.isInsideOfWater()) {
            double motionX = player.motionX;
            double motionZ = player.motionZ;
            double horizontalSpeed = Math.sqrt(motionX * motionX + motionZ * motionZ);

            double limit = Server.getInstance().getSettings().antiCheatSettings().waterThreshold();
            
            if (horizontalSpeed > limit) {
                onViolation(player, "Water speed/fly detected: " + String.format("%.2f", horizontalSpeed), PlayerHackDetectedEvent.HackType.SPEED);
                return false;
            }
        }

        return true;
    }
}
