package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.function.Function;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.Map;

public class FunctionCommand extends VanillaCommand {
    public FunctionCommand(String name) {
        super(name,"commands.function.description");
        this.setPermission("nukkit.command.function");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
            CommandParameter.newType("name", CommandParamType.FILE_PATH)
        });
        this.commandParameters.put("opera", new CommandParameter[]{
                CommandParameter.newEnum("opera", false, new String[]{"opera"}),
                CommandParameter.newEnum("type", false, new String[]{"list","reload"}),
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
        if (args.length == 1) {
            Function function = Server.getInstance().getFunctionManager().getFunction(args[0]);
            if (function == null) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.function.functionNameNotFound", args[0]));
                return false;
            }
            function.dispatch(sender);
            sender.sendMessage(new TranslationContainer("commands.function.success", String.valueOf(function.getCommands().size())));
        }
        if (args.length == 2) {
            if (!args[0].equals("opera")) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                return false;
            }
            if (args[1].equals("list")) {
                sender.sendMessage(TextFormat.GREEN + "The following functions exist\n");
                for(Map.Entry<String,Function> entry : Server.getInstance().getFunctionManager().getFunctions().entrySet()){
                    sender.sendMessage("- " + "name: '" + entry.getKey() + "',commands length: " + entry.getValue().getCommands().size() + "\n");
                }
            }
            if (args[1].equals("reload")) {
                Command.broadcastCommandMessage(sender, TextFormat.YELLOW + "Reloading functions...");
                Server.getInstance().getFunctionManager().reload();
                Command.broadcastCommandMessage(sender, TextFormat.GREEN + "Functions reloaded");
            }
        }
        return true;
    }
}
