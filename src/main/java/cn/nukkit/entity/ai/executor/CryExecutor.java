package cn.nukkit.entity.ai.executor;

import cn.nukkit.Server;
import cn.nukkit.entity.EntityIntelligent;
import lombok.Getter;

@Getter
public class CryExecutor implements IBehaviorExecutor {

    protected String message;

    public CryExecutor(String message){
        this.message = message;
    }

    @Override
    public boolean execute(EntityIntelligent entity) {
        Server.getInstance().broadcastMessage(entity.getName() + " >> " + message);
        return false;
    }
}
