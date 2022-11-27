package cn.nukkit.entity.ai;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

/**
 * 存放一些AI框架的全局参数
 */
@PowerNukkitXOnly
@Since("1.19.40-r4")
public final class EntityAI {
    /**
     * 是否开启生物AI框架测试模式
     */
    public static boolean DEBUG = false;
    private EntityAI() {/*不能实例化*/}
}
