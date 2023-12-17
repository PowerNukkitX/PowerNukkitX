package cn.nukkit.plugin;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import lombok.NoArgsConstructor;


@NoArgsConstructor(onConstructor = @__())
@Deprecated
@DeprecationDetails(since = "1.19.60-r1", reason = "replace", replaceWith = "InternalPlugin")
public class PowerNukkitPlugin extends PluginBase {
    private static final PowerNukkitPlugin INSTANCE = new PowerNukkitPlugin();


    public static PowerNukkitPlugin getInstance() {
        return INSTANCE;
    }
}
