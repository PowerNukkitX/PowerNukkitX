package cn.nukkit.entity.ai;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.EnumSet;
import java.util.Set;

/**
 * 存放一些AI框架的全局参数
 */
@PowerNukkitXOnly
@Since("1.19.50-r1")
public final class EntityAI {

    private static final Set<DebugOption> debugOptions = EnumSet.noneOf(DebugOption.class);
    private EntityAI() {/*不能实例化*/}

    public static void setDebugOption(DebugOption option, boolean open) {
        if (open) debugOptions.add(option);
        else debugOptions.remove(option);
    }

    public static boolean checkDebugOption(DebugOption option) {
        return debugOptions.contains(option);
    }

    public enum DebugOption {
        /**
         * 显示路径点
         */
        ROUTE,
        /**
         * 在生物名称tag中显示behavior状态
         */
        BEHAVIOR,
        /**
         * 允许使用木棍右击生物查询memory状态
         */
        MEMORY
    }
}
