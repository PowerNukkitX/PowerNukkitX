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
    private final Map<UUID, Double> balance = new HashMap<>();
    private final Map<UUID, Long> lastCheckTime = new HashMap<>();

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
            balance.remove(player.getUniqueId());
            lastCheckTime.remove(player.getUniqueId());
            return true;
        }

        Vector3 currentPos = player.getPosition();
        UUID uuid = player.getUniqueId();
        Vector3 lastPos = lastPositions.get(uuid);
        long now = System.currentTimeMillis();
        Long lastTime = lastCheckTime.get(uuid);
        
        lastPositions.put(uuid, currentPos);
        lastCheckTime.put(uuid, now);

        if (lastPos == null || lastTime == null) {
            return true;
        }

        double distance = currentPos.distance(lastPos);
        if (distance <= 0.0001) { // Joueur immobile
            return true;
        }

        long timeDiff = now - lastTime;
        if (timeDiff <= 0) { // Devrait être impossible avec System.currentTimeMillis() mais par sécurité
            return true;
        }

        double maxDistancePerTick = cn.nukkit.Server.getInstance().getSettings().antiCheatSettings().speedThreshold();

        if (player.isSprinting()) {
            maxDistancePerTick *= 1.5;
        }
        
        if (player.isSwimming() || player.isInsideOfWater()) {
            maxDistancePerTick *= 0.8;
        }

        // On ajuste maxDistancePerTick en fonction du temps réellement écoulé entre les paquets
        // Si 100ms se sont écoulés (2 ticks), on autorise 2 * maxDistancePerTick
        double adjustedMaxDistance = maxDistancePerTick * (timeDiff / 50.0);

        double currentBalance = balance.getOrDefault(uuid, 0.0);
        currentBalance += distance;
        currentBalance -= adjustedMaxDistance;

        // On ne laisse pas la balance descendre trop bas
        if (currentBalance < -2.0) {
            currentBalance = -2.0;
        }

        // Seuil de violation augmenté de 3.0 à 4.0 pour plus de tolérance aux lags
        if (currentBalance > 4.0) {
            onViolation(player, "Moving too fast (balance: " + String.format("%.2f", currentBalance) + ", dist: " + String.format("%.2f", distance) + ")", PlayerHackDetectedEvent.HackType.SPEED);
            balance.put(uuid, 0.0); // Reset
            return false;
        }

        balance.put(uuid, currentBalance);
        return true;
    }
}
