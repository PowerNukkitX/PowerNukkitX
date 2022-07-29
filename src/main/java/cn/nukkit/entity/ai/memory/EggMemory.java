package cn.nukkit.entity.ai.memory;

import cn.nukkit.Server;

/**
 * @author LT_Name
 */
public class EggMemory extends UniversalTimedMemory {

    public EggMemory() {
        time = Server.getInstance().getTick();
    }

}
