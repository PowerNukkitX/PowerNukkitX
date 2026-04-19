package cn.nukkit.anticheat.check.impl;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.anticheat.ICheatCheck;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.math.Vector3;
import cn.nukkit.level.MovingObjectPosition;

public class GhostHandCheck implements ICheatCheck {

    @Override
    public String getName() {
        return "GhostHand";
    }

    @Override
    public boolean check(Player player) {
        // This check is performed during block interactions
        return true;
    }

    public boolean checkInteraction(Player player, Vector3 blockPos) {
        if (!Server.getInstance().getSettings().antiCheatSettings().ghosthandEnabled()) return true;
        if (player.isSpectator() || player.isCreative()) return true;

        Vector3 eyePos = player.getPosition().add(0, player.getEyeHeight(), 0);
        // Step size of 0.5 is usually enough to detect blocks in between
        if (player.getLevel().isRayCollidingWithBlocks(eyePos.x, eyePos.y, eyePos.z, blockPos.x, blockPos.y, blockPos.z, 0.5)) {
             onViolation(player, "GhostHand detected (interaction through blocks at " + blockPos + ")", PlayerHackDetectedEvent.HackType.GHOSTHAND);
             return false;
        }
        return true;
    }
}
