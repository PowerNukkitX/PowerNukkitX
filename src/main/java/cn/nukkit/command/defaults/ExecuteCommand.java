package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ExecutorCommandSender;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Location;

import java.util.List;
import java.util.Set;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ExecuteCommand extends VanillaCommand{

    public ExecuteCommand(String name) {
        super(name,"commands.execute.description", "commands.execute.usage");
        this.setPermission("nukkit.command.execute");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args, false);

        return nextSubCommand(sender, parser);
    }

    protected boolean nextSubCommand(CommandSender sender,CommandParser parser){
        try {
            switch (parser.parseString()) {
                case "as" -> {
                    List<Entity> executors = parser.parseTargets();
                    for (Entity executor : executors){
                        ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender,executor,sender.getLocation());
                        nextSubCommand(executorCommandSender, new CommandParser(parser,executorCommandSender));
                    }
                }
                case "at" -> {
                    List<Entity> locations = parser.parseTargets();
                    if (locations.isEmpty()) return false;
                    for (Location location : locations){
                        ExecutorCommandSender executorCommandSender = new ExecutorCommandSender(sender,sender.asEntity(),location);
                        nextSubCommand(executorCommandSender, new CommandParser(parser,executorCommandSender));
                    }
                }
                case "run" -> {
                    String command = parser.parseAllRemain();
                    if (command.isEmpty()) return false;
                    sender.getServer().dispatchCommand(sender, command);
                }
            }
        } catch (CommandSyntaxException e){
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        return true;
    }
}
