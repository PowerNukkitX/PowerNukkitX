package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.command.EntitySelector;
import cn.nukkit.utils.TextFormat;

import java.util.List;

/**
 * @author Pub4Game
 * @since 23.01.2016
 */
public class EnchantCommand extends VanillaCommand {

    public EnchantCommand(String name) {
        super(name, "commands.enchant.description");
        this.setPermission("nukkit.command.enchant");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("enchantmentId", CommandParamType.INT),
                CommandParameter.newType("level", true, CommandParamType.INT)
        });
        this.commandParameters.put("byName", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("enchantmentName", Enchantment.getEnchantmentName2IDMap().keySet().toArray(String[]::new)),
                CommandParameter.newType("level", true, CommandParamType.INT)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }
        if (args.length < 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        List<Entity> entities = List.of();
        if (EntitySelector.hasArguments(args[0])) {
            entities = EntitySelector.matchEntities(sender, args[0]);
        } else if(sender.getServer().getPlayer(args[0]) != null){
            entities = List.of(sender.getServer().getPlayer(args[0]));
        }

        int enchantLevel;
        try {
            enchantLevel = args.length == 3 ? Integer.parseInt(args[2]) : 1;
        } catch (NumberFormatException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        Enchantment enchantment;
        try {
            enchantment = Enchantment.getEnchantment(args[1]);
        }catch(NullPointerException e){
            sender.sendMessage(new TranslationContainer("commands.enchant.notFound", args[1]));
            return false;
        }

        boolean successExecute = true;
        for (Entity entity : entities) {
            Player player = (Player) entity;
            enchantment.setLevel(enchantLevel,false);
            Item item = player.getInventory().getItemInHand();
            if (item.getId() == 0) {
                sender.sendMessage(new TranslationContainer("commands.enchant.noItem"));
                successExecute = false;
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
                PlayerInventory inventory = player.getInventory();
                inventory.setItemInHand(clone);
                player.giveItem(enchanted);
            }
            if (!successExecute) {
                return false;
            }
            Command.broadcastCommandMessage(sender, new TranslationContainer("commands.enchant.success", args[1]));
            return true;
        }
        return false;
    }
}
