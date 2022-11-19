package cn.nukkit.command;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

@PowerNukkitXOnly
@Since("1.19.31-r2")
public abstract class CoreCommand extends Command {
    public CoreCommand(String name) {
        super(name);
    }

    public CoreCommand(String name, String description) {
        super(name, description);
    }

    public CoreCommand(String name, String description, String usageMessage) {
        super(name, description, usageMessage);
    }

    public CoreCommand(String name, String description, String usageMessage, String[] aliases) {
        super(name, description, usageMessage, aliases);
    }
}
