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
import cn.nukkit.utils.EntitySelector;
import cn.nukkit.utils.TextFormat;

import java.util.List;

/**
 * @author Pub4Game
 * @since 23.01.2016
 */
public class EnchantCommand extends VanillaCommand {

    public EnchantCommand(String name) {
        super(name, "commands.enchant.description", "commands.enchant.usage");
        this.setPermission("nukkit.command.enchant");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newType("enchantmentId", CommandParamType.INT),
                CommandParameter.newType("level", true, CommandParamType.INT)
        });
        this.commandParameters.put("byName", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET),
                CommandParameter.newEnum("enchantmentName", new CommandEnum("Enchant",
                        "protection", "fire_protection", "feather_falling", "blast_protection", "projectile_projection", "thorns", "respiration",
                        "aqua_affinity", "depth_strider", "sharpness", "smite", "bane_of_arthropods", "knockback", "fire_aspect", "looting", "efficiency",
                        "silk_touch", "durability", "fortune", "power", "punch", "flame", "infinity", "luck_of_the_sea", "lure", "frost_walker", "mending",
                        "binding_curse", "vanishing_curse", "impaling", "loyalty", "riptide", "channeling", "multishot", "piercing", "quick_charge",
                        "soul_speed")),
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

        List<Entity> players = entities.stream().filter(entity -> entity instanceof Player).toList();
        if (players.size() == 0) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return false;
        }

        int enchantId;
        int enchantLevel;
        try {
            enchantId = getIdByName(args[1]);
            enchantLevel = args.length == 3 ? Integer.parseInt(args[2]) : 1;
        } catch (NumberFormatException e) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }
        Enchantment enchantment = Enchantment.getEnchantment(enchantId);

        if (enchantment == null) {
            sender.sendMessage(new TranslationContainer("commands.enchant.notFound", String.valueOf(enchantId)));
            return false;
        }

        boolean successExecute = false;
        for (Entity entity : entities) {
            Player player = (Player) entity;
            enchantment.setLevel(enchantLevel);
            Item item = player.getInventory().getItemInHand();
            if (item.getId() == 0) {
                sender.sendMessage(new TranslationContainer("commands.enchant.noItem"));
                continue;
            }
            if (item.getId() != ItemID.BOOK) {
                item.addEnchantment(enchantment);
                player.getInventory().setItemInHand(item);
            } else {
                successExecute = true;
                Item enchanted = Item.get(ItemID.ENCHANTED_BOOK, 0, 1, item.getCompoundTag());
                enchanted.addEnchantment(enchantment);
                Item clone = item.clone();
                clone.count--;
                PlayerInventory inventory = player.getInventory();
                inventory.setItemInHand(clone);
                player.giveItem(enchanted);
            }
            if (!successExecute) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                return false;
            }
            Command.broadcastCommandMessage(sender, new TranslationContainer("%commands.enchant.success", args[1]));
            return true;
        }
        return false;
    }

    public int getIdByName(String value) throws NumberFormatException {
        value = value.toLowerCase();
        return switch (value) {
            case "protection" -> 0;
            case "fire_protection" -> 1;
            case "feather_falling" -> 2;
            case "blast_protection" -> 3;
            case "projectile_projection" -> 4;
            case "thorns" -> 5;
            case "respiration" -> 6;
            case "aqua_affinity" -> 7;
            case "depth_strider" -> 8;
            case "sharpness" -> 9;
            case "smite" -> 10;
            case "bane_of_arthropods" -> 11;
            case "knockback" -> 12;
            case "fire_aspect" -> 13;
            case "looting" -> 14;
            case "efficiency" -> 15;
            case "silk_touch" -> 16;
            case "durability", "unbreaking" -> 17;
            case "fortune" -> 18;
            case "power" -> 19;
            case "punch" -> 20;
            case "flame" -> 21;
            case "infinity" -> 22;
            case "luck_of_the_sea" -> 23;
            case "lure" -> 24;
            case "frost_walker" -> 25;
            case "mending" -> 26;
            case "binding_curse" -> 27;
            case "vanishing_curse" -> 28;
            case "impaling" -> 29;
            case "riptide" -> 30;
            case "loyalty" -> 31;
            case "channeling" -> 32;
            case "multishot" -> 33;
            case "piercing" -> 34;
            case "quick_charge" -> 35;
            case "soul_speed" -> 36;
            default -> Integer.parseInt(value);
        };
    }
}
