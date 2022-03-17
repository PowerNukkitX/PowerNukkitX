package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Position;
import cn.nukkit.utils.EntitySelector;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Snake1999
 * @since 2016/1/22
 */
public class XpCommand extends Command {
    public XpCommand(String name) {
        super(name, "%nukkit.command.xp.description", "%commands.xp.usage");
        this.setPermission("nukkit.command.xp");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("amount", CommandParamType.INT),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
        this.commandParameters.put("level", new CommandParameter[]{
                CommandParameter.newPostfix("amount", "l"),
                CommandParameter.newType("player", true, CommandParamType.TARGET)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        //  "/xp <amount> [player]"  for adding exp
        //  "/xp <amount>L [player]" for adding exp level
        String amountString;
        String playerName;
        List<Entity> players;
        if (!(sender instanceof Player)) {
            if (args.length != 2) {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return true;
            }

            List<Entity> entities = null;
            if (EntitySelector.hasArguments(args[1])) {
                entities = EntitySelector.matchEntities(new Position(0, 0, 0, Server.getInstance().getDefaultLevel()), args[1]);
            } else if (sender.getServer().getPlayer(args[1]) != null){
                entities.set(0, sender.getServer().getPlayer(args[1]));
            }

            players = entities.stream().filter(entity -> entity instanceof Player).toList();
            if (players.size() == 0) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return false;
            }
            amountString = args[0];
        } else {
            if (args.length == 1) {
                amountString = args[0];
                players = new ArrayList<>();
                players.add((Entity) sender);
            } else if (args.length == 2) {
                amountString = args[0];
                List<Entity> entities = null;
                if (EntitySelector.hasArguments(args[1])) {
                    if (sender.isPlayer()) {
                        entities = EntitySelector.matchEntities((Player) sender, args[1]);
                    } else {
                        entities = EntitySelector.matchEntities(new Position(0, 0, 0, Server.getInstance().getDefaultLevel()), args[1]);
                    }
                } else if(sender.getServer().getPlayer(args[1]) != null){
                    entities.set(0, sender.getServer().getPlayer(args[1]));
                }

                players = entities.stream().filter(entity -> entity instanceof Player).toList();
                if (players.size() == 0) {
                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                    return false;
                }
            } else {
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                return true;
            }
        }


        int amount;
        boolean isLevel = false;
        if (amountString.endsWith("l") || amountString.endsWith("L")) {
            amountString = amountString.substring(0, amountString.length() - 1);
            isLevel = true;
        }

        try {
            amount = Integer.parseInt(amountString);
        } catch (NumberFormatException e1) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }

        for (Entity entity : players) {
            Player player = (Player) entity;
            if (isLevel) {
                int newLevel = player.getExperienceLevel();
                newLevel += amount;
                if (newLevel > 24791) newLevel = 24791;
                if (newLevel < 0) {
                    player.setExperience(0, 0);
                } else {
                    player.setExperience(player.getExperience(), newLevel, true);
                }
                if (amount > 0) {
                    sender.sendMessage(new TranslationContainer("commands.xp.success.levels", String.valueOf(amount), player.getName()));
                } else {
                    sender.sendMessage(new TranslationContainer("commands.xp.success.levels.minus", String.valueOf(-amount), player.getName()));
                }
                return true;
            } else {
                if (amount < 0) {
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                    return false;
                }
                player.addExperience(amount);
                sender.sendMessage(new TranslationContainer("commands.xp.success", String.valueOf(amount), player.getName()));
                return true;
            }
        }
        return false;
    }
}
