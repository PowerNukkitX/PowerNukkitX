package cn.nukkit.plugin;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@PowerNukkitXOnly
@Since("1.19.50-r4")
@NoArgsConstructor(onConstructor = @__(@PowerNukkitXOnly), access = AccessLevel.PRIVATE)
public final class InternalPlugin extends PluginBase {
    public static final InternalPlugin INSTANCE = new InternalPlugin();
}
