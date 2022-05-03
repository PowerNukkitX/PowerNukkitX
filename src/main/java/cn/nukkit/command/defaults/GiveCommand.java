package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.command.EntitySelector;
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
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));

            return true;
        }

        List<Entity> entities = List.of();
        if (EntitySelector.hasArguments(args[0])) {
            entities = EntitySelector.matchEntities(sender, args[0]);
        } else if(sender.getServer().getPlayer(args[0]) != null){
            entities = List.of(sender.getServer().getPlayer(args[0]));
        }

        List<Player> players = entities.stream().filter(entity -> entity instanceof Player).map(p -> (Player)p).toList();
        Item item;

        try {
            item = Item.fromString(args[1]);
        } catch (Exception e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return true;
        }
        
        if (item.getDamage() < 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return true;
        }
        
        if (item instanceof ItemBlock && item.getBlock() instanceof BlockUnknown) {
            sender.sendMessage(new TranslationContainer("commands.give.block.notFound", args[1]));
            return true;
        }

        int count;
        try {
            if (args.length <= 2) {
                count = 1;
            } else {
                count = Integer.parseInt(args[2]);
            }
        } catch (NumberFormatException e) {
            count = 1;
        }
        if (count <= 0) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        item.setCount(count);

        if (args.length >= 4) {
            item.setDamage(Integer.parseInt(args[3]));
        }

        if (args.length >= 5) {
            Item.ItemJsonComponents components = Item.ItemJsonComponents.fromJson(Arrays.stream(Arrays.copyOfRange(args, 4, args.length)).collect(Collectors.joining("")));
            item.readItemJsonComponents(components);
        }

        if (players.size() == 0) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return false;
        }
        
        if (item.isNull()) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.give.item.notFound", args[1]));
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

            Command.broadcastCommandMessage(sender, new TranslationContainer(
                    "%commands.give.success",
                    item.getName() + " (" + item.getNamespaceId() + (item.getDamage() != 0 ? ":" + item.getDamage() : "") + ")",
                    String.valueOf(item.getCount()),
                    players.stream().map(p -> p.getName()).collect(Collectors.joining(" "))));
            return true;
        }
        return false;
    }
}
