package cn.nukkit.anticheat.check.impl;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.anticheat.ICheatCheck;
import cn.nukkit.event.player.PlayerHackDetectedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerCheck implements ICheatCheck {

    private final Map<UUID, Long> lastUpdate = new HashMap<>();
    private final Map<UUID, Long> lastDiff = new HashMap<>();

    @Override
    public String getName() {
        return "Timer";
    }

    @Override
    public boolean check(Player player) {
        if (!Server.getInstance().getSettings().antiCheatSettings().timerEnabled()) return true;
        if (!player.isAlive()) return true;

        long now = System.currentTimeMillis();
        UUID uuid = player.getUniqueId();

        if (lastUpdate.containsKey(uuid)) {
            long diff = now - lastUpdate.get(uuid);
            
            // If too little time has elapsed between two check calls (moves)
            // We expect about 50 ms per tick
            if (diff < 10) { 
                 onViolation(player, "Timer modification detected (too fast: " + diff + "ms)", PlayerHackDetectedEvent.HackType.TIMER);
                 return false;
            }
        }

        lastUpdate.put(uuid, now);
        return true;
    }
}
