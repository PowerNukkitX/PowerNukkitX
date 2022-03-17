package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.PlayerOffhandInventory;
import cn.nukkit.item.Item;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.CommandParser;
import cn.nukkit.utils.CommandSyntaxException;
import cn.nukkit.utils.TextFormat;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class ClearCommand extends VanillaCommand {

    public ClearCommand(String name) {
        super(name, "Clears items from player inventory.", "/clear [player: target] [itemId: int] [data: int] [maxCount: int]");
        this.setPermission("vanillacommand.clear");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("player",true, CommandParamType.TARGET),
                CommandParameter.newType("itemId",true, CommandParamType.INT),
                CommandParameter.newType("data", true,CommandParamType.INT),
                CommandParameter.newType("maxCount", true,CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            List<Player> targets;
            int maxCount = -1;

            Item item = null;

            if (args.length > 0) {
                targets = parser.parseTargetPlayers();

                if (args.length > 1) {
                    int itemId = parser.parseInt();
                    int data = -1;

                    if (args.length > 2) {
                        data = parser.parseInt();
                        if (args.length > 3) {
                            maxCount = parser.parseInt();
                        }
                    }

                    item = Item.get(itemId, data);
                }
            } else if (sender instanceof Player) {
                targets = Lists.newArrayList((Player) sender);
            } else {
                sender.sendMessage(TextFormat.RED + "No targets matched selector");
                return true;
            }

            for (Player target : targets) {
                PlayerInventory inventory = target.getInventory();
                PlayerOffhandInventory offhand = target.getOffhandInventory();

                if (item == null) {
                    int count = 0;

                    for (Map.Entry<Integer, Item> entry : inventory.getContents().entrySet()) {
                        Item slot = entry.getValue();
                        if (!slot.isNull()) {
                            count += slot.getCount();
                            inventory.clear(entry.getKey());
                        }
                    }

                    Item slot = offhand.getItem(0);
                    if (!slot.isNull()) {
                        count += slot.getCount();
                        offhand.clear(0);
                    }

                    if (count == 0) {
                        sender.sendMessage(String.format(TextFormat.RED + "Could not clear the inventory of %1$s, no items to remove", target.getName()));
                    } else {
                        sender.sendMessage(String.format("Cleared the inventory of %1$s, removing %2$d items", target.getName(), count));
                    }
                } else if (maxCount == 0) {
                    int count = 0;

                    for (Map.Entry<Integer, Item> entry : inventory.getContents().entrySet()) {
                        Item slot = entry.getValue();

                        if (item.equals(slot, item.hasMeta(), false)) {
                            count += slot.getCount();
                        }
                    }

                    Item slot = offhand.getItem(0);
                    if (item.equals(slot, item.hasMeta(), false)) {
                        count += slot.getCount();
                    }

                    if (count == 0) {
                        sender.sendMessage(String.format(TextFormat.RED + "Could not clear the inventory of %1$s, no items to remove", target.getName()));
                    } else {
                        sender.sendMessage(String.format("%1$s has %2$d items that match the criteria", target.getName(), count));
                    }
                } else if (maxCount == -1) {
                    int count = 0;

                    for (Map.Entry<Integer, Item> entry : inventory.getContents().entrySet()) {
                        Item slot = entry.getValue();

                        if (item.equals(slot, item.hasMeta(), false)) {
                            count += slot.getCount();
                            inventory.clear(entry.getKey());
                        }
                    }

                    Item slot = offhand.getItem(0);
                    if (item.equals(slot, item.hasMeta(), false)) {
                        count += slot.getCount();
                        offhand.clear(0);
                    }

                    if (count == 0) {
                        sender.sendMessage(String.format(TextFormat.RED + "Could not clear the inventory of %1$s, no items to remove", target.getName()));
                    } else {
                        sender.sendMessage(String.format("Cleared the inventory of %1$s, removing %2$d items", target.getName(), count));
                    }
                } else {
                    int remaining = maxCount;

                    for (Map.Entry<Integer, Item> entry : inventory.getContents().entrySet()) {
                        Item slot = entry.getValue();

                        if (item.equals(slot, item.hasMeta(), false)) {
                            int count = slot.getCount();
                            int amount = Math.min(count, remaining);

                            slot.setCount(count - amount);
                            inventory.setItem(entry.getKey(), slot);

                            if ((remaining -= amount) <= 0) {
                                break;
                            }
                        }
                    }

                    if (remaining > 0) {
                        Item slot = offhand.getItem(0);
                        if (item.equals(slot, item.hasMeta(), false)) {
                            int count = slot.getCount();
                            int amount = Math.min(count, remaining);

                            slot.setCount(count - amount);
                            inventory.setItem(0, slot);
                            remaining -= amount;
                        }
                    }

                    if (remaining == maxCount) {
                        sender.sendMessage(String.format(TextFormat.RED + "Could not clear the inventory of %1$s, no items to remove", target.getName()));
                    } else {
                        sender.sendMessage(String.format("Cleared the inventory of %1$s, removing %2$d items", target.getName(), maxCount - remaining));
                    }
                }
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
        }

        return true;
    }
}
