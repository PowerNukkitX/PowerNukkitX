package cn.nukkit.entity.ai.sensor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemory;
import org.jetbrains.annotations.NotNull;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class RandomRoamTargetSensor implements ISensor {

    @NotNull
    @Override
    public IMemory<?> sense(EntityIntelligent entity) {
        
        return null;
    }
}
