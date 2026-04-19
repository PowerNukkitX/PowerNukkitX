package cn.nukkit.anticheat.check.impl;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.anticheat.ICheatCheck;
import cn.nukkit.block.Block;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.level.Location;

public class NoClipCheck implements ICheatCheck {

    @Override
    public String getName() {
        return "NoClip";
    }

    @Override
    public boolean check(Player player) {
        if (!Server.getInstance().getSettings().antiCheatSettings().noclipEnabled()) return true;
        if (player.isSpectator() || player.isCreative() || !player.isAlive()) return true;

        Location to = player.getLocation();
        
        // On vérifie le bloc au niveau des jambes et de la tête
        if (isInsideSolidBlock(player, to.add(0, 0.1, 0)) || isInsideSolidBlock(player, to.add(0, 1.1, 0))) {
             onViolation(player, "Tried to move through blocks", PlayerHackDetectedEvent.HackType.NOCLIP);
             return false;
        }

        return true;
    }

    private boolean isInsideSolidBlock(Player player, Location loc) {
        Block block = loc.getLevel().getBlock(loc);
        if (block.isSolid() && !block.isTransparent()) {
            // Vérifier si le bloc a une bounding box qui contient la position
            return block.getBoundingBox() != null && block.getBoundingBox().isVectorInside(loc);
        }
        return false;
    }
}
