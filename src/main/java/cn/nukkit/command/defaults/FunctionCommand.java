package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.function.Function;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.network.protocol.UpdateSoftEnumPacket;

import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class FunctionCommand extends VanillaCommand {


    public FunctionCommand(String name) {
        super(name, "commands.function.description");
        this.setPermission("nukkit.command.function");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newEnum("name", false, CommandEnum.FUNCTION_FILE)//todo 找到CommandParamType.FILE_PATH自动补全的工作原理
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        UpdateSoftEnumPacket pk = new UpdateSoftEnumPacket();
        pk.type = UpdateSoftEnumPacket.Type.SET;
        pk.name = "filepath";
        pk.values = Server.getInstance().getFunctionManager().getFunctions().keySet().stream().toList();
        Server.broadcastPacket(Server.getInstance().getOnlinePlayers().values(), pk);
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
