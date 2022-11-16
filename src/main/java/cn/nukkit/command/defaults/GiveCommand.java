package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.command.utils.CommandOutputContainer;
import cn.nukkit.command.utils.EntitySelector;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
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
    //item name;item count
    public static final String RECEIVER_RAWTEXT = """
            {"rawtext":[{"translate":"commands.give.successRecipient","with":{"rawtext":[{"text":"%s"},{"text":"%d"}]}}]}
            """;
    //sender name;item name;item count;receivers name
    public static final String EXECUTE_SUCCESS_RAWTEXT = """
            {"rawtext":[{"text":"§7§o["},{"translate":"%s"},{"text":": "},{"translate":"commands.give.success","with":{"rawtext":[{"text":"%s"},{"text":"%d"},{"text":"%s"}]}},{"text":"]"}]}
            """;

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

        var log = new CommandLogger(this, sender, args);

        if (args.length < 2) {
            log.outputSyntaxErrors(args.length == 0 ? -1 : 0);
            return false;
        }

        final List<Player> players = new ArrayList<>();
        if (EntitySelector.hasArguments(args[0])) {
            EntitySelector.matchEntities(sender, args[0]).stream().filter(entity -> entity instanceof Player).map(e -> (Player) e).forEach(players::add);
        } else if (sender.getServer().getPlayer(args[0]) != null) {
            players.add(sender.getServer().getPlayer(args[0]));
        }

        if (players.size() == 0) {
            log.outputNoTargetMatch();
            return false;
        }

        Item item;
        try {
            item = Item.fromString(args[1]);
        } catch (Exception e) {
            log.outputSyntaxErrors(1);
            return false;
        }

        if (item.isNull()) {
            log.outputError(1, TextFormat.RED + "%commands.give.item.notFound", new String[]{args[1]});
            return false;
        } else if (item instanceof ItemBlock && item.getBlock() instanceof BlockUnknown) {
            log.outputError(1, "commands.give.block.notFound", new String[]{args[1]});
            return false;
        }

        int count = 1;

        if (args.length > 2) {
            try {
                count = Integer.parseInt(args[2]);
            } catch (NumberFormatException ignored) {
            }
        }

        if (count <= 0) {
            log.outputNumTooSmall(2, 1);
            return false;
        }
        item.setCount(count);

        if (args.length >= 4) {
            item.setDamage(Integer.parseInt(args[3]));
        }

        if (item.getDamage() < 0) {
            log.outputError("commands.give.item.invalid", new String[]{item.getNamespaceId().split(":")[1]});
            return true;
        }

        if (args.length >= 5) {
            Item.ItemJsonComponents components = Item.ItemJsonComponents.fromJson(String.join("", Arrays.copyOfRange(args, 4, args.length)));
            item.readItemJsonComponents(components);
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
            log.outputObjectWhisper(RECEIVER_RAWTEXT, player, item.getName(), item.getCount());
        }
        var playerV = new ArrayList<String>();
        playerV.add(item.getName());
        playerV.add(" " + count);
        playerV.addAll(players.stream().map(Player::getName).toList());
        var consoleV = new ArrayList<String>();
        consoleV.add(item.getName() + " (" + item.getNamespaceId() + (item.getDamage() != 0 ? ":" + item.getDamage() : "") + ")");
        consoleV.add(String.valueOf(item.getCount()));
        consoleV.add(players.stream().map(Player::getName).collect(Collectors.joining(", ")));
        log.outputResult(players.size(), "commands.give.success", playerV.toArray(CommandOutputContainer.EMPTY_STRING_ARRAY),
                "%commands.give.success", consoleV.toArray(CommandOutputContainer.EMPTY_STRING_ARRAY),
                EXECUTE_SUCCESS_RAWTEXT, sender.getName(), item.getName(), item.getCount(), consoleV.get(2));
        return true;
    }
}
