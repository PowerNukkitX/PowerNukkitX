package cn.nukkit.plugin;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InternalPlugin extends PluginBase {
    public static final InternalPlugin INSTANCE = new InternalPlugin();
}
