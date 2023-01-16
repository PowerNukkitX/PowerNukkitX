package cn.nukkit.command.defaults;

import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.BVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.PlayerAbility;

import java.util.List;
import java.util.Map;

/**
 * @author Pub4Game and milkice
 * @since 2015/11/12
 */
public class TeleportCommand extends VanillaCommand {
    public TeleportCommand(String name) {
        super(name, "commands.tp.description", "commands.tp.usage", new String[]{"teleport"});
        this.setPermission("nukkit.command.teleport");
        this.commandParameters.clear();
        this.commandParameters.put("->Entity", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.TARGET),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("Entity->Entity", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.TARGET),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("Entity->Pos", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newType("yRot", true, CommandParamType.VALUE),
                CommandParameter.newType("xRot", true, CommandParamType.VALUE),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("Entity->Pos(FacingPos)", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newEnum("facing", false, new String[]{"facing"}),
                CommandParameter.newType("lookAtPosition", CommandParamType.POSITION),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("Entity->Pos(FacingEntity)", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.TARGET),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newEnum("facing", false, new String[]{"facing"}),
                CommandParameter.newType("lookAtEntity", CommandParamType.TARGET),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("->Pos", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newType("yRot", true, CommandParamType.VALUE),
                CommandParameter.newType("xRot", true, CommandParamType.VALUE),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("->Pos(FacingPos)", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newEnum("facing", false, new String[]{"facing"}),
                CommandParameter.newType("lookAtPosition", CommandParamType.POSITION),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("->Pos(FacingEntity)", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newEnum("facing", false, new String[]{"facing"}),
                CommandParameter.newType("lookAtEntity", CommandParamType.TARGET),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.paramTree = new ParamTree(this);
    }

    @Override
    public boolean testPermissionSilent(CommandSender target) {
        if (target.isPlayer() && target.asPlayer().getAdventureSettings().get(PlayerAbility.TELEPORT))
            return true;
        return super.testPermissionSilent(target);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
            case "->Entity" -> {
                if (!sender.isEntity()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                List<Entity> destination = list.getResult(0);
                if (destination.size() > 1) {
                    log.addError("commands.generic.tooManyTargets").output();
                    return 0;
                }
                Location victim = sender.getLocation();
                Location target = destination.get(0).setYaw(victim.getYaw()).setPitch(victim.getPitch());
                boolean checkForBlocks = false;
                if (list.hasResult(1)) {
                    checkForBlocks = list.getResult(1);
                }
                if (checkForBlocks) {
                    if (!target.getLevelBlock().isSolid() && !target.add(0, 1, 0).getLevelBlock().isSolid()) {
                        sender.asEntity().teleport(target);
                        log.addSuccess("commands.tp.successVictim", destination.get(0).getName()).output();
                    } else {
                        log.addError("commands.tp.safeTeleportFail", sender.asEntity().getName(), destination.get(0).getName()).output();
                        return 0;
                    }
                } else {
                    sender.asEntity().teleport(target);
                    log.addSuccess("commands.tp.successVictim", destination.get(0).getName()).output();
                }
                return 1;
            }
            case "Entity->Entity" -> {
                List<Entity> victims = list.getResult(0);
                List<Entity> destination = list.getResult(1);
                if (destination.size() > 1) {
                    log.addError("commands.generic.tooManyTargets").output();
                    return 0;
                }
                Entity target = destination.get(0);
                boolean checkForBlocks = false;
                if (list.hasResult(3)) {
                    checkForBlocks = list.getResult(3);
                }
                StringBuilder sb = new StringBuilder();
                for (Entity victim : victims) {
                    sb.append(victim.getName()).append(" ");
                }
                if (checkForBlocks) {
                    if (!target.getLevelBlock().isSolid() && !target.add(0, 1, 0).getLevelBlock().isSolid()) {
                        for (Entity victim : victims) {
                            victim.teleport(target.getLocation().setYaw(victim.getYaw()).setPitch(victim.getPitch()));
                        }
                        log.addSuccess("commands.tp.success", sb.toString(), target.getName());
                    } else {
                        log.addError("commands.tp.safeTeleportFail ", sb.toString(), target.getName()).output();
                        return 0;
                    }
                } else {
                    for (Entity victim : victims) {
                        victim.teleport(target.getLocation().setYaw(victim.getYaw()).setPitch(victim.getPitch()));
                    }
                    log.addSuccess("commands.tp.success", sb.toString(), target.getName());
                }
                log.output();
                return victims.size();
            }
            case "Entity->Pos" -> {
                List<Entity> victims = list.getResult(0);
                Position pos = list.getResult(1);
                double yRot = sender.getLocation().pitch;
                if (list.hasResult(2)) {
                    yRot = list.getResult(2);
                }
                double xRot = sender.getLocation().yaw;
                if (list.hasResult(3)) {
                    xRot = list.getResult(3);
                }
                boolean checkForBlocks = false;
                if (list.hasResult(4)) {
                    checkForBlocks = list.getResult(4);
                }
                StringBuilder sb = new StringBuilder();
                for (Entity victim : victims) {
                    sb.append(victim.getName()).append(" ");
                }
                Location target = Location.fromObject(pos, pos.level, xRot, yRot);
                if (checkForBlocks) {
                    if (!target.getLevelBlock().isSolid() && !target.add(0, 1, 0).getLevelBlock().isSolid()) {
                        for (Entity victim : victims) {
                            victim.teleport(target);
                        }
                        log.addSuccess("commands.tp.success.coordinates", sb.toString(), String.valueOf(target.getFloorX()), String.valueOf(target.getFloorY()), String.valueOf(target.getFloorZ()));
                    } else {
                        log.addError("commands.tp.safeTeleportFail ", sb.toString(), target.toString()).output();
                        return 0;
                    }
                } else {
                    for (Entity victim : victims) {
                        victim.teleport(target);
                    }
                    log.addSuccess("commands.tp.success.coordinates", sb.toString(), String.valueOf(target.getFloorX()), String.valueOf(target.getFloorY()), String.valueOf(target.getFloorZ()));
                }
                return 1;
            }
            case "Entity->Pos(FacingPos)" -> {
                List<Entity> victims = list.getResult(0);
                Position pos = list.getResult(1);
                Position lookAtPosition = list.getResult(2);
                boolean checkForBlocks = false;
                if (list.hasResult(3)) {
                    checkForBlocks = list.getResult(3);
                }
                StringBuilder sb = new StringBuilder();
                for (Entity victim : victims) {
                    sb.append(victim.getName()).append(" ");
                }
                BVector3 bv = BVector3.fromPos(new Vector3(lookAtPosition.x - pos.x, lookAtPosition.y - pos.y, lookAtPosition.z - pos.z));
                Location target = Location.fromObject(pos, pos.level, bv.getYaw(), bv.getPitch());
                if (checkForBlocks) {
                    if (!target.getLevelBlock().isSolid() && !target.add(0, 1, 0).getLevelBlock().isSolid()) {
                        for (Entity victim : victims) {
                            victim.teleport(target);
                        }
                        log.addSuccess("commands.tp.success.coordinates", sb.toString(), String.valueOf(target.getFloorX()), String.valueOf(target.getFloorY()), String.valueOf(target.getFloorZ()));
                    } else {
                        log.addError("commands.tp.safeTeleportFail ", sb.toString(), target.getFloorX() + " " + target.getFloorY() + " " + target.getFloorZ()).output();
                        return 0;
                    }
                } else {
                    for (Entity victim : victims) {
                        victim.teleport(target);
                    }
                    log.addSuccess("commands.tp.success.coordinates", sb.toString(), String.valueOf(target.getFloorX()), String.valueOf(target.getFloorY()), String.valueOf(target.getFloorZ()));
                }
                log.output();
                return 1;
            }
            case "Entity->Pos(FacingEntity)" -> {
                List<Entity> victims = list.getResult(0);
                if (victims.isEmpty()) {
                    log.addError("commands.generic.noTargetMatch").output();
                    return 0;
                }
                Position pos = list.getResult(1);
                List<Entity> lookAtEntity = list.getResult(3);
                if (lookAtEntity.size() != 1) {
                    return 0;
                }
                Position lookAtPosition = lookAtEntity.get(0);
                boolean checkForBlocks = false;
                if (list.hasResult(4)) {
                    checkForBlocks = list.getResult(4);
                }
                StringBuilder sb = new StringBuilder();
                for (Entity victim : victims) {
                    sb.append(victim.getName()).append(" ");
                }
                BVector3 bv = BVector3.fromPos(new Vector3(lookAtPosition.x - pos.x, lookAtPosition.y - pos.y, lookAtPosition.z - pos.z));
                Location target = Location.fromObject(pos, pos.level, bv.getYaw(), bv.getPitch());
                if (checkForBlocks) {
                    if (!target.getLevelBlock().isSolid() && !target.add(0, 1, 0).getLevelBlock().isSolid()) {
                        for (Entity victim : victims) {
                            victim.teleport(target);
                        }
                        log.addSuccess("commands.tp.success.coordinates", sb.toString(), String.valueOf(target.getFloorX()), String.valueOf(target.getFloorY()), String.valueOf(target.getFloorZ()));
                    } else {
                        log.addError("commands.tp.safeTeleportFail ", sb.toString(), target.toString()).output();
                        return 0;
                    }
                } else {
                    for (Entity victim : victims) {
                        victim.teleport(target);
                    }
                    log.addSuccess("commands.tp.success.coordinates", sb.toString(), String.valueOf(target.getFloorX()), String.valueOf(target.getFloorY()), String.valueOf(target.getFloorZ()));
                }
                log.output();
                return 1;
            }
            case "->Pos" -> {
                if (!sender.isEntity()) {
                    log.addError("commands.generic.noTargetMatch").output();
                    return 0;
                }
                Position pos = list.getResult(0);
                double yRot = sender.getLocation().pitch;
                if (list.hasResult(1)) {
                    yRot = list.getResult(1);
                }
                double xRot = sender.getLocation().yaw;
                if (list.hasResult(2)) {
                    xRot = list.getResult(2);
                }
                boolean checkForBlocks = false;
                if (list.hasResult(3)) {
                    checkForBlocks = list.getResult(3);
                }
                Location target = Location.fromObject(pos, pos.level, xRot, yRot);
                if (checkForBlocks) {
                    if (!target.getLevelBlock().isSolid() && !target.add(0, 1, 0).getLevelBlock().isSolid()) {
                        sender.asEntity().teleport(target);
                        log.addSuccess("commands.tp.success.coordinates", sender.getName(), String.valueOf(target.getFloorX()), String.valueOf(target.getFloorY()), String.valueOf(target.getFloorZ()));
                    } else {
                        log.addError("commands.tp.safeTeleportFail ", sender.getName(), target.toString()).output();
                        return 0;
                    }
                } else {
                    sender.asEntity().teleport(target);
                    log.addSuccess("commands.tp.success.coordinates", sender.getName(), String.valueOf(target.getFloorX()), String.valueOf(target.getFloorY()), String.valueOf(target.getFloorZ()));
                }
                log.output();
                return 1;
            }
            case "->Pos(FacingPos)" -> {
                if (!sender.isEntity()) {
                    log.addError("commands.generic.noTargetMatch").output();
                    return 0;
                }
                Position pos = list.getResult(0);
                Position lookAtPosition = list.getResult(2);
                boolean checkForBlocks = false;
                if (list.hasResult(3)) {
                    checkForBlocks = list.getResult(3);
                }
                BVector3 bv = BVector3.fromPos(new Vector3(lookAtPosition.x - pos.x, lookAtPosition.y - pos.y, lookAtPosition.z - pos.z));
                Location target = Location.fromObject(pos, pos.level, bv.getYaw(), bv.getPitch());
                if (checkForBlocks) {
                    if (!target.getLevelBlock().isSolid() && !target.add(0, 1, 0).getLevelBlock().isSolid()) {
                        sender.asEntity().teleport(target);
                        log.addSuccess("commands.tp.success.coordinates", sender.asEntity().getName(), String.valueOf(target.getFloorX()), String.valueOf(target.getFloorY()), String.valueOf(target.getFloorZ()));
                    } else {
                        log.addError("commands.tp.safeTeleportFail ", sender.asEntity().getName(), target.toString()).output();
                        return 0;
                    }
                } else {
                    sender.asEntity().teleport(target);
                    log.addSuccess("commands.tp.success.coordinates", sender.asEntity().getName(), String.valueOf(target.getFloorX()), String.valueOf(target.getFloorY()), String.valueOf(target.getFloorZ()));
                }
                log.output();
                return 1;
            }
            case "->Pos(FacingEntity)" -> {
                if (!sender.isEntity()) {
                    log.addError("commands.generic.noTargetMatch").output();
                    return 0;
                }
                Position pos = list.getResult(0);
                List<Entity> lookAtEntity = list.getResult(2);
                if (lookAtEntity.size() > 1) {
                    log.addError("commands.generic.tooManyTargets").output();
                    return 0;
                }
                Position lookAtPosition = lookAtEntity.get(0);
                boolean checkForBlocks = false;
                if (list.hasResult(3)) {
                    checkForBlocks = list.getResult(3);
                }
                BVector3 bv = BVector3.fromPos(new Vector3(lookAtPosition.x - pos.x, lookAtPosition.y - pos.y, lookAtPosition.z - pos.z));
                Location target = Location.fromObject(pos, pos.level, bv.getYaw(), bv.getPitch());
                if (checkForBlocks) {
                    if (!target.getLevelBlock().isSolid() && !target.add(0, 1, 0).getLevelBlock().isSolid()) {
                        sender.asEntity().teleport(target);
                        log.addSuccess("commands.tp.success.coordinates", sender.asEntity().getName(), String.valueOf(target.getFloorX()), String.valueOf(target.getFloorY()), String.valueOf(target.getFloorZ()));
                    } else {
                        log.addError("commands.tp.safeTeleportFail", sender.asEntity().getName(), target.toString()).output();
                        return 0;
                    }
                } else {
                    sender.asEntity().teleport(target);
                    log.addSuccess("commands.tp.success.coordinates", sender.asEntity().getName(), String.valueOf(target.getFloorX()), String.valueOf(target.getFloorY()), String.valueOf(target.getFloorZ()));
                }
                log.output();
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }
}
