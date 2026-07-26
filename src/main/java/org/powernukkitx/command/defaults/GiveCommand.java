package org.powernukkitx.command.defaults;

import org.powernukkitx.Player;
import org.powernukkitx.block.BlockUnknown;
import org.powernukkitx.command.CommandSender;
import org.powernukkitx.command.data.CommandParameter;
import org.powernukkitx.command.data.GenericParameter;
import org.powernukkitx.command.tree.ParamList;
import org.powernukkitx.command.tree.node.PlayersNode;
import org.powernukkitx.command.tree.node.RemainStringNode;
import org.powernukkitx.command.utils.CommandLogger;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                CommandParameter.newType("player", CommandParamType.SELECTION, new PlayersNode()),
                GenericParameter.ITEM_NAME.get(false),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("data", true, CommandParamType.INT),
                CommandParameter.newType("components", true, CommandParamType.JSON_OBJECT, new RemainStringNode())
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Player> players = list.getResult(0);
        players = players.stream().filter(Objects::nonNull).toList();
        if (players.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }

        Item item = list.getResult(1);
        if (item.isNull()) {
            log.addError("commands.give.item.notFound", item.getDisplayName()).output();
            return 0;
        }
        if (item instanceof ItemBlock && item.getBlock() instanceof BlockUnknown) {
            log.addError("commands.give.block.notFound", item.getDisplayName()).output();
            return 0;
        }
        if (list.hasResult(2)) {
            int count = list.getResult(2);
            if (count <= 0) {
                log.addNumTooSmall(2, 1).output();
                return 0;
            }
            if (count > 32767) {
                log.addError("commands.generic.num.tooBig", String.valueOf(count), String.valueOf(32767)).output();
                return 0;
            }
            item.setCount(count);
        }
        if (list.hasResult(3)) {
            int damage = list.getResult(3);
            if (damage < 0) {
                log.addNumTooSmall(3, 0).output();
                return 0;
            }
            item.setDamage(damage);
        }
        if (list.hasResult(4)) {
            String json = list.getResult(4);
            Item.ItemJsonComponents components = Item.ItemJsonComponents.fromJson(json);
            item.readItemJsonComponents(components);
        }

        for (Player player : players) {
            Item[] returns = player.getInventory().addItem(item.clone());
            List<Item> drops = new ArrayList<>();
            for (Item returned : returns) {
                int maxStackSize = returned.getMaxStackSize();
                while (returned.getCount() > maxStackSize) {
                    Item drop = returned.clone();
                    drop.setCount(maxStackSize);
                    returned.setCount(returned.getCount() - maxStackSize);
                    drops.add(drop);
                }
                if (!returned.isNull()) {
                    drops.add(returned);
                }
            }
            for (Item drop : drops) {
                player.dropItem(drop);
            }
        }
        log.addSuccess("commands.give.success", item.getDisplayName(),
                String.valueOf(item.getCount()),
                players.stream().map(p -> p.getViewableName(sender)).collect(Collectors.joining(", "))).successCount(players.size()).output(true);
        return players.size();
    }
}
