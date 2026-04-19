package cn.nukkit.anticheat.check.impl;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.anticheat.ICheatCheck;
import cn.nukkit.event.player.PlayerHackDetectedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AutoClickerCheck implements ICheatCheck {

    private final Map<UUID, Long> lastSecond = new HashMap<>();
    private final Map<UUID, Integer> clicks = new HashMap<>();

    @Override
    public String getName() {
        return "AutoClicker";
    }

    @Override
    public boolean check(Player player) {
        // This method might be called for movement, we need to integrate it with interaction
        // For now, let's keep it as a placeholder or implement it via a separate hook if needed
        // But for PowerNukkitX anti-cheat architecture I started, check(Player) is called in handleMovement.
        // AutoClicker is better checked during packets.
        return true;
    }

    public void recordClick(Player player) {
        if (!Server.getInstance().getSettings().antiCheatSettings().autoclickerEnabled()) return;
        
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        
        if (!lastSecond.containsKey(uuid) || now - lastSecond.get(uuid) > 1000) {
            lastSecond.put(uuid, now);
            clicks.put(uuid, 1);
        } else {
            int count = clicks.getOrDefault(uuid, 0) + 1;
            clicks.put(uuid, count);
            
            int maxCps = Server.getInstance().getSettings().antiCheatSettings().autoclickerMaxCps();
            if (count > maxCps) {
                onViolation(player, "AutoClicker detected (" + count + " CPS, max: " + maxCps + ")", PlayerHackDetectedEvent.HackType.AUTOCLICKER);
            }
        }
    }
}
