package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ExecutorCommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;

import java.util.List;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ExecuteCommandOld extends VanillaCommand {

    public ExecuteCommandOld(String name) {
        super(name, "old execute command", "commands.execute.usage");
        this.setPermission("nukkit.command.executeold");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("origin", CommandParamType.TARGET),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newType("command", CommandParamType.COMMAND)
        });
        this.commandParameters.put("detect", new CommandParameter[]{
                CommandParameter.newType("origin", CommandParamType.TARGET),
                CommandParameter.newType("position", CommandParamType.POSITION),
                CommandParameter.newEnum("detect", new String[]{"detect"}),
                CommandParameter.newType("detectPos", CommandParamType.POSITION),
                CommandParameter.newType("block", CommandParamType.INT),
                CommandParameter.newType("data", CommandParamType.INT),
                CommandParameter.newType("command", CommandParamType.COMMAND)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args, Boolean sendCommandFeedback) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Entity> entities = parser.parseTargets();
            if (entities.isEmpty()) {
                sender.sendMessage(new TranslationContainer("commands.generic.noTargetMatch"));
                return false;
            }
            CommandParser executePosParser = new CommandParser(parser);
            parser.parsePosition();//skip execute position
            String form = new CommandParser(this, sender, args).matchCommandForm();
            if (form == null) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                return false;
            }
            if (form.equals("default")) {
                String command = parser.parseString();
                for (Entity entity : entities) {
                    CommandSender executeSender = new ExecutorCommandSender(sender, entity, Location.fromObject(executePosParser.parsePosition(entity.getPosition(), false), entity.level, entity.yaw, entity.pitch));
                    if (!Server.getInstance().dispatchCommand(executeSender, command)) {
                        sender.sendMessage(new TranslationContainer("commands.execute.failed", entity.getName(), command));
                        return false;
                    }
                }
            } else {//detect
                parser.parseString();//skip "detect"
                CommandParser detectPosParser = new CommandParser(parser);
                parser.parsePosition();//skip detect position
                int blockid = parser.parseInt();
                int meta = parser.parseInt();
                String command = parser.parseString();
                for (Entity entity : entities) {
                    Position detectPos = detectPosParser.parsePosition(entity, false);
                    if (detectPos.getLevelBlock().getId() == blockid && detectPos.getLevelBlock().getDamage() == meta) {
                        CommandSender executeSender = new ExecutorCommandSender(sender, entity, Location.fromObject(executePosParser.parsePosition(entity.getPosition(), false), entity.level, entity.yaw, entity.pitch));
                        if (!Server.getInstance().dispatchCommand(executeSender, command)) {
                            sender.sendMessage(new TranslationContainer("commands.execute.failed", entity.getName(), command));
                            return false;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
    }
}
