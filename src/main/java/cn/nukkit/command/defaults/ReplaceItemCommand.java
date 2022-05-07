package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockEntityHolder;
import cn.nukkit.blockentity.BlockEntityContainer;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.List;

public class ReplaceItemCommand extends VanillaCommand {
    public ReplaceItemCommand(String name) {
        super(name, "commands.replaceitem.description");
        this.setPermission("nukkit.command.replaceitem");
        this.commandParameters.clear();
        this.commandParameters.put("block", new CommandParameter[]{
                CommandParameter.newEnum("block",false,new String[]{"block"}),
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("slot.container",false,new String[]{"slot.container"}),
                CommandParameter.newType("slotId", CommandParamType.INT),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM),
                CommandParameter.newType("amount",true, CommandParamType.INT),
                CommandParameter.newType("data",true, CommandParamType.INT),
                CommandParameter.newType("components",true, CommandParamType.JSON),
        });
        this.commandParameters.put("block-oldItemHandling", new CommandParameter[]{
                CommandParameter.newEnum("block",false,new String[]{"block"}),
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("slot.container",false,new String[]{"slot.container"}),
                CommandParameter.newType("slotId", CommandParamType.INT),
                CommandParameter.newEnum("oldItemHandling",false,new String[]{"destroy","keep"}),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM),
                CommandParameter.newType("amount",true, CommandParamType.INT),
                CommandParameter.newType("data",true, CommandParamType.INT),
                CommandParameter.newType("components",true, CommandParamType.JSON),
        });
        List<String> slotTypes = List.of(
                "slot.weapon.mainhand",
                "slot.weapon.offhand",
                "slot.armor.head",
                "slot.armor.chest",
                "slot.armor.legs",
                "slot.armor.feet",
                "slot.enderchest",
                "slot.hotbar",
                "slot.inventory",
                "slot.saddle",
                "slot.armor",
                "slot.equippable"
        );
        this.commandParameters.put("entity", new CommandParameter[]{
                CommandParameter.newEnum("entity",false,new String[]{"entity"}),
                CommandParameter.newType("target", CommandParamType.TARGET),
                CommandParameter.newEnum("slotType",false,slotTypes.toArray(new String[0])),
                CommandParameter.newType("slotId", CommandParamType.INT),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM),
                CommandParameter.newType("amount",true, CommandParamType.INT),
                CommandParameter.newType("data",true, CommandParamType.INT),
                CommandParameter.newType("components",true, CommandParamType.JSON),
        });
        this.commandParameters.put("entity-oldItemHandling", new CommandParameter[]{
                CommandParameter.newEnum("entity",false,new String[]{"entity"}),
                CommandParameter.newType("target", CommandParamType.TARGET),
                CommandParameter.newEnum("slotType",false,slotTypes.toArray(new String[0])),
                CommandParameter.newType("slotId", CommandParamType.INT),
                CommandParameter.newEnum("oldItemHandling",false,new String[]{"destroy","keep"}),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM),
                CommandParameter.newType("amount",true, CommandParamType.INT),
                CommandParameter.newType("data",true, CommandParamType.INT),
                CommandParameter.newType("components",true, CommandParamType.JSON),
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        String form = parser.matchCommandForm();
        if (form == null) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }
        try {
            switch (form) {
                case "entity", "entity-oldItemHandling": {
                    parser.parseString();
                    List<Entity> entities = parser.parseTargets().stream().filter(e -> e instanceof InventoryHolder).toList();
                    if (entities.isEmpty()) {
                        sender.sendMessage(new TranslationContainer("commands.generic.noTargetMatch"));
                        return false;
                    }
                    String slotType = parser.parseString();
                    int slotId = parser.parseInt();
                    String oldItemHandling = form.equals("entity") ? "destroy" : parser.parseString();
                    Item item = Item.fromString(parser.parseString());
                    item.setCount(1);
                    if (parser.hasNext()) {
                        item.setCount(parser.parseInt());
                    }
                    if (parser.hasNext()) {
                        item.setDamage(parser.parseInt());
                    }
                    if (parser.hasNext()) {
                        item.readItemJsonComponents(Item.ItemJsonComponents.fromJson(parser.parseString()));
                    }
                    for (Entity entity : entities) {
                        switch (slotType) {
                            case "slot.weapon.mainhand" -> {
                                if (entity instanceof Player player) {
                                    Item old = player.getInventory().getItemInHand();
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (player.getInventory().setItemInHand(item)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                }
                                if (entity instanceof EntityArmorStand armorStand) {
                                    Item old = armorStand.getEquipmentInventory().getItemInHand();
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (armorStand.getEquipmentInventory().setItemInHand(item, true)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                }
                                //todo: 实现其他实体 (等待生物AI实现)
                                return false;
                            }
                            case "slot.weapon.offhand" -> {
                                if (entity instanceof Player player) {
                                    Item old = player.getOffhandInventory().getItem(0);
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (player.getOffhandInventory().setItem(0, item)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                }
                                if (entity instanceof EntityArmorStand armorStand) {
                                    Item old = armorStand.getEquipmentInventory().getItemInOffhand();
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (armorStand.getEquipmentInventory().setItemInOffhand(item, true)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                }
                                //todo: 实现其他实体 (等待生物AI实现)
                                return false;
                            }
                            case "slot.armor.head" -> {
                                if (entity instanceof Player player) {
                                    Item old = player.getInventory().getHelmet();
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (player.getInventory().setHelmet(item)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                }
                                if (entity instanceof EntityArmorStand armorStand) {
                                    Item old = armorStand.getInventory().getHelmet();
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (armorStand.getInventory().setHelmet(item)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                }
                                //todo: 实现其他实体 (等待生物AI实现)
                                return false;
                            }
                            case "slot.armor.chest" -> {
                                if (entity instanceof Player player) {
                                    Item old = player.getInventory().getChestplate();
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (player.getInventory().setChestplate(item)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                }
                                if (entity instanceof EntityArmorStand armorStand) {
                                    Item old = armorStand.getInventory().getChestplate();
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (armorStand.getInventory().setChestplate(item)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                }
                                //todo: 实现其他实体 (等待生物AI实现)
                                return false;
                            }
                            case "slot.armor.legs" -> {
                                if (entity instanceof Player player) {
                                    Item old = player.getInventory().getLeggings();
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (player.getInventory().setLeggings(item)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                }
                                if (entity instanceof EntityArmorStand armorStand) {
                                    Item old = armorStand.getInventory().getLeggings();
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (armorStand.getInventory().setLeggings(item)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                }
                                //todo: 实现其他实体 (等待生物AI实现)
                                return false;
                            }
                            case "slot.armor.feet" -> {
                                if (entity instanceof Player player) {
                                    Item old = player.getInventory().getBoots();
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (player.getInventory().setBoots(item)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                }
                                if (entity instanceof EntityArmorStand armorStand) {
                                    Item old = armorStand.getInventory().getBoots();
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (armorStand.getInventory().setBoots(item)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                }
                                //todo: 实现其他实体 (等待生物AI实现)
                                return false;
                            }
                            case "slot.enderchest" -> {
                                if (slotId < 0 || slotId >= 27) {
                                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.badSlotNumber", slotType, "0", "26"));
                                    return false;
                                }
                                if (entity instanceof Player player) {
                                    Item old = player.getEnderChestInventory().getItem(slotId);
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (player.getEnderChestInventory().setItem(slotId, item)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                } else {
                                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, "0", String.valueOf(item.getCount()), item.getName()));
                                    return false;
                                }
                            }
                            case "slot.hotbar" -> {
                                if (slotId < 0 || slotId >= 9) {
                                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.badSlotNumber", slotType, "0", "8"));
                                    return false;
                                }
                                if (entity instanceof Player player) {
                                    Item old = player.getInventory().getItem(slotId);
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (player.getInventory().setItem(slotId, item)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                } else {
                                    sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, "0", String.valueOf(item.getCount()), item.getName()));
                                    return false;
                                }
                            }
                            case "slot.inventory" -> {
                                if (entity instanceof Player player) {
                                    Item old = player.getInventory().getItem(slotId);
                                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", slotType, String.valueOf(slotId)));
                                        return false;
                                    }
                                    if (slotId < 0 || slotId >= player.getInventory().getSize()) {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.badSlotNumber", slotType, "0", String.valueOf(player.getInventory().getSize())));
                                        return false;
                                    }
                                    if (player.getInventory().setItem(slotId, item)) {
                                        sender.sendMessage(new TranslationContainer("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));

                                    } else {
                                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.failed", slotType, "0", String.valueOf(item.getCount()), item.getName()));
                                        return false;
                                    }
                                }
                                //todo: 实现其他实体 (等待生物AI实现)
                            }
                        }
                    }
                }
                    break;
                case "block", "block-oldItemHandling": {
                    parser.parseString();
                    Block block = parser.parsePosition().getLevelBlock();
                    BlockEntityContainer container = null;
                    if (block instanceof BlockEntityHolder<?> holder) {
                        if (!(holder.getOrCreateBlockEntity() instanceof BlockEntityContainer ct)) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.noContainer", block.asBlockVector3().toString()));
                            return false;
                        }else{
                            container = ct;
                        }
                    }
                    parser.parseString();
                    int slotId = parser.parseInt();
                    if (slotId < 0 || slotId >= container.getSize()) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.badSlotNumber", "slot.container", "0", String.valueOf(container.getSize() - 1)));
                        return false;
                    }
                    String oldItemHandling = form.equals("block") ? "destroy" : parser.parseString();
                    Item old = container.getItem(slotId);
                    if (oldItemHandling.equals("keep") && !old.isNull()) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.replaceitem.keepFailed", "slot.container", String.valueOf(slotId)));
                        return false;
                    }
                    Item item = Item.fromString(parser.parseString());
                    item.setCount(1);
                    if (parser.hasNext()) {
                        item.setCount(parser.parseInt());
                    }
                    if (parser.hasNext()) {
                        item.setDamage(parser.parseInt());
                    }
                    if (parser.hasNext()) {
                        item.readItemJsonComponents(Item.ItemJsonComponents.fromJson(parser.parseString()));
                    }
                    container.setItem(slotId, item);
                    sender.sendMessage(new TranslationContainer("commands.replaceitem.success", "slot.container", String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()));
                }
                    break;
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return true;
    }
}
