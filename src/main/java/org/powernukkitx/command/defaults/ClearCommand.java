package org.powernukkitx.command.defaults;

import org.powernukkitx.Player;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.data.GenericParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.tree.node.PlayersNode;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.inventory.HumanInventory;
import org.powernukkitx.inventory.HumanOffHandInventory;
import org.powernukkitx.item.Item;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public class ClearCommand extends VanillaCommand {

    public ClearCommand(String name) {
        super(name, "commands.clear.description", "commands.clear.usage");
        this.setPermission("nukkit.command.clear");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("player", true, CommandParamType.SELECTION, new PlayersNode()),
                GenericParameter.ITEM_NAME.get(true),
                CommandParameter.newType("data", true, CommandParamType.INT),
                CommandParameter.newType("maxCount", true, CommandParamType.INT)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
            var list = result.getValue();
            List<Player> targets = sender.isPlayer() ? List.of(sender.asPlayer()) : List.of();
            int maxCount = -1;
            Item item = null;

            if (list.hasResult(0)) {
                targets = list.getResult(0);
                if (list.hasResult(1)) {
                    item = list.getResult(1);
                    int data = -1;
                    if (list.hasResult(2)) {
                        data = list.getResult(2);
                        if (list.hasResult(3)) {
                            maxCount = list.getResult(3);
                        }
                    }
                    item.setDamage(data);
                }
            }

            targets = targets.stream().filter(Objects::nonNull).toList();

            if (targets.isEmpty()) {
                log.addNoTargetMatch().output();
                return 0;
            }

            for (Player target : targets) {
                HumanInventory inventory = target.getInventory();
                HumanOffHandInventory offhand = target.getOffhandInventory();

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
                        log.addError("commands.clear.failure.no.items", target.getViewableName(sender)).output();
                    } else {
                        log.addSuccess("commands.clear.success", target.getViewableName(sender), String.valueOf(count)).output();
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
                        log.addError("commands.clear.failure.no.items", target.getViewableName(sender)).output();
                        return 0;
                    } else {
                        log.addSuccess("commands.clear.testing", target.getViewableName(sender), String.valueOf(count)).output();
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
                        log.addError("commands.clear.failure.no.items", target.getViewableName(sender)).output();
                        return 0;
                    } else {
                        log.addSuccess("commands.clear.success", target.getViewableName(sender), String.valueOf(count)).output();
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
                        log.addError("commands.clear.failure.no.items", target.getViewableName(sender)).output();
                        return 0;
                    } else {
                        log.addSuccess("commands.clear.success", target.getViewableName(sender), String.valueOf(maxCount - remaining)).output();
                    }
                }
            }
            return targets.size();
    }
}
