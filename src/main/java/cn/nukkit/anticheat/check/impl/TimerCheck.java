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
    private final Map<UUID, Double> balance = new HashMap<>();

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
            double currentBalance = balance.getOrDefault(uuid, 0.0);
            
            // Chaque tick de mouvement client devrait prendre ~50ms
            // On ajoute 50ms au "budget" attendu, et on retire le temps réel écoulé
            // Si le joueur envoie des paquets trop vite, le balance augmente.
            // On autorise un petit buffer pour le jitter réseau.
            
            currentBalance += 50.0;
            currentBalance -= diff;
            
            // Buffer de 100ms pour tolérer le jitter (2 ticks)
            if (currentBalance > 150) {
                 onViolation(player, "Timer modification detected (balance: " + (int)currentBalance + "ms)", PlayerHackDetectedEvent.HackType.TIMER);
                 // Reset pour éviter le spam si non kické immédiatement
                 balance.put(uuid, 0.0);
                 lastUpdate.put(uuid, now);
                 return false;
            }
            
            // On ne laisse pas le balance devenir trop négatif (si le joueur lag)
            // pour ne pas qu'il puisse "accélérer" après pour rattraper le temps.
            if (currentBalance < -100) {
                currentBalance = -100;
            }
            balance.put(uuid, currentBalance);
        }

        lastUpdate.put(uuid, now);
        return true;
    }
}
