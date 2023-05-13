package cn.nukkit.entity.custom;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public interface CustomEntity {
    CustomEntityDefinition getDefinition();

    /**
     * 自定义实体设置NetworkId没有作用,默认应该设置0,想定义实体类型,应该使用{@link CustomEntityDefinition.Builder#bid(String)}来定义
     * <p>
     * Setting NetworkId for custom entities has no effect, it should be set to 0 by default, and if you want to define an entity type, you should use {@link CustomEntityDefinition.Builder#bid(String)} to define it
     */
    default int getNetworkId() {
        return 0;
    }
}
