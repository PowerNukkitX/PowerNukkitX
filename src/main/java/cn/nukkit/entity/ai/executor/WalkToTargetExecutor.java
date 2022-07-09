package cn.nukkit.entity.ai.executor;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.route.*;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.SpawnParticleEffectPacket;
import lombok.Getter;

import java.util.Arrays;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
@Getter
public class WalkToTargetExecutor extends BaseMoveExecutor{

    //指示执行器应该从哪个Memory获取目标位置
    protected Class<?> memoryClazz;

    public WalkToTargetExecutor(Class<?> memoryClazz){
        this.memoryClazz = memoryClazz;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        return true;
    }
}
