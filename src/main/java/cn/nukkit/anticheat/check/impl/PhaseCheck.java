package cn.nukkit.anticheat.check.impl;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.anticheat.ICheatCheck;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.block.Block;
import cn.nukkit.math.Vector3;

public class PhaseCheck implements ICheatCheck {

    @Override
    public String getName() {
        return "Phase";
    }

    @Override
    public boolean check(Player player) {
        if (!Server.getInstance().getSettings().antiCheatSettings().phaseEnabled()) return true;
        if (!player.isAlive() || player.isSpectator() || player.isCreative()) return true;

        Vector3 pos = player.getPosition();
        Block block = player.getLevel().getBlock(pos);

        if (block.isSolid() && !block.canPassThrough()) {
             // Basic check for standing inside a solid block
             // We allow some tolerance because players can be slightly inside a block edge
             double diffX = Math.abs(pos.x - (block.getFloorX() + 0.5));
             double diffZ = Math.abs(pos.z - (block.getFloorZ() + 0.5));
             
             if (diffX < 0.3 && diffZ < 0.3) {
                 onViolation(player, "Phase detected (inside " + block.getName() + ")", PlayerHackDetectedEvent.HackType.PHASE);
                 return false;
             }
        }
        return true;
    }
}
