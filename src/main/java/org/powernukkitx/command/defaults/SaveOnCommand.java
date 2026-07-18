package org.powernukkitx.command.defaults;

import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;

import java.util.Map;

/**
 * @author xtypr
 * @since 2015/11/13
 */
public class SaveOnCommand extends VanillaCommand {

    public SaveOnCommand(String name) {
        super(name, "Enable auto saving");//no translation in client
        this.setPermission("nukkit.command.save.enable");
        this.commandParameters.clear();
        this.commandParameters.put("default", CommandParameter.EMPTY_ARRAY);
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        sender.getServer().setAutoSave(true);
        log.addSuccess("commands.save.enabled").output(true);
        return 1;
    }
}
