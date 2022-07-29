package cn.nukkit.entity.ai.memory;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class AttackTargetMemory<P extends Player> extends PlayerMemory<P>{

    public AttackTargetMemory(){
        super(null);
    }

    public AttackTargetMemory(P player) {
        super(player);
    }
}
