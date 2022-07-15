package cn.nukkit.entity.ai.memory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class NearestPlayerMemory extends Vector3Memory {

    public NearestPlayerMemory(Player player){
        super(player);
    }

    @Override
    public Player getData() {
        return (Player) super.getData();
    }
}
