package cn.nukkit.entity.ai.memory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class NearestFeedingPlayerMemory extends PlayerMemory<Player> {

    public NearestFeedingPlayerMemory() {
        super(null);
    }

    public NearestFeedingPlayerMemory(Player player) {
        super(player);
    }
}
