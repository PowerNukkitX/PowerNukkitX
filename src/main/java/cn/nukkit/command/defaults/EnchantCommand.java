package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;

import java.util.List;
import java.util.Map;

/**
 * @author Pub4Game
 * @since 23.01.2016
 */
public class EnchantCommand extends VanillaCommand {

    public EnchantCommand(String name) {
        super(name, "commands.enchant.description", "nukkit.command.enchant.usage");
        this.setPermission("nukkit.command.enchant");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("enchantmentId", CommandParamType.INT),
                CommandParameter.newType("level", true, CommandParamType.INT)
        });
        this.commandParameters.put("byName", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("enchantmentName", CommandEnum.ENUM_ENCHANTMENT),
                CommandParameter.newType("level", true, CommandParamType.INT)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        List<Entity> entities = list.getResult(0);
        if (entities.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        Enchantment enchantment;
        int enchantLevel = 1;
        switch (result.getKey()) {
            case "default" -> {
                int enchant = list.getResult(1);
                enchantment = Enchantment.getEnchantment(enchant);
                if (enchantment.getOriginalName().equals("unknown")) {
                    log.addError("commands.enchant.notFound", String.valueOf(enchant)).output();
                    return 0;
                }
            }
            case "byName" -> {
                String str = list.getResult(1);
                enchantment = Enchantment.getEnchantment(str);
                if (enchantment == null) {
                    log.addError("commands.enchant.notFound", String.valueOf(str)).output();
                    return 0;
                }
            }
            default -> {
                return 0;
            }
        }
        if (list.hasResult(2)) {
            enchantLevel = list.getResult(2);
            if (enchantLevel < 1) {
                log.addNumTooSmall(2, 1).output();
                return 0;
            }
        }
        int success = 0;
        for (Entity entity : entities) {
            Player player = (Player) entity;
            enchantment.setLevel(enchantLevel, false);
            Item item = player.getInventory().getItemInHand();
            if (item.isNull()) {
                log.addError("commands.enchant.noItem").output();
                continue;
            }
            if (item.getId() != ItemID.BOOK) {
                item.addEnchantment(enchantment);
                player.getInventory().setItemInHand(item);
            } else {
                Item enchanted = Item.get(ItemID.ENCHANTED_BOOK, 0, 1, item.getCompoundTag());
                enchanted.addEnchantment(enchantment);
                Item clone = item.clone();
                clone.count--;
                HumanInventory inventory = player.getInventory();
                inventory.setItemInHand(clone);
                player.giveItem(enchanted);
            }
            log.addSuccess("commands.enchant.success", enchantment.getName()).output(true);
            success++;
        }
        return success;
    }
}
