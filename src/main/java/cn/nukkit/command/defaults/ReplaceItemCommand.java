package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockEntityHolder;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class ReplaceItemCommand extends VanillaCommand {
    public ReplaceItemCommand(String name) {
        super(name, "commands.replaceitem.description");
        this.setPermission("nukkit.command.replaceitem");
        this.commandParameters.clear();
        this.commandParameters.put("block", new CommandParameter[]{
                CommandParameter.newEnum("block", false, new String[]{"block"}),
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("slot.container", false, new String[]{"slot.container"}),
                CommandParameter.newType("slotId", CommandParamType.INT),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("data", true, CommandParamType.INT),
                CommandParameter.newType("components", true, CommandParamType.JSON),
        });
        this.commandParameters.put("block-oldItemHandling", new CommandParameter[]{
                CommandParameter.newEnum("block", false, new String[]{"block"}),
                CommandParameter.newType("position", CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("slot.container", false, new String[]{"slot.container"}),
                CommandParameter.newType("slotId", CommandParamType.INT),
                CommandParameter.newEnum("oldItemHandling", false, new String[]{"destroy", "keep"}),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("data", true, CommandParamType.INT),
                CommandParameter.newType("components", true, CommandParamType.JSON),
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
                CommandParameter.newEnum("entity", false, new String[]{"entity"}),
                CommandParameter.newType("target", CommandParamType.TARGET),
                CommandParameter.newEnum("slotType", false, slotTypes.toArray(new String[0])),
                CommandParameter.newType("slotId", CommandParamType.INT),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("data", true, CommandParamType.INT),
                CommandParameter.newType("components", true, CommandParamType.JSON),
        });
        this.commandParameters.put("entity-oldItemHandling", new CommandParameter[]{
                CommandParameter.newEnum("entity", false, new String[]{"entity"}),
                CommandParameter.newType("target", CommandParamType.TARGET),
                CommandParameter.newEnum("slotType", false, slotTypes.toArray(new String[0])),
                CommandParameter.newType("slotId", CommandParamType.INT),
                CommandParameter.newEnum("oldItemHandling", false, new String[]{"destroy", "keep"}),
                CommandParameter.newEnum("itemName", CommandEnum.ENUM_ITEM),
                CommandParameter.newType("amount", true, CommandParamType.INT),
                CommandParameter.newType("data", true, CommandParamType.INT),
                CommandParameter.newType("components", true, CommandParamType.JSON),
        });
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        switch (result.getKey()) {
            case "entity", "entity-oldItemHandling" -> {
                return entity(sender, result.getKey(), list, log);
            }
            case "block", "block-oldItemHandling" -> {
                Position pos = list.getResult(1);
                Block block = pos.getLevelBlock();
                InventoryHolder holder = null;
                boolean isHolder = false;
                if (block instanceof BlockEntityHolder<?> h) {
                    if (h.getBlockEntity() instanceof InventoryHolder ct) {
                        holder = ct;
                        isHolder = true;
                    }
                }
                if (!isHolder) {
                    log.addError("commands.replaceitem.noContainer", block.asBlockVector3().toString()).output();
                    return 0;
                }
                int slotId = list.getResult(3);
                if (slotId < 0 || slotId >= holder.getInventory().getSize()) {
                    log.addError("commands.replaceitem.badSlotNumber", "slot.container", "0", String.valueOf(holder.getInventory().getSize() - 1)).output();
                    return 0;
                }
                String oldItemHandling = result.getKey().equals("block") ? "destroy" : list.getResult(4);
                Item old = holder.getInventory().getItem(slotId);
                if (oldItemHandling.equals("keep") && !old.isNull()) {
                    log.addError("commands.replaceitem.keepFailed", "slot.container", String.valueOf(slotId)).output();
                    return 0;
                }
                Item item = list.getResult(5);
                item.setCount(1);
                if (list.hasResult(6)) {
                    int count = list.getResult(6);
                    item.setCount(count);
                }
                if (list.hasResult(7)) {
                    int data = list.getResult(7);
                    item.setDamage(data);
                }
                if (list.hasResult(8)) {
                    String[] components = list.getResult(8);
                    StringJoiner join = new StringJoiner("");
                    for (var c : components) join.add(c);
                    item.readItemJsonComponents(Item.ItemJsonComponents.fromJson(join.toString()));
                }
                if (holder.getInventory().setItem(slotId, item)) {
                    log.addSuccess("commands.replaceitem.success", "slot.container", String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()).output();
                    return 1;
                } else {
                    log.addError("commands.replaceitem.failed", "slot.container", String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName()).output();
                    return 0;
                }
            }
            default -> {
                return 0;
            }
        }
    }


    private int entity(CommandSender sender, String key, ParamList list, CommandLogger log) {
        List<Entity> entities = list.getResult(1);
        String slotType = list.getResult(2);
        int slotId = list.getResult(3);
        String oldItemHandling = key.equals("entity") ? "destroy" : list.getResult(4);
        Item item = list.getResult(5);
        item.setCount(1);
        if (list.hasResult(6)) {
            int amount = list.getResult(6);
            item.setCount(amount);
        }
        if (list.hasResult(7)) {
            int data = list.getResult(7);
            item.setDamage(data);
        }
        if (list.hasResult(8)) {
            String[] components = list.getResult(8);
            StringJoiner join = new StringJoiner("");
            for (var c : components) join.add(c);
            item.readItemJsonComponents(Item.ItemJsonComponents.fromJson(join.toString()));
        }
        int successCount = 0;
        for (Entity entity : entities) {
            switch (slotType) {
                case "slot.weapon.mainhand" -> {
                    if (entity instanceof Player player) {
                        Item old = player.getInventory().getItemInHand();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getInventory().setItemInHand(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                        }
                    } else if (entity instanceof EntityInventoryHolder entityMob) {
                        Item old = entityMob.getEquipmentInventory().getItemInHand();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (entityMob.getEquipmentInventory().setItemInHand(item, true)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                        }
                    }
                }
                case "slot.weapon.offhand" -> {
                    if (entity instanceof Player player) {
                        Item old = player.getOffhandInventory().getItem(0);
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getOffhandInventory().setItem(0, item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                        }
                    } else if (entity instanceof EntityInventoryHolder entityMob) {
                        Item old = entityMob.getEquipmentInventory().getItemInOffhand();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (entityMob.getEquipmentInventory().setItemInOffhand(item, true)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                        }
                    }
                }
                case "slot.armor.head" -> {
                    if (entity instanceof Player player) {
                        Item old = player.getInventory().getHelmet();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getInventory().setHelmet(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            continue;
                        }
                    }
                    if (entity instanceof EntityInventoryHolder entityMob) {
                        Item old = entityMob.getHelmet();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (entityMob.setHelmet(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                        }
                    }
                }
                case "slot.armor.chest" -> {
                    if (entity instanceof Player player) {
                        Item old = player.getInventory().getChestplate();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getInventory().setChestplate(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            continue;
                        }
                    }
                    if (entity instanceof EntityInventoryHolder entityMob) {
                        Item old = entityMob.getChestplate();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (entityMob.setChestplate(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                        }
                    }
                }
                case "slot.armor.legs" -> {
                    if (entity instanceof Player player) {
                        Item old = player.getInventory().getLeggings();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getInventory().setLeggings(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            continue;
                        }
                    }
                    if (entity instanceof EntityInventoryHolder entityMob) {
                        Item old = entityMob.getLeggings();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (entityMob.setLeggings(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                        }
                    }
                }
                case "slot.armor.feet" -> {
                    if (entity instanceof Player player) {
                        Item old = player.getInventory().getBoots();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getInventory().setBoots(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            continue;
                        }
                    }
                    if (entity instanceof EntityInventoryHolder entityMob) {
                        Item old = entityMob.getBoots();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (entityMob.setBoots(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                        }
                    }
                }
                case "slot.enderchest" -> {
                    if (slotId < 0 || slotId >= 27) {
                        log.addError("commands.replaceitem.badSlotNumber", slotType, "0", "26");
                        continue;
                    }
                    if (entity instanceof Player player) {
                        Item old = player.getEnderChestInventory().getItem(slotId);
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getEnderChestInventory().setItem(slotId, item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                        }
                    } else {
                        log.addError("commands.replaceitem.failed", slotType, "0", String.valueOf(item.getCount()), item.getName());
                    }
                }
                case "slot.hotbar" -> {
                    if (slotId < 0 || slotId >= 9) {
                        log.addError("commands.replaceitem.badSlotNumber", slotType, "0", "8");
                        continue;
                    }
                    if (entity instanceof Player player) {
                        Item old = player.getInventory().getItem(slotId);
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getInventory().setItem(slotId, item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                        }
                    } else {
                        log.addError("commands.replaceitem.failed", slotType, "0", String.valueOf(item.getCount()), item.getName());
                    }
                }
                case "slot.inventory" -> {
                    if (entity instanceof Player player) {
                        Item old = player.getInventory().getItem(slotId);
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (slotId < 0 || slotId >= player.getInventory().getSize()) {
                            log.addError("commands.replaceitem.badSlotNumber", slotType, "0", String.valueOf(player.getInventory().getSize()));
                            continue;
                        }
                        if (player.getInventory().setItem(slotId, item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, "0", String.valueOf(item.getCount()), item.getName());
                        }
                    } else if (entity instanceof EntityInventoryHolder entityMob) {
                        Item old = entityMob.getInventory().getItem(slotId);
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (slotId < 0 || slotId >= entityMob.getInventory().getSize()) {
                            log.addError("commands.replaceitem.badSlotNumber", slotType, "0", String.valueOf(entityMob.getInventory().getSize()));
                            continue;
                        }
                        if (entityMob.getInventory().setItem(slotId, item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, "0", String.valueOf(item.getCount()), item.getName());
                        }
                    }
                }
            }
        }
        log.successCount(successCount).output();
        return successCount;
    }
}
