package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.function.Function;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

public class FunctionCommand extends VanillaCommand {
    public FunctionCommand(String name) {
        super(name,"commands.function.description");
        this.setPermission("nukkit.command.function");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
            CommandParameter.newType("name", CommandParamType.FILE_PATH)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }
        if (args.length == 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        Function function = Server.getInstance().getFunctionManager().getFunction(args[0]);
        if (function == null) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.function.functionNameNotFound", args[0]));
            return false;
        }
        function.dispatch(sender);
        sender.sendMessage(new TranslationContainer("commands.function.success",String.valueOf(function.getCommands().size())));
        return true;
    }
}
