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

        if (args.length < 4){
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        List<Entity> entities = null;
        if(EntitySelector.hasArguments(args[0])){
            entities = EntitySelector.matchEntities(sender,args[0]);
        }else{
            entities = Collections.singletonList(Server.getInstance().getPlayer(args[0]));
        }

        for (Entity entity : entities) {
            Location pos = entity.getLocation();
            boolean usePos = false;
            for(int i = 1;i <= 3;i++){
                if(!args[i].equals("~")){
                    usePos = true;
                    switch (i) {
                        case 1 -> pos.x = Double.parseDouble(args[i]);
                        case 2 -> pos.y = Double.parseDouble(args[i]);
                        case 3 -> pos.z = Double.parseDouble(args[i]);
                    }
                }
            }
            CommandSender executeSender = new ExecutorCommandSender(sender,entity,usePos ? pos : null);
            StringBuilder executedCommand = new StringBuilder();
            for(int i = 4;i < args.length;i++){
                executedCommand.append(args[i]).append(" ");
            }
            if(!Server.getInstance().dispatchCommand(executeSender,executedCommand.toString())){
                sender.sendMessage(new TranslationContainer("commands.generic.exception"));
                return false;
            }
        }
        return true;
    }
}
