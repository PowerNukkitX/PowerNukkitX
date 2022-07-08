package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.Vector3;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class BaseMoveExecutor implements IBehaviorExecutor {
    protected void move(Vector3 vec){

    };
    protected void look(Vector3 vec){

    };
    protected void jump(Vector3 vec){

    };
}
