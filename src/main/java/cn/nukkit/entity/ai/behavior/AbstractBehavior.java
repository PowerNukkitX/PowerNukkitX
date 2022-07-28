package cn.nukkit.entity.ai.behavior;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.Getter;
import lombok.Setter;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class AbstractBehavior implements IBehavior {
    @Getter
    @Setter
    protected BehaviorState behaviorState = BehaviorState.STOP;
}
