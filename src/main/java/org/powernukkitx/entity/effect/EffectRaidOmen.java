package org.powernukkitx.entity.effect;

import org.powernukkitx.Player;
import org.powernukkitx.entity.Entity;

import java.awt.*;

/**
 * Replaces the bad omen a player carries into a village. When it runs out the village the player
 * is standing in at that moment is raided. Removing it early, with milk or a command, cancels the
 * raid.
 */
public class EffectRaidOmen extends Effect {

    public EffectRaidOmen() {
        super(EffectType.RAID_OMEN, "%effect.raid_omen", new Color(222, 64, 88), true);
    }

    @Override
    public void remove(Entity entity) {
        super.remove(entity);
        if (this.getDuration() > 0) {
            return;
        }
        if (entity instanceof Player player && player.getLevel() != null) {
            player.getLevel().getVillageManager().triggerRaid(player);
        }
    }
}
