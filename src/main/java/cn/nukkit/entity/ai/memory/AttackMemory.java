package cn.nukkit.entity.ai.memory;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.entity.EntityDamageEvent;
import lombok.Getter;
import lombok.Setter;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class AttackMemory implements IMemory<EntityDamageEvent>{

    protected EntityDamageEvent event;

    @Getter
    @Setter
    protected int attackTime;//gt

    public AttackMemory(){
        this.event = null;
        this.attackTime = -1;
    }

    public AttackMemory(EntityDamageEvent event) {
        this.event = event;
        this.attackTime = Server.getInstance().getTick();
    }

    @Override
    public EntityDamageEvent getData() {
        return event;
    }

    @Override
    public void setData(EntityDamageEvent event) {
        this.event = event;
        this.attackTime = Server.getInstance().getTick();
    }
}
