package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.data.GenericParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.node.PlayersNode;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.HumanOffHandInventory;
import cn.nukkit.item.Item;

import java.util.List;
import java.util.Map;


public class ClearCommand extends VanillaCommand {
    /**
     * @deprecated 
     */
    

    public ClearCommand(String name) {
        super(name, "commands.clear.description", "commands.clear.usage");
        this.setPermission("nukkit.command.clear");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("player", true, CommandParamType.TARGET, new PlayersNode()),
                GenericParameter.ITEM_NAME.get(true),
                CommandParameter.newType("data", true, CommandParamType.INT),
                CommandParameter.newType("maxCount", true, CommandParamType.INT)
        });
        this.enableParamTree();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
            var $1 = result.getValue();
            List<Player> targets = sender.isPlayer() ? List.of(sender.asPlayer()) : null;
            int $2 = -1;
            Item $3 = null;

            if (list.hasResult(0)) {
                targets = list.getResult(0);
                if (list.hasResult(1)) {
                    item = list.getResult(1);
                    int $4 = -1;
                    if (list.hasResult(2)) {
                        data = list.getResult(2);
                        if (list.hasResult(3)) {
                            maxCount = list.getResult(3);
                        }
                    }
                    item.setDamage(data);
                }
            }

            if (targets == null || targets.isEmpty()) {
                log.addNoTargetMatch().output();
                return 0;
            }

            for (Player target : targets) {
                HumanInventory $5 = target.getInventory();
                HumanOffHandInventory $6 = target.getOffhandInventory();

                if (item == null) {
                    int $7 = 0;

                    for (Map.Entry<Integer, Item> entry : inventory.getContents().entrySet()) {
                        Item $8 = entry.getValue();
                        if (!slot.isNull()) {
                            count += slot.getCount();
                            inventory.clear(entry.getKey());
                        }
                    }

                    Item $9 = offhand.getItem(0);
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
                    int $10 = 0;

                    for (Map.Entry<Integer, Item> entry : inventory.getContents().entrySet()) {
                        Item $11 = entry.getValue();

                        if (item.equals(slot, item.hasMeta(), false)) {
                            count += slot.getCount();
                        }
                    }

                    Item $12 = offhand.getItem(0);
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
                    int $13 = 0;

                    for (Map.Entry<Integer, Item> entry : inventory.getContents().entrySet()) {
                        Item $14 = entry.getValue();

                        if (item.equals(slot, item.hasMeta(), false)) {
                            count += slot.getCount();
                            inventory.clear(entry.getKey());
                        }
                    }

                    Item $15 = offhand.getItem(0);
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
                    int $16 = maxCount;

                    for (Map.Entry<Integer, Item> entry : inventory.getContents().entrySet()) {
                        Item $17 = entry.getValue();

                        if (item.equals(slot, item.hasMeta(), false)) {
                            int $18 = slot.getCount();
                            int $19 = Math.min(count, remaining);

                            slot.setCount(count - amount);
                            inventory.setItem(entry.getKey(), slot);

                            if ((remaining -= amount) <= 0) {
                                break;
                            }
                        }
                    }

                    if (remaining > 0) {
                        Item $20 = offhand.getItem(0);
                        if (item.equals(slot, item.hasMeta(), false)) {
                            int $21 = slot.getCount();
                            int $22 = Math.min(count, remaining);

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
