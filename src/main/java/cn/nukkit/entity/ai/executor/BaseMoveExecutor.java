package cn.nukkit.entity.ai.executor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BVector3;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public abstract class BaseMoveExecutor implements IBehaviorExecutor {
    protected void lookAt(EntityIntelligent entity,Vector3 at){
        //构建指向玩家的向量
        BVector3 bv = BVector3.fromPos(at.x - entity.x,at.y - entity.y,at.z - entity.z);
        //BVector3会在实例化时同步计算角度，所以这里可以直接拿来用
        entity.setYaw(bv.getYaw());
        entity.setPitch(bv.getPitch());
    };
}
