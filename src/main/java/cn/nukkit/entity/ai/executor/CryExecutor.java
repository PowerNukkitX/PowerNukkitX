package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.entity.EntityIntelligent;

public class CryExecutor implements IBehaviorExecutor {

    @Override
    public boolean execute(EntityIntelligent entity) {
        Server.getInstance().broadcastMessage(entity.getName() + " >> 114514 1919810");
        return false;
    }
}
