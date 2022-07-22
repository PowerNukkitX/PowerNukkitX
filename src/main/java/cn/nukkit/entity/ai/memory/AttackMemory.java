package cn.nukkit.entity.ai.memory;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.entity.EntityDamageEvent;
import lombok.Getter;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class AttackMemory implements IMemory<EntityDamageEvent>{

    protected EntityDamageEvent event;
    protected int attackTime;//gt

    public AttackMemory(EntityDamageEvent event) {
        this.event = event;
        this.attackTime = Server.getInstance().getTick();
    }

    @Override
    public EntityDamageEvent getData() {
        return event;
    }
}
