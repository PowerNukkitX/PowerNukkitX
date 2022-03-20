package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityTeleportEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.utils.CommandParser;
import cn.nukkit.utils.CommandSyntaxException;
import cn.nukkit.utils.EntitySelector;
import cn.nukkit.utils.TextFormat;

import java.util.Arrays;
import java.util.List;

/**
 * @author Pub4Game and milkice
 * @since 2015/11/12
 */
public class TeleportCommand extends VanillaCommand {
    public TeleportCommand(String name) {
        super(name, "%nukkit.command.tp.description", "%commands.tp.usage");
        this.setPermission("nukkit.command.teleport");
        this.commandParameters.clear();
        this.commandParameters.put("->Entity", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.TARGET),
                CommandParameter.newEnum("checkForBlocks",true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("Entity->Entity", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.TARGET),
                CommandParameter.newEnum("checkForBlocks",true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("Entity->Pos", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newType("yRot", true, CommandParamType.VALUE),
                CommandParameter.newType("xRot", true, CommandParamType.VALUE),
                CommandParameter.newEnum("checkForBlocks",true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("Entity->Pos(FacingPos)", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newEnum("facing",false, "facing"),
                CommandParameter.newType("lookAtPosition", CommandParamType.POSITION),
                CommandParameter.newEnum("checkForBlocks",true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("Entity->Pos(FacingEntity)", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newEnum("facing",false, "facing"),
                CommandParameter.newType("lookAtEntity", CommandParamType.TARGET),
                CommandParameter.newEnum("checkForBlocks",true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("->Pos", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newType("yRot", true, CommandParamType.VALUE),
                CommandParameter.newType("xRot", true, CommandParamType.VALUE),
                CommandParameter.newEnum("checkForBlocks",true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("->Pos(FacingPos)", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newEnum("facing",false, "facing"),
                CommandParameter.newType("lookAtPosition", CommandParamType.POSITION),
                CommandParameter.newEnum("checkForBlocks",true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("->Pos(FacingEntity)", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newEnum("facing",false, "facing"),
                CommandParameter.newType("lookAtEntity", CommandParamType.TARGET),
                CommandParameter.newEnum("checkForBlocks",true, CommandEnum.ENUM_BOOLEAN)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender) || sender.isEntity()) {
            return false;
        }
        CommandParser parser = new CommandParser(this,sender,args);
        try {
            String form = parser.matchCommandForm();
            if (form == null) return false;
            switch (form) {
                case "->Entity" -> {
                    CommandParser p = new CommandParser(parser);
                    List<Entity> destination = p.parseTargets();
                    if(destination.isEmpty()) {
                        return false;
                    }
                    Entity target = destination.get(0);
                    boolean checkForBlocks = false;
                    if(p.hasNext()){
                        checkForBlocks = parser.parseBoolean();
                    }
                    Entity victim = sender.asEntity();
                    if(checkForBlocks){
                        if(!target.getLevelBlock().isSolid() && !target.add(0,1,0).getLevelBlock().isSolid()){
                            victim.teleport(target);
                            sender.sendMessage(new TranslationContainer("commands.tp.successVictim", target.getName()));
                        }else{
                            sender.sendMessage(new TranslationContainer("commands.tp.safeTeleportFail",victim.getName(),target.getName()));
                        }
                    }else{
                        victim.teleport(target);
                        sender.sendMessage(new TranslationContainer("commands.tp.successVictim", target.getName()));
                    }
                }
                case "Entity->Entity" -> {
                    CommandParser p = new CommandParser(parser);
                    List<Entity> victims = p.parseTargets();
                    List<Entity> destination = p.parseTargets();
                    if(destination.isEmpty()) {
                        return false;
                    }
                    Entity target = destination.get(0);
                    boolean checkForBlocks = false;
                    if(p.hasNext()){
                        checkForBlocks = parser.parseBoolean();
                    }
                    StringBuilder sb = new StringBuilder();
                    for(Entity victim : victims) {
                        sb.append(victim.getName()).append(" ");
                    }
                    if(checkForBlocks){
                        if(!target.getLevelBlock().isSolid() && !target.add(0,1,0).getLevelBlock().isSolid()){
                            for(Entity victim : victims) {
                                victim.teleport(target);
                            }
                            sender.sendMessage(new TranslationContainer("commands.tp.success", sb.toString(), target.getName()));
                        }else{
                            sender.sendMessage(new TranslationContainer("commands.tp.safeTeleportFail",sb.toString(),target.getName()));
                        }
                    }else{
                        for(Entity victim : victims) {
                            victim.teleport(target);
                        }
                        sender.sendMessage(new TranslationContainer("commands.tp.success", sb.toString(), target.getName()));
                    }
                }
                case "Entity->Pos" -> {

                }
                case "Entity->Pos(FacingPos)" -> {

                }
                case "Entity->Pos(FacingEntity)" -> {

                }
                case "->Pos" -> {

                }
                case "->Pos(FacingPos)" -> {

                }
                case "->Pos(FacingEntity)" -> {

                }
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.usage", this.usageMessage));
        }
        return false;
    }
}
