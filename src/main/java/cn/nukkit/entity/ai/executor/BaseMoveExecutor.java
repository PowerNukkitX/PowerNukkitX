package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BVector3;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class BaseMoveExecutor implements IBehaviorExecutor {
    protected void move(EntityIntelligent entity,Vector3 motion){
        entity.motionX += motion.x;
        entity.motionY += motion.y;
        entity.motionZ += motion.z;
    };
    protected void lookAt(EntityIntelligent entity,Vector3 at){
        BVector3 bv = BVector3.fromPos(at.x - entity.x,at.y - entity.y,at.z - entity.z);
        entity.setYaw(bv.getYaw());
        entity.setPitch(bv.getPitch());
    };
}
