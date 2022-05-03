package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.PlayerOffhandInventory;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.utils.TextFormat;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class ClearCommand extends VanillaCommand {

    public ClearCommand(String name) {
        super(name, "commands.clear.description", "commands.clear.usage");
        this.setPermission("nukkit.command.clear");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("player",true, CommandParamType.TARGET),
                CommandParameter.newEnum("itemName",true, CommandEnum.ENUM_ITEM),
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
                    String itemName = parser.parseString();
                    int data = -1;

                    if (args.length > 2) {
                        data = parser.parseInt();
                        if (args.length > 3) {
                            maxCount = parser.parseInt();
                        }
                    }

                    itemName = new StringBuilder(itemName).append(":" + data).toString();

                    item = Item.fromString(itemName);
                }
            } else if (sender.isPlayer()) {
                targets = Lists.newArrayList(sender.asPlayer());
            } else {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.noTargetMatch"));
                return false;
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
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.clear.failure.no.items",target.getName()));
                    } else {
                        sender.sendMessage(new TranslationContainer("commands.clear.success",target.getName(),String.valueOf(count)));
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
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.clear.failure.no.items",target.getName()));
                        return false;
                    } else {
                        sender.sendMessage(new TranslationContainer("%commands.clear.testing",target.getName(),String.valueOf(count)));
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
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.clear.failure.no.items",target.getName()));
                        return false;
                    } else {
                        sender.sendMessage(new TranslationContainer("%commands.clear.success",target.getName(),String.valueOf(count)));
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
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.clear.failure.no.items",target.getName()));
                        return false;
                    } else {
                        sender.sendMessage(new TranslationContainer("%commands.clear.success",target.getName(),String.valueOf(maxCount - remaining)));
                    }
                }
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        return true;
    }
}
