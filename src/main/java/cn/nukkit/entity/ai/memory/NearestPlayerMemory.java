package cn.nukkit.entity.ai.memory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class NearestPlayerMemory extends PlayerMemory {

    public NearestPlayerMemory() {
        super(null);
    }

    public NearestPlayerMemory(Player player) {
        super(player);
    }
}
