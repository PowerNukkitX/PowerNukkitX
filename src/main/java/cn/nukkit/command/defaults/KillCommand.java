package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.utils.TextFormat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Pub4Game
 * @since 2015/12/08
 */
public class KillCommand extends VanillaCommand {

    public KillCommand(String name) {
        super(name, "commands.kill.description");
        this.setPermission("nukkit.command.kill.self;"
                + "nukkit.command.kill.other");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
        this.paramTree = new ParamTree(this);
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        if (result.getKey().equals("default")) {
            if (result.getValue().hasResult(0)) {
                if (!sender.hasPermission("nukkit.command.kill.other")) {
                    log.outputError(TextFormat.RED + "%nukkit.command.generic.permission");
                    return 0;
                }
                List<Entity> entities = result.getValue().getResult(0);
                entities = entities.stream().filter(entity -> {
                    if (entity instanceof Player player)
                        if (player.isCreative()) {
                            log.addMessage(TextFormat.WHITE + "%commands.kill.attemptKillPlayerCreative");
                            return false;
                        } else return true;
                    else
                        return true;
                }).toList();
                if (entities.isEmpty()) {
                    if (log.outputContainer().getMessages().isEmpty()) log.outputNoTargetMatch();
                    else log.output();
                    return 0;
                }
                for (Entity entity : entities) {
                    if (entity.getName().equals(sender.getName())) {
                        if (!sender.hasPermission("nukkit.command.kill.self")) {
                            continue;
                        }
                    }
                    if (entity instanceof Player player) {
                        EntityDamageEvent ev = new EntityDamageEvent(player, DamageCause.SUICIDE, 1000000);
                        player.attack(ev);
                    } else {
                        entity.kill();
                    }
                }
                String message = entities.stream().map(Entity::getName).collect(Collectors.joining(", "));
                log.addMessage(TextFormat.WHITE + "%commands.kill.successful", message).successCount(entities.size()).output(true);
                return entities.size();
            } else {
                if (sender.isPlayer()) {
                    if (!sender.hasPermission("nukkit.command.kill.self")) {
                        log.outputError("nukkit.command.generic.permission");
                        return 0;
                    }
                    if (sender.asPlayer().isCreative()) {
                        log.outputError(TextFormat.WHITE + "%commands.kill.attemptKillPlayerCreative");
                        return 0;
                    }
                    EntityDamageEvent ev = new EntityDamageEvent(sender.asPlayer(), DamageCause.SUICIDE, 1000000);
                    sender.asPlayer().attack(ev);
                } else {
                    log.outputError("commands.generic.usage", "\n" + this.getCommandFormatTips());
                    return 0;
                }
                return 1;
            }
        }
        return 0;
    }
}
