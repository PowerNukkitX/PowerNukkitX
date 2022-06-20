package cn.nukkit.scoreboard.interfaces;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.scoreboard.data.ScorerType;

/**
 * the class which can be used as a key in a scoreboard
 */

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface Scorer {
    ScorerType getScorerType();

    String getName();
}
