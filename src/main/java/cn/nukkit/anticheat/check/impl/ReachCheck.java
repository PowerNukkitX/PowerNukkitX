package cn.nukkit.anticheat.check.impl;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.anticheat.ICheatCheck;
import cn.nukkit.event.player.PlayerHackDetectedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReachCheck implements ICheatCheck {

    @Override
    public String getName() {
        return "Reach";
    }

    @Override
    public boolean check(Player player) {
        // Cette méthode est appelée par l'AntiCheatManager à chaque tick,
        // mais le ReachCheck est principalement événementiel.
        return true;
    }

    public void checkReach(Player player, double distance) {
        if (!Server.getInstance().getSettings().antiCheatSettings().reachEnabled()) return;

        double limit = Server.getInstance().getSettings().antiCheatSettings().reachDistance();
        if (distance > limit) {
            onViolation(player, "Reach detected: " + String.format("%.2f", distance) + " > " + limit, PlayerHackDetectedEvent.HackType.INVALID_PVP);
        }
    }
}
