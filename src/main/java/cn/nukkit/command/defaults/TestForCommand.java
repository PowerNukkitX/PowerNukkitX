package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.CommandParser;
import cn.nukkit.utils.CommandSyntaxException;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.stream.Collectors;

public class TestForCommand extends VanillaCommand {

    public TestForCommand(String name) {
        super(name, "commands.testfor.description", "commands.testfor.usage");
        this.setPermission("nukkit.command.testfor");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("victim",false, CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Entity> targets = parser.parseTargets();

            if (targets.size() == 0) {
                sender.sendMessage(TextFormat.RED + "No targets matched selector");
                return false;
            }

            sender.sendMessage(String.format("Found %1$s", targets.stream().map(Entity::getName).collect(Collectors.joining(", "))));
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
            return false;
        }

        return true;
    }
}
