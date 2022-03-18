package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ExecutorCommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.utils.CommandParser;

import java.util.List;

public class ExecuteCommand extends VanillaCommand {

    public ExecuteCommand(String name) {
        super(name,"%nukkit.command.execute.description", "%commands.execute.usage");
        this.setPermission("nukkit.command.execute");
        this.commandParameters.clear();
        this.commandParameters.put("default",new CommandParameter[]{
                CommandParameter.newType("origin",CommandParamType.TARGET),
                CommandParameter.newType("position",CommandParamType.POSITION),
                CommandParameter.newType("command",CommandParamType.RAWTEXT)
        });
        this.commandParameters.put("detect",new CommandParameter[]{
                CommandParameter.newType("origin",CommandParamType.TARGET),
                CommandParameter.newType("position",CommandParamType.POSITION),
                CommandParameter.newEnum("detect",new String[]{"detect"}),
                CommandParameter.newType("detectPos",CommandParamType.POSITION),
                CommandParameter.newType("block",CommandParamType.INT),
                CommandParameter.newType("data",CommandParamType.INT),
                CommandParameter.newType("command",CommandParamType.RAWTEXT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        try{
            CommandParser parser = new CommandParser(this,sender, args);
            List<Entity> entities = parser.parseTargets();
            CommandParser executePosParser = new CommandParser(parser);
            parser.parsePosition();//skip execute position
            if (!parser.parseString(false).equals("detect")) {
                String command = parser.parseAllRemain();
                for (Entity entity : entities) {
                    CommandSender executeSender = new ExecutorCommandSender(sender, entity, Location.fromObject(executePosParser.parsePosition(entity, false)));
                    if (!Server.getInstance().dispatchCommand(executeSender, command)) {
                        sender.sendMessage(new TranslationContainer("commands.generic.exception"));
                        return false;
                    }
                }
            }else{
                parser.parseString();//skip "detect"
                CommandParser detectPosParser = new CommandParser(parser);
                parser.parsePosition();//skip detect position
                int blockid = parser.parseInt();
                int meta = parser.parseInt();
                String command = parser.parseAllRemain();
                for (Entity entity : entities) {
                    Position detectPos = detectPosParser.parsePosition(entity,false);
                    if (detectPos.getLevelBlock().getId() == blockid && detectPos.getLevelBlock().getDamage() == meta) {
                        CommandSender executeSender = new ExecutorCommandSender(sender, entity, Location.fromObject(executePosParser.parsePosition(entity, false)));
                        if (!Server.getInstance().dispatchCommand(executeSender, command)) {
                            sender.sendMessage(new TranslationContainer("commands.generic.exception"));
                            return false;
                        }
                        return true;
                    }else{
                        return false;
                    }
                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
