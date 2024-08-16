package cn.nukkit.plugin;

import cn.nukkit.event.Listener;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InternalPlugin extends PluginBase {
    public static final InternalPlugin INSTANCE = new InternalPlugin();

    @Override
    public String getName() {
        return "PowerNukkitX";
    }
}
