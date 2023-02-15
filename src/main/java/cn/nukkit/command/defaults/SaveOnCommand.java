package cn.nukkit.command.defaults;

import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;

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

    @Since("1.19.60-r1")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        sender.getServer().setAutoSave(true);
        log.addSuccess("commands.save.enabled").output(true);
        return 1;
    }
}
