package cn.nukkit.command.defaults;

import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.*;

import java.util.List;

/**
 * @author Pub4Game and milkice
 * @since 2015/11/12
 */
public class TeleportCommand extends VanillaCommand {
    public TeleportCommand(String name) {
        super(name, "commands.tp.description", "commands.tp.usage");
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
                CommandParameter.newEnum("facing",false, new String[]{"facing"}),
                CommandParameter.newType("lookAtPosition", CommandParamType.POSITION),
                CommandParameter.newEnum("checkForBlocks",true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("Entity->Pos(FacingEntity)", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newEnum("facing",false, new String[]{"facing"}),
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
                CommandParameter.newEnum("facing",false, new String[]{"facing"}),
                CommandParameter.newType("lookAtPosition", CommandParamType.POSITION),
                CommandParameter.newEnum("checkForBlocks",true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("->Pos(FacingEntity)", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newEnum("facing",false, new String[]{"facing"}),
                CommandParameter.newType("lookAtEntity", CommandParamType.TARGET),
                CommandParameter.newEnum("checkForBlocks",true, CommandEnum.ENUM_BOOLEAN)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender) || !sender.isEntity()) {
            return false;
        }
        CommandParser parser = new CommandParser(this,sender,args);
        try {
            String form = parser.matchCommandForm();
            if (form == null){
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
                return false;
            };
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
                        checkForBlocks = p.parseBoolean();
                    }
                    Entity victim = sender.asEntity();
                    if(checkForBlocks){
                        if(!target.getLevelBlock().isSolid() && !target.add(0,1,0).getLevelBlock().isSolid()){
                            victim.teleport(target);
                            sender.sendMessage(new TranslationContainer("commands.tp.successVictim", target.getName()));
                        }else{
                            sender.sendMessage(new TranslationContainer("commands.tp.safeTeleportFail",victim.getName(),target.getName()));
                            return false;
                        }
                    }else{
                        victim.teleport(target);
                        sender.sendMessage(new TranslationContainer("commands.tp.successVictim", target.getName()));
                    }
                    return true;
                }
                case "Entity->Entity" -> {
                    CommandParser p = new CommandParser(parser);
                    List<Entity> victims = p.parseTargets();
                    List<Entity> destination = p.parseTargets();
                    if(destination.size() != 1) {
                        return false;
                    }
                    Entity target = destination.get(0);
                    boolean checkForBlocks = false;
                    if(p.hasNext()){
                        checkForBlocks = p.parseBoolean();
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
                            return false;
                        }
                    }else{
                        for(Entity victim : victims) {
                            victim.teleport(target);
                        }
                        sender.sendMessage(new TranslationContainer("commands.tp.success", sb.toString(), target.getName()));
                    }
                    return true;
                }
                case "Entity->Pos" -> {
                    CommandParser p = new CommandParser(parser);
                    List<Entity> victims = p.parseTargets();
                    Position pos = p.parsePosition();
                    double yRot = sender.asEntity().pitch;
                    if(p.hasNext()){
                        yRot = p.parseOffsetDouble(yRot);
                    }
                    double xRot = sender.asEntity().yaw;
                    if(p.hasNext()){
                        xRot = p.parseOffsetDouble(xRot);
                    }
                    boolean checkForBlocks = false;
                    if(p.hasNext()){
                        checkForBlocks = p.parseBoolean();
                    }
                    StringBuilder sb = new StringBuilder();
                    for(Entity victim : victims) {
                        sb.append(victim.getName()).append(" ");
                    }
                    Location target = Location.fromObject(pos,pos.level,xRot,yRot);
                    if(checkForBlocks){
                        if(!target.getLevelBlock().isSolid() && !target.add(0,1,0).getLevelBlock().isSolid()){
                            for(Entity victim : victims) {
                                victim.teleport(target);
                            }
                            sender.sendMessage(new TranslationContainer("commands.tp.success.coordinates", sb.toString(), String.valueOf(target.getFloorX()),String.valueOf(target.getFloorY()),String.valueOf(target.getFloorZ())));
                        }else{
                            sender.sendMessage(new TranslationContainer("commands.tp.safeTeleportFail",sb.toString(),target.toString()));
                            return false;
                        }
                    }else{
                        for(Entity victim : victims) {
                            victim.teleport(target);
                        }
                        sender.sendMessage(new TranslationContainer("commands.tp.success.coordinates", sb.toString(), String.valueOf(target.getFloorX()),String.valueOf(target.getFloorY()),String.valueOf(target.getFloorZ())));
                    }
                    return true;
                }
                case "Entity->Pos(FacingPos)" -> {
                    CommandParser p = new CommandParser(parser);
                    List<Entity> victims = p.parseTargets();
                    Position pos = p.parsePosition();
                    p.parseString();//avoid "facing"
                    Position lookAtPosition = p.parsePosition();
                    boolean checkForBlocks = false;
                    if(p.hasNext()){
                        checkForBlocks = p.parseBoolean();
                    }
                    StringBuilder sb = new StringBuilder();
                    for(Entity victim : victims) {
                        sb.append(victim.getName()).append(" ");
                    }
                    BVector3 bv = BVector3.fromPos(new Vector3(lookAtPosition.x - pos.x, lookAtPosition.y - pos.y, lookAtPosition.z - pos.z));
                    Location target = Location.fromObject(pos,pos.level,bv.getYaw(),bv.getPitch());
                    if(checkForBlocks){
                        if(!target.getLevelBlock().isSolid() && !target.add(0,1,0).getLevelBlock().isSolid()){
                            for(Entity victim : victims) {
                                victim.teleport(target);
                            }
                            sender.sendMessage(new TranslationContainer("commands.tp.success.coordinates", sb.toString(), String.valueOf(target.getFloorX()),String.valueOf(target.getFloorY()),String.valueOf(target.getFloorZ())));
                        }else{
                            sender.sendMessage(new TranslationContainer("commands.tp.safeTeleportFail",sb.toString(),target.getFloorX() + " " + target.getFloorY() + " " + target.getFloorZ()));
                            return false;
                        }
                    }else{
                        for(Entity victim : victims) {
                            victim.teleport(target);
                        }
                        sender.sendMessage(new TranslationContainer("commands.tp.success.coordinates", sb.toString(), String.valueOf(target.getFloorX()),String.valueOf(target.getFloorY()),String.valueOf(target.getFloorZ())));
                    }
                    return true;
                }
                case "Entity->Pos(FacingEntity)" -> {
                    CommandParser p = new CommandParser(parser);
                    List<Entity> victims = p.parseTargets();
                    Position pos = p.parsePosition();
                    p.parseString();//avoid "facing"
                    List<Entity> lookAtEntity = p.parseTargets();
                    if (lookAtEntity.size() != 1){
                        return false;
                    }
                    Position lookAtPosition = lookAtEntity.get(0);
                    boolean checkForBlocks = false;
                    if(p.hasNext()){
                        checkForBlocks = p.parseBoolean();
                    }
                    StringBuilder sb = new StringBuilder();
                    for(Entity victim : victims) {
                        sb.append(victim.getName()).append(" ");
                    }
                    BVector3 bv = BVector3.fromPos(new Vector3(lookAtPosition.x - pos.x, lookAtPosition.y - pos.y, lookAtPosition.z - pos.z));
                    Location target = Location.fromObject(pos,pos.level,bv.getYaw(),bv.getPitch());
                    if(checkForBlocks){
                        if(!target.getLevelBlock().isSolid() && !target.add(0,1,0).getLevelBlock().isSolid()){
                            for(Entity victim : victims) {
                                victim.teleport(target);
                            }
                            sender.sendMessage(new TranslationContainer("commands.tp.success.coordinates", sb.toString(), String.valueOf(target.getFloorX()),String.valueOf(target.getFloorY()),String.valueOf(target.getFloorZ())));
                        }else{
                            sender.sendMessage(new TranslationContainer("commands.tp.safeTeleportFail",sb.toString(),target.toString()));
                            return false;
                        }
                    }else{
                        for(Entity victim : victims) {
                            victim.teleport(target);
                        }
                        sender.sendMessage(new TranslationContainer("commands.tp.success.coordinates", sb.toString(), String.valueOf(target.getFloorX()),String.valueOf(target.getFloorY()),String.valueOf(target.getFloorZ())));
                    }
                    return true;
                }
                case "->Pos" -> {
                    CommandParser p = new CommandParser(parser);
                    Position pos = p.parsePosition();
                    double yRot = sender.asEntity().pitch;
                    if(p.hasNext()){
                        yRot = p.parseOffsetDouble(yRot);
                    }
                    double xRot = sender.asEntity().yaw;
                    if(p.hasNext()){
                        xRot = p.parseOffsetDouble(xRot);
                    }
                    boolean checkForBlocks = false;
                    if(p.hasNext()){
                        checkForBlocks = p.parseBoolean();
                    }
                    Location target = Location.fromObject(pos,pos.level,xRot,yRot);
                    if(checkForBlocks){
                        if(!target.getLevelBlock().isSolid() && !target.add(0,1,0).getLevelBlock().isSolid()){
                            sender.asEntity().teleport(target);
                            sender.sendMessage(new TranslationContainer("commands.tp.success.coordinates", sender.getName(), String.valueOf(target.getFloorX()),String.valueOf(target.getFloorY()),String.valueOf(target.getFloorZ())));
                        }else{
                            sender.sendMessage(new TranslationContainer("commands.tp.safeTeleportFail",sender.getName(),target.toString()));
                            return false;
                        }
                    }else{
                        sender.asEntity().teleport(target);
                        sender.sendMessage(new TranslationContainer("commands.tp.success.coordinates", sender.getName(), String.valueOf(target.getFloorX()),String.valueOf(target.getFloorY()),String.valueOf(target.getFloorZ())));
                    }
                    return true;
                }
                case "->Pos(FacingPos)" -> {
                    CommandParser p = new CommandParser(parser);
                    Position pos = p.parsePosition();
                    p.parseString();//avoid "facing"
                    Position lookAtPosition = p.parsePosition();
                    boolean checkForBlocks = false;
                    if(p.hasNext()){
                        checkForBlocks = p.parseBoolean();
                    }
                    BVector3 bv = BVector3.fromPos(new Vector3(lookAtPosition.x - pos.x, lookAtPosition.y - pos.y, lookAtPosition.z - pos.z));
                    Location target = Location.fromObject(pos,pos.level,bv.getYaw(),bv.getPitch());
                    if(checkForBlocks){
                        if(!target.getLevelBlock().isSolid() && !target.add(0,1,0).getLevelBlock().isSolid()){
                            sender.asEntity().teleport(target);
                            sender.sendMessage(new TranslationContainer("commands.tp.success.coordinates", sender.asEntity().getName(), String.valueOf(target.getFloorX()),String.valueOf(target.getFloorY()),String.valueOf(target.getFloorZ())));
                        }else{
                            sender.sendMessage(new TranslationContainer("commands.tp.safeTeleportFail",sender.asEntity().getName(),target.toString()));
                            return false;
                        }
                    }else{
                        sender.asEntity().teleport(target);
                        sender.sendMessage(new TranslationContainer("commands.tp.success.coordinates", sender.asEntity().getName(), String.valueOf(target.getFloorX()),String.valueOf(target.getFloorY()),String.valueOf(target.getFloorZ())));
                    }
                    return true;
                }
                case "->Pos(FacingEntity)" -> {
                    CommandParser p = new CommandParser(parser);
                    Position pos = p.parsePosition();
                    p.parseString();//avoid "facing"
                    List<Entity> lookAtEntity = p.parseTargets();
                    if (lookAtEntity.size() != 1){
                        return false;
                    }
                    Position lookAtPosition = lookAtEntity.get(0);
                    boolean checkForBlocks = false;
                    if(p.hasNext()){
                        checkForBlocks = p.parseBoolean();
                    }
                    BVector3 bv = BVector3.fromPos(new Vector3(lookAtPosition.x - pos.x, lookAtPosition.y - pos.y, lookAtPosition.z - pos.z));
                    Location target = Location.fromObject(pos,pos.level,bv.getYaw(),bv.getPitch());
                    if(checkForBlocks){
                        if(!target.getLevelBlock().isSolid() && !target.add(0,1,0).getLevelBlock().isSolid()){
                            sender.asEntity().teleport(target);
                            sender.sendMessage(new TranslationContainer("commands.tp.success.coordinates", sender.asEntity().getName(), String.valueOf(target.getFloorX()),String.valueOf(target.getFloorY()),String.valueOf(target.getFloorZ())));
                        }else{
                            sender.sendMessage(new TranslationContainer("commands.tp.safeTeleportFail",sender.asEntity().getName(),target.toString()));
                            return false;
                        }
                    }else{
                        sender.asEntity().teleport(target);
                        sender.sendMessage(new TranslationContainer("commands.tp.success.coordinates", sender.asEntity().getName(), String.valueOf(target.getFloorX()),String.valueOf(target.getFloorY()),String.valueOf(target.getFloorZ())));
                    }
                    return true;
                }
            }
        } catch (CommandSyntaxException e) {
             sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
        }
        return false;
    }
}
