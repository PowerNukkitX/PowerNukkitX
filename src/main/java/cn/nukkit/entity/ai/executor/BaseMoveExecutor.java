package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class BaseMoveExecutor implements IBehaviorExecutor {
    protected void move(EntityIntelligent entityIntelligent,Vector3 motion){
        entityIntelligent.addMotion(motion.x,motion.y,motion.z);
    };
    protected void look(Vector3 motion){

    };
    protected void jump(Vector3 motion){

    };
}
