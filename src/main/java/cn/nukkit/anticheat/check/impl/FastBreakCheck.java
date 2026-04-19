package cn.nukkit.anticheat.check.impl;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.anticheat.ICheatCheck;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FastBreakCheck implements ICheatCheck {

    private final Map<UUID, Long> lastBreak = new HashMap<>();

    @Override
    public String getName() {
        return "FastBreak";
    }

    @Override
    public boolean check(Player player) {
        // FastBreak se vérifie normalement via BlockBreakEvent, pas via handleMovement
        return true;
    }

    public void onBlockBreak(Player player, Block block) {
        if (!Server.getInstance().getSettings().antiCheatSettings().fastbreakEnabled()) return;
        if (player.isCreative() || !player.isAlive()) return;

        long now = System.currentTimeMillis();
        UUID uuid = player.getUniqueId();

        if (lastBreak.containsKey(uuid)) {
            long diff = now - lastBreak.get(uuid);
            double breakTime = block.getBreakTime(player.getInventory().getItemInMainHand(), player);
            
            // Si le temps écoulé est beaucoup plus court que le temps théorique de cassage du bloc
            // On accorde une marge car Bedrock a des comportements de minage rapide légitimes (Insta-break etc)
            if (breakTime > 0.1 && diff < (breakTime * 1000 * 0.7)) {
                 onViolation(player, "Breaking blocks too fast: " + block.getName() + " (Time: " + diff + "ms, Expected: " + (breakTime * 1000) + "ms)", PlayerHackDetectedEvent.HackType.FAST_BREAK);
            }
        }

        lastBreak.put(uuid, now);
    }
}
