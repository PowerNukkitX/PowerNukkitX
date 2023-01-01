package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.entity.ai.EntityAI;
import cn.nukkit.lang.TranslationContainer;

import java.util.Arrays;

@PowerNukkitXOnly
@Since("1.19.50-r1")
public class DebugCommand extends TestCommand{
    public DebugCommand(String name) {
        super(name, "commands.debug.description");
        this.setPermission("nukkit.command.debug");
        this.commandParameters.clear();
        //生物AI debug模式开关
        this.commandParameters.put("entity", new CommandParameter[]{
                CommandParameter.newEnum("entity", new String[]{"entity"}),
                CommandParameter.newEnum("option", Arrays.stream(EntityAI.DebugOption.values()).map(option -> option.name().toLowerCase()).toList().toArray(new String[0])),
                CommandParameter.newEnum("value", false, CommandEnum.ENUM_BOOLEAN)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        if (parser.matchCommandForm() == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        try{
            switch (parser.getMatchedCommandForm()) {
                case "entity" -> {
                    parser.parseString();
                    var option = parser.parseEnum(EntityAI.DebugOption.class);
                    EntityAI.setDebugOption(option, parser.parseBoolean());
                    sender.sendMessage("Entity AI framework " + option.name() + " debug mode have been set to: " + EntityAI.checkDebugOption(option));
                    return true;
                }
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        return true;
    }
}
