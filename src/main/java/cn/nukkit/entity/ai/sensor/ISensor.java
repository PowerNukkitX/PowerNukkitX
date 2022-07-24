package cn.nukkit.entity.ai.sensor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemory;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * 此接口抽象了一个传感器 <br/>
 * 传感器用于搜集环境信息并向记忆存储器{@link cn.nukkit.entity.ai.memory.IMemoryStorage}写入一个记忆{@link IMemory}
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface ISensor {

    /**
     * @param entity 目标实体
     */
    void sense(EntityIntelligent entity);
}
