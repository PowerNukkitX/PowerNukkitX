package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.utils.CommandOutputContainer;
import cn.nukkit.command.utils.EntitySelector;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xtypr
 * @since 2015/12/9
 */
public class GiveCommand extends VanillaCommand {
    public GiveCommand(String name) {
        super(name, "commands.give.description");
        this.setPermission("nukkit.command.give");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("data", true, CommandParamType.INT),
                CommandParameter.newType("components", true, CommandParamType.JSON)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args, Boolean sendCommandFeedback) {
        if (!this.testPermission(sender)) {
            return false;
        }
        if (args.length < 2) {
            if (sender.isPlayer())
                sender.sendMessage(new CommandOutputContainer("commands.generic.syntax", this.getGenericSyntaxErrors(args, args.length == 0 ? -1 : 0), false, sendCommandFeedback));
            else
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        final List<Player> players = new ArrayList<>();
        if (EntitySelector.hasArguments(args[0])) {
            EntitySelector.matchEntities(sender, args[0]).stream().filter(entity -> entity instanceof Player).map(e -> (Player) e).forEach(players::add);
        } else if (sender.getServer().getPlayer(args[0]) != null) {
            players.add(sender.getServer().getPlayer(args[0]));
        }

        Item item;
        try {
            item = Item.fromString(args[1]);
        } catch (Exception e) {
            if (sender.isPlayer())
                sender.sendMessage(new CommandOutputContainer("commands.generic.syntax", this.getGenericSyntaxErrors(args, 1), false, sendCommandFeedback));
            else
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        if (item.isNull()) {
            if (sender.isPlayer())
                sender.sendMessage(new CommandOutputContainer("commands.generic.syntax", this.getGenericSyntaxErrors(args, 1), false, sendCommandFeedback));
            else
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.give.item.notFound", args[1]));
            return false;
        } else if (item instanceof ItemBlock && item.getBlock() instanceof BlockUnknown) {
            if (sender.isPlayer())
                sender.sendMessage(new CommandOutputContainer("commands.generic.syntax", this.getGenericSyntaxErrors(args, 1), false, sendCommandFeedback));
            else
                sender.sendMessage(new TranslationContainer("commands.give.block.notFound", args[1]));
            return false;
        }

        int count = 1;

        if (args.length > 2) {
            try {
                count = Integer.parseInt(args[2]);
            } catch (NumberFormatException ignored) {}
        }

        if (count <= 0) {
            if (sender.isPlayer())
                sender.sendMessage(new CommandOutputContainer("commands.generic.num.tooSmall", new String[]{args[2], " 1"}, false, sendCommandFeedback));
            else
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        item.setCount(count);

        if (args.length >= 4) {
            item.setDamage(Integer.parseInt(args[3]));
        }

        if (item.getDamage() < 0) {
            if (sender.isPlayer())
                sender.sendMessage(new CommandOutputContainer("commands.give.item.invalid", new String[]{item.getNamespaceId().split(":")[1]}, false, sendCommandFeedback));
            else
                sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return true;
        }

        if (args.length >= 5) {
            Item.ItemJsonComponents components = Item.ItemJsonComponents.fromJson(String.join("", Arrays.copyOfRange(args, 4, args.length)));
            item.readItemJsonComponents(components);
        }

        if (players.size() == 0) {
            if (sender.isPlayer())
                sender.sendMessage(new CommandOutputContainer("commands.generic.noTargetMatch", new String[]{}, false, sendCommandFeedback));
            else sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return false;
        }

        for (Player player : players) {
            Item[] returns = player.getInventory().addItem(item.clone());
            List<Item> drops = new ArrayList<>();
            for (Item returned : returns) {
                int maxStackSize = returned.getMaxStackSize();
                if (returned.getCount() <= maxStackSize) {
                    drops.add(returned);
                } else {
                    while (returned.getCount() > maxStackSize) {
                        Item drop = returned.clone();
                        int toDrop = Math.min(returned.getCount(), maxStackSize);
                        drop.setCount(toDrop);
                        returned.setCount(returned.getCount() - toDrop);
                        drops.add(drop);
                    }
                    if (!returned.isNull()) {
                        drops.add(returned);
                    }
                }
            }

            for (Item drop : drops) {
                player.dropItem(drop);
            }

            if (sender.isPlayer())
                sender.sendMessage(new CommandOutputContainer("commands.give.success", new String[]{item.getName(), " " + count, sender.getName()}, true, sendCommandFeedback));
            else {
                Command.broadcastCommandMessage(sender, new TranslationContainer(
                        "%commands.give.success",
                        item.getName() + " (" + item.getNamespaceId() + (item.getDamage() != 0 ? ":" + item.getDamage() : "") + ")",
                        String.valueOf(item.getCount()),
                        players.stream().map(Player::getName).collect(Collectors.joining(" "))));
            }
        }
        return true;
    }
}
