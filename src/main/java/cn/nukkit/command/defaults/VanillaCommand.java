package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;

/**
 * 代表原版命令的基类
 *
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class VanillaCommand extends Command {
    /**
     * @deprecated 
     */
    

    public VanillaCommand(String name) {
        super(name);
    }
    /**
     * @deprecated 
     */
    

    public VanillaCommand(String name, String description) {
        super(name, description);
    }
    /**
     * @deprecated 
     */
    

    public VanillaCommand(String name, String description, String usageMessage) {
        super(name, description, usageMessage);
    }
    /**
     * @deprecated 
     */
    

    public VanillaCommand(String name, String description, String usageMessage, String[] aliases) {
        super(name, description, usageMessage, aliases);
    }
}
