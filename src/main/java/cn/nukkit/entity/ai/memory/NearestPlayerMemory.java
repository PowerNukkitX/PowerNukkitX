package cn.nukkit.entity.ai.memory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class NearestPlayerMemory implements IMemory<Player> {

    protected Player player;

    public NearestPlayerMemory(Player player){
        this.player = player;
    }

    @Override
    public Player getData() {
        return this.player;
    }
}
