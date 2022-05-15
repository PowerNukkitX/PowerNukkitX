package cn.nukkit.entity.ai.action;

import cn.nukkit.entity.EntityIntelligent;

/**
 * 用于异步发起同步实体操作的函数式接口
 */
public interface Action {
    void run(int currentTick, EntityIntelligent entity);
}
