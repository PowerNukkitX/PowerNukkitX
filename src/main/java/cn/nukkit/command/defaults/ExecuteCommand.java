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
import cn.nukkit.utils.EntitySelector;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ExecuteCommand extends VanillaCommand {

    public ExecuteCommand(String name) {
        super(name,"%nukkit.command.execute.description", "%commands.execute.usage");
        this.setPermission("nukkit.command.execute");
        this.commandParameters.clear();
        this.commandParameters.put("default",new CommandParameter[]{
                CommandParameter.newType("target",CommandParamType.TARGET),
                CommandParameter.newType("position",CommandParamType.POSITION),
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
            CommandParser parser2 = new CommandParser(parser);
            parser.parsePosition();//skip position
            String command = parser.parseAllRemain();
            for (Entity entity : entities) {
                CommandSender executeSender = new ExecutorCommandSender(sender,entity,Location.fromObject(parser2.parsePosition(entity,false)));
                if(!Server.getInstance().dispatchCommand(executeSender,command)){
                    sender.sendMessage(new TranslationContainer("commands.generic.exception"));
                    return false;
                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
