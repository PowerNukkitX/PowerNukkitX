package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.BVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.NukkitMath;
import org.cloudburstmc.protocol.bedrock.data.AbilitiesIndex;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                CommandParameter.newType("destination", CommandParamType.SELECTION),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("Entity->Entity", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.SELECTION),
                CommandParameter.newType("destination", CommandParamType.SELECTION),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("Entity->Pos", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.SELECTION),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newType("yRot", true, CommandParamType.VAL),
                CommandParameter.newType("xRot", true, CommandParamType.VAL),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("Entity->Pos(FacingPos)", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.SELECTION),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newEnum("facing", false, new String[]{"facing"}),
                CommandParameter.newType("lookAtPosition", CommandParamType.POSITION),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("Entity->Pos(FacingEntity)", new CommandParameter[]{
                CommandParameter.newType("victim", CommandParamType.SELECTION),
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newEnum("facing", false, new String[]{"facing"}),
                CommandParameter.newType("lookAtEntity", CommandParamType.SELECTION),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.commandParameters.put("->Pos", new CommandParameter[]{
                CommandParameter.newType("destination", CommandParamType.POSITION),
                CommandParameter.newType("yRot", true, CommandParamType.VAL),
                CommandParameter.newType("xRot", true, CommandParamType.VAL),
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
                CommandParameter.newType("lookAtEntity", CommandParamType.SELECTION),
                CommandParameter.newEnum("checkForBlocks", true, CommandEnum.ENUM_BOOLEAN)
        });
        this.enableParamTree();
    }

    @Override
    public boolean testPermissionSilent(CommandSender target) {
        if (target.isPlayer() && target.asPlayer().getAdventureSettings().get(AbilitiesIndex.TELEPORT))
            return true;
        return super.testPermissionSilent(target);
    }

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
                if (destination.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                if (destination.size() > 1) {
                    log.addError("commands.generic.tooManyTargets").output();
                    return 0;
                }
                Location victim = sender.getLocation();
                Entity first = destination.getFirst();
                Location target = first.setYaw(victim.getYaw()).setPitch(victim.getPitch());
                boolean checkForBlocks = list.hasResult(1) ? list.getResult(1) : false;

                if (isUnsafe(target, checkForBlocks)) {
                    log.addError("commands.tp.safeTeleportFail", sender.isPlayer() ? sender.asPlayer().getDisplayName() : sender.getName(), first instanceof Player pl ? pl.getDisplayName() : first.getName()).output();
                    return 0;
                }

                sender.asEntity().teleport(target);
                log.addSuccess("commands.tp.successVictim", first instanceof Player pl ? pl.getDisplayName() : first.getName()).output(true);
                return 1;
            }
            case "Entity->Entity" -> {
                List<Entity> victims = list.getResult(0);
                if (victims.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                List<Entity> destination = list.getResult(1);
                if (destination.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                if (destination.size() > 1) {
                    log.addError("commands.generic.tooManyTargets").output();
                    return 0;
                }
                Entity target = destination.getFirst();
                boolean checkForBlocks = list.hasResult(2) ? list.getResult(2) : false;
                String message = victims.stream().map(Entity::getName).collect(Collectors.joining(", "));

                if (isUnsafe(target, checkForBlocks)) {
                    log.addError("commands.tp.safeTeleportFail", message, target.getName()).output();
                    return 0;
                }

                for (Entity victim : victims) {
                    victim.teleport(target.getLocation().setYaw(victim.getYaw()).setPitch(victim.getPitch()));
                }
                log.addSuccess("commands.tp.success", message, target.getName()).output(true);
                return victims.size();
            }
            case "Entity->Pos" -> {
                List<Entity> victims = list.getResult(0);
                if (victims.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                Position pos = list.getResult(1);
                double yRot = list.hasResult(2) ? list.getResult(2) : sender.getLocation().pitch;
                double xRot = list.hasResult(3) ? list.getResult(3) : sender.getLocation().yaw;
                boolean checkForBlocks = list.hasResult(4) ? list.getResult(4) : false;

                String message = victims.stream().map(Entity::getName).collect(Collectors.joining(", "));
                Location target = Location.fromObject(pos, pos.level, xRot, yRot);

                if (isUnsafe(target, checkForBlocks)) {
                    log.addError("commands.tp.safeTeleportFail", message, NukkitMath.round(target.getX(), 2) + ", " + NukkitMath.round(target.getY(), 2) + ", " + NukkitMath.round(target.getZ(), 2)).output();
                    return 0;
                }

                for (Entity victim : victims) {
                    victim.teleport(target);
                }
                log.addSuccess("commands.tp.success.coordinates", message, String.valueOf(NukkitMath.round(target.getX(), 2)), String.valueOf(NukkitMath.round(target.getY(), 2)), String.valueOf(NukkitMath.round(target.getZ(), 2))).output(true);
                return 1;
            }
            case "Entity->Pos(FacingPos)" -> {
                List<Entity> victims = list.getResult(0);
                if (victims.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                Position pos = list.getResult(1);
                Position lookAtPosition = list.getResult(3);
                boolean checkForBlocks = list.hasResult(4) ? list.getResult(4) : false;

                String message = victims.stream().map(Entity::getName).collect(Collectors.joining(", "));
                Location target = getFacingLocation(pos, lookAtPosition);

                if (isUnsafe(target, checkForBlocks)) {
                    log.addError("commands.tp.safeTeleportFail", message, NukkitMath.round(target.getX(), 2) + ", " + NukkitMath.round(target.getY(), 2) + ", " + NukkitMath.round(target.getZ(), 2)).output();
                    return 0;
                }

                for (Entity victim : victims) {
                    victim.teleport(target);
                }
                log.addSuccess("commands.tp.success.coordinates", message, String.valueOf(NukkitMath.round(target.getX(), 2)), String.valueOf(NukkitMath.round(target.getY(), 2)), String.valueOf(NukkitMath.round(target.getZ(), 2))).output(true);
                return 1;
            }
            case "Entity->Pos(FacingEntity)" -> {
                List<Entity> victims = list.getResult(0);
                if (victims.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                Position pos = list.getResult(1);
                List<Entity> lookAtEntity = list.getResult(3);
                if (lookAtEntity.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                if (lookAtEntity.size() > 1) {
                    log.addTooManyTargets().output();
                    return 0;
                }
                Position lookAtPosition = lookAtEntity.getFirst();
                boolean checkForBlocks = list.hasResult(4) ? list.getResult(4) : false;

                String message = victims.stream().map(Entity::getName).collect(Collectors.joining(", "));
                Location target = getFacingLocation(pos, lookAtPosition);

                if (isUnsafe(target, checkForBlocks)) {
                    log.addError("commands.tp.safeTeleportFail", message, NukkitMath.round(target.getX(), 2) + ", " + NukkitMath.round(target.getY(), 2) + ", " + NukkitMath.round(target.getZ(), 2)).output();
                    return 0;
                }

                for (Entity victim : victims) {
                    victim.teleport(target);
                }
                log.addSuccess("commands.tp.success.coordinates", message, String.valueOf(NukkitMath.round(target.getX(), 2)), String.valueOf(NukkitMath.round(target.getY(), 2)), String.valueOf(NukkitMath.round(target.getZ(), 2))).output(true);
                return 1;
            }
            case "->Pos" -> {
                if (!sender.isEntity()) {
                    log.addError("commands.generic.noTargetMatch").output();
                    return 0;
                }
                Position pos = list.getResult(0);
                double yRot = list.hasResult(1) ? list.getResult(1) : sender.getLocation().pitch;
                double xRot = list.hasResult(2) ? list.getResult(2) : sender.getLocation().yaw;
                boolean checkForBlocks = list.hasResult(3) ? list.getResult(3) : false;

                Location target = Location.fromObject(pos, pos.level, xRot, yRot);
                if (isUnsafe(target, checkForBlocks)) {
                    log.addError("commands.tp.safeTeleportFail", sender.getName(), NukkitMath.round(target.getX(), 2) + ", " + NukkitMath.round(target.getY(), 2) + ", " + NukkitMath.round(target.getZ(), 2)).output();
                    return 0;
                }

                sender.asEntity().teleport(target);
                log.addSuccess("commands.tp.success.coordinates", sender.getName(), String.valueOf(NukkitMath.round(target.getX(), 2)), String.valueOf(NukkitMath.round(target.getY(), 2)), String.valueOf(NukkitMath.round(target.getZ(), 2))).output(true);
                return 1;
            }
            case "->Pos(FacingPos)" -> {
                if (!sender.isEntity()) {
                    log.addError("commands.generic.noTargetMatch").output();
                    return 0;
                }
                Position pos = list.getResult(0);
                Position lookAtPosition = list.getResult(2);
                boolean checkForBlocks = list.hasResult(3) ? list.getResult(3) : false;

                Location target = getFacingLocation(pos, lookAtPosition);
                if (isUnsafe(target, checkForBlocks)) {
                    log.addError("commands.tp.safeTeleportFail", sender.asEntity().getName(), NukkitMath.round(target.getX(), 2) + ", " + NukkitMath.round(target.getY(), 2) + ", " + NukkitMath.round(target.getZ(), 2)).output();
                    return 0;
                }

                sender.asEntity().teleport(target);
                log.addSuccess("commands.tp.success.coordinates", sender.asEntity().getName(), String.valueOf(NukkitMath.round(target.getX(), 2)), String.valueOf(NukkitMath.round(target.getY(), 2)), String.valueOf(NukkitMath.round(target.getZ(), 2))).output(true);
                return 1;
            }
            case "->Pos(FacingEntity)" -> {
                if (!sender.isEntity()) {
                    log.addError("commands.generic.noTargetMatch").output();
                    return 0;
                }
                Position pos = list.getResult(0);
                List<Entity> lookAtEntity = list.getResult(2);
                if (lookAtEntity.isEmpty()) {
                    log.addNoTargetMatch().output();
                    return 0;
                }
                if (lookAtEntity.size() > 1) {
                    log.addTooManyTargets().output();
                    return 0;
                }
                Position lookAtPosition = lookAtEntity.getFirst();
                boolean checkForBlocks = list.hasResult(3) ? list.getResult(3) : false;

                Location target = getFacingLocation(pos, lookAtPosition);
                if (isUnsafe(target, checkForBlocks)) {
                    log.addError("commands.tp.safeTeleportFail", sender.asEntity().getName(), NukkitMath.round(target.getX(), 2) + ", " + NukkitMath.round(target.getY(), 2) + ", " + NukkitMath.round(target.getZ(), 2)).output();
                    return 0;
                }

                sender.asEntity().teleport(target);
                log.addSuccess("commands.tp.success.coordinates", sender.asEntity().getName(), String.valueOf(NukkitMath.round(target.getX(), 2)), String.valueOf(NukkitMath.round(target.getY(), 2)), String.valueOf(NukkitMath.round(target.getZ(), 2))).output(true);
                return 1;
            }
            default -> {
                return 0;
            }
        }
    }

    private boolean isUnsafe(Position target, boolean checkForBlocks) {
        return checkForBlocks && (target.getLevelBlock().isSolid() || target.add(0, 1, 0).getLevelBlock().isSolid());
    }

    private Location getFacingLocation(Position pos, Position lookAtPosition) {
        BVector3 bv = BVector3.fromPos(new Vector3(lookAtPosition.x - pos.x, lookAtPosition.y - pos.y, lookAtPosition.z - pos.z));
        return Location.fromObject(pos, pos.level, bv.getYaw(), bv.getPitch());
    }
}