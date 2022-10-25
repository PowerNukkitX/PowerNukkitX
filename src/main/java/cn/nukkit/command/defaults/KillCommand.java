package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.utils.EntitySelector;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDamageEvent.DamageCause;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.Collections;
import java.util.List;
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
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args, Boolean sendCommandFeedback) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length >= 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        if (args.length == 1) {
            if (!sender.hasPermission("nukkit.command.kill.other")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
                return true;
            }
            List<Entity> entities = EntitySelector.hasArguments(args[0]) ? EntitySelector.matchEntities(sender, args[0]) : Server.getInstance().getPlayer(args[0]) != null ? Collections.singletonList(Server.getInstance().getPlayer(args[0])) : null;
            entities = entities.stream().filter(entity -> {
                if (entity instanceof Player player)
                    return !player.isCreative();
                else
                    return true;
            }).toList();
            if (entities == null || entities.isEmpty()) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
                return false;
            }
            boolean successExecute = true;
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
            String message = entities.stream().map(entity -> entity.getName()).collect(Collectors.joining(", "));
            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.kill.successful", message));
            return true;
        }
        if (sender.isPlayer()) {
            if (!sender.hasPermission("nukkit.command.kill.self")) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
                return true;
            }
            EntityDamageEvent ev = new EntityDamageEvent(sender.asPlayer(), DamageCause.SUICIDE, 1000000);
            sender.asPlayer().attack(ev);
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        return true;
    }
}
