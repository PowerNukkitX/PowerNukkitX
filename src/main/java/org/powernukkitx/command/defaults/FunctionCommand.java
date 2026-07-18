package org.powernukkitx.command.defaults;

import org.powernukkitx.Server;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandEnum;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.function.Function;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.utils.CommandLogger;

import java.util.Map;


public class FunctionCommand extends VanillaCommand {


    public FunctionCommand(String name) {
        super(name, "commands.function.description");
        this.setPermission("nukkit.command.function");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("name", false, CommandEnum.FUNCTION_FILE)//todo 找到CommandParamType.FILE_PATH自动补全的工作原理
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        if (result.getKey().equals("default")) {
            String file = list.getResult(0);
            Function function = Server.getInstance().getFunctionManager().getFunction(file);
            if (function == null) {
                log.addError("commands.function.functionNameNotFound", file).output();
                return 0;
            }
            function.dispatch(sender);
            log.addSuccess("commands.function.success", String.valueOf(function.getCommands().size())).output();
            return 1;
        }
        return 0;
    }
}
