package cn.nukkit.entity.ai.executor;

import cn.nukkit.entity.EntityIntelligent;

import java.util.function.Consumer;

/**
 * 自定义操作执行器
 *
 * @author LT_Name
 */
public class CustomActionExecutor implements IBehaviorExecutor {

    protected Consumer<EntityIntelligent> action;

    public CustomActionExecutor(Consumer<EntityIntelligent> aciton) {
        this.action = aciton;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        action.accept(entity);
        return true;
    }
}
