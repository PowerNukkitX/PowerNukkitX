package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.command.exceptions.CommandSyntaxException;

import java.util.List;
import java.util.stream.Collectors;

public class TestForCommand extends VanillaCommand {

    public TestForCommand(String name) {
        super(name, "commands.testfor.description");
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
                sender.sendMessage(new TranslationContainer("commands.generic.noTargetMatch"));
                return false;
            }

            sender.sendMessage(new TranslationContainer("commands.testfor.success", targets.stream().map(Entity::getName).collect(Collectors.joining(","))));
        } catch (CommandSyntaxException e) {
             sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        return true;
    }
}
