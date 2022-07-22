package cn.nukkit.entity.ai.sensor;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.EntityIntelligent;
import cn.nukkit.entity.ai.memory.IMemory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 此接口抽象了一个传感器 <br/>
 * 传感器用于搜集环境信息并返回一个记忆{@link IMemory}，且将会被写入记忆存储器{@link cn.nukkit.entity.ai.memory.IMemoryStorage}
 */
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface ISensor {

    /**
     * 要求传感器返回一个数据(Memory),若Memory的Data为Null则表示没有数据,MemoryStorage将会删除对应的键值对
     *
     * @param entity
     * @return IMemory
     */
    @Nonnull
    IMemory<?> sense(EntityIntelligent entity);
}
