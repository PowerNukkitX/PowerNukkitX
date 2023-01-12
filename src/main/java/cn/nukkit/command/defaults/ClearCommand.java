package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.PlayerOffhandInventory;
import cn.nukkit.item.Item;

import java.util.List;
import java.util.Map;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ClearCommand extends VanillaCommand {

    public ClearCommand(String name) {
        super(name, "commands.clear.description", "commands.clear.usage");
        this.setPermission("nukkit.command.clear");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("player", true, CommandParamType.TARGET, new PlayersNode()),
                ITEM_NAME.get(true),
                CommandParameter.newType("data", true, CommandParamType.INT),
                CommandParameter.newType("maxCount", true, CommandParamType.INT)
        });
        this.paramTree = new ParamTree(this);
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
            var list = result.getValue();
            List<Player> targets = sender.isPlayer() ? List.of(sender.asPlayer()) : null;
            int maxCount = -1;
            Item item = null;

            if (list.hasResult(0)) {
                targets = list.getResult(1);
                if (list.hasResult(1)) {
                    String itemName = list.getResult(1);
                    int data = -1;
                    if (list.hasResult(2)) {
                        data = list.getResult(2);
                        if (list.hasResult(3)) {
                            maxCount = list.getResult(3);
                        }
                    }

                    itemName = itemName + ":" + data;

                    item = Item.fromString(itemName);
                }
            }

            if (targets == null) {
                log.addNoTargetMatch().output();
                return 0;
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
                        log.addError("commands.clear.failure.no.items", target.getName()).output();
                    } else {
                        log.addSuccess("commands.clear.success", target.getName(), String.valueOf(count)).output();
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
                        log.addError("commands.clear.failure.no.items", target.getName()).output();
                        return 0;
                    } else {
                        log.addSuccess("commands.clear.testing", target.getName(), String.valueOf(count)).output();
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
                        log.addError("commands.clear.failure.no.items", target.getName()).output();
                        return 0;
                    } else {
                        log.addSuccess("commands.clear.success", target.getName(), String.valueOf(count)).output();
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
                        log.addError("commands.clear.failure.no.items", target.getName()).output();
                        return 0;
                    } else {
                        log.addSuccess("commands.clear.success", target.getName(), String.valueOf(maxCount - remaining)).output();
                    }
                }
            }
            return targets.size();
    }
}
