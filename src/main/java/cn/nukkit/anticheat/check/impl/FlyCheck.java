package cn.nukkit.anticheat.check.impl;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.anticheat.ICheatCheck;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlyCheck implements ICheatCheck {

    private final Map<UUID, Integer> inAirTicks = new HashMap<>();

    @Override
    public String getName() {
        return "Fly";
    }

    @Override
    public boolean check(Player player) {
        if (!cn.nukkit.Server.getInstance().getSettings().antiCheatSettings().flyEnabled()) return true;
        
        if (player.getAdventureSettings().get(AdventureSettings.Type.FLYING) || 
            player.getAllowFlight() || 
            player.getGamemode() == Player.CREATIVE || 
            player.getGamemode() == Player.SPECTATOR ||
            player.isSpectator() ||
            player.getRiding() != null ||
            player.isGliding() ||
            player.isSwimming() ||
            player.isTouchingWater() ||
            player.isInsideOfWater()) {
            inAirTicks.remove(player.getUniqueId());
            return true;
        }

        if (player.isOnGround() || isNearGround(player)) {
            inAirTicks.remove(player.getUniqueId());
            return true;
        }

        int ticks = inAirTicks.getOrDefault(player.getUniqueId(), 0) + 1;
        inAirTicks.put(player.getUniqueId(), ticks);

        int threshold = cn.nukkit.Server.getInstance().getSettings().antiCheatSettings().flyThreshold();
        if (ticks > threshold) {
            onViolation(player, "Flying detected", PlayerHackDetectedEvent.HackType.FLIGHT);
            inAirTicks.remove(player.getUniqueId());
            return false;
        }

        return true;
    }

    private boolean isNearGround(Player player) {
        Vector3 pos = player.getPosition();
        Level level = player.getLevel();
        for (double x = -0.3; x <= 0.3; x += 0.3) {
            for (double z = -0.3; z <= 0.3; z += 0.3) {
                if (!level.getBlock(pos.add(x, -0.5, z)).canPassThrough()) {
                    return true;
                }
            }
        }
        return false;
    }
}
