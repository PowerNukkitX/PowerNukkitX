package cn.nukkit.command;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;

import java.util.List;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public interface CommandMap {

    void registerAll(String fallbackPrefix, List<? extends Command> commands);

    boolean register(String fallbackPrefix, Command command);

    boolean register(String fallbackPrefix, Command command, String label);

    void registerSimpleCommands(Object object);

    boolean dispatch(CommandSender sender, String cmdLine);

    @PowerNukkitXOnly
    @Since("1.19.50-r4")
    int executeCommand(CommandSender sender, String cmdLine);

    void clearCommands();

    Command getCommand(String name);

}
