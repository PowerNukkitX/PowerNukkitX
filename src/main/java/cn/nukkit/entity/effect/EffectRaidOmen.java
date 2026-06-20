package cn.nukkit.entity.effect;

import java.awt.*;

/**
 * The transient Raid Omen effect displayed when a player's Bad Omen converts into a village raid.
 */
public class EffectRaidOmen extends Effect {

    public EffectRaidOmen() {
        super(EffectType.RAID_OMEN, "%effect.raidOmen", new Color(182, 17, 56), true);
    }
}
