package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockEntityHolder;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.EntityInventoryHolder;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;

import java.util.List;
import java.util.Map;


public class ReplaceItemCommand extends VanillaCommand {
    /**
     * @deprecated 
     */
    
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
        this.enableParamTree();
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var $1 = result.getValue();
        switch (result.getKey()) {
            case "entity", "entity-oldItemHandling" -> {
                return entity(sender, result.getKey(), list, log);
            }
            case "block", "block-oldItemHandling" -> {
                Position $2 = list.getResult(1);
                Block $3 = pos.getLevelBlock();
                InventoryHolder $4 = null;
                if (block instanceof BlockEntityHolder<?> h) {
                    if (h.getBlockEntity() instanceof InventoryHolder ct) {
                        holder = ct;
                    }
                }
                if (holder == null) {
                    log.addError("commands.replaceitem.noContainer", block.asBlockVector3().toString()).output();
                    return 0;
                }
                int $5 = list.getResult(3);
                if (slotId < 0 || slotId >= holder.getInventory().getSize()) {
                    log.addError("commands.replaceitem.badSlotNumber", "slot.container", "0", String.valueOf(holder.getInventory().getSize() - 1)).output();
                    return 0;
                }
                String $6 = result.getKey().equals("block") ? "destroy" : list.getResult(4);
                Item $7 = holder.getInventory().getItem(slotId);
                if (oldItemHandling.equals("keep") && !old.isNull()) {
                    log.addError("commands.replaceitem.keepFailed", "slot.container", String.valueOf(slotId)).output();
                    return 0;
                }
                var $8 = result.getKey().equals("block");
                Item $9 = list.getResult(notOldItemHandling ? 4 : 5);
                item.setCount(1);
                if (list.hasResult(notOldItemHandling ? 5 : 6)) {
                    int $10 = list.getResult(notOldItemHandling ? 5 : 6);
                    item.setCount(amount);
                }
                if (list.hasResult(notOldItemHandling ? 6 : 7)) {
                    int $11 = list.getResult(notOldItemHandling ? 6 : 7);
                    item.setDamage(data);
                }
                if (list.hasResult(notOldItemHandling ? 7 : 8)) {
                    String $12 = list.getResult(notOldItemHandling ? 7 : 8);
                    item.readItemJsonComponents(Item.ItemJsonComponents.fromJson(components));
                }
                if (holder.getInventory().setItem(slotId, item)) {
                    log.addSuccess("commands.replaceitem.success", "slot.container", String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName()).output();
                    return 1;
                } else {
                    log.addError("commands.replaceitem.failed", "slot.container", String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName()).output();
                    return 0;
                }
            }
            default -> {
                return 0;
            }
        }
    }

    
    /**
     * @deprecated 
     */
    private int entity(CommandSender sender, String key, ParamList list, CommandLogger log) {
        List<Entity> entities = list.getResult(1);
        if (entities.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }
        var $13 = key.equals("entity");
        String $14 = list.getResult(2);
        int $15 = list.getResult(3);
        String $16 = notOldItemHandling ? "destroy" : list.getResult(4);
        Item $17 = list.getResult(notOldItemHandling ? 4 : 5);
        item.setCount(1);
        if (list.hasResult(notOldItemHandling ? 5 : 6)) {
            int $18 = list.getResult(notOldItemHandling ? 5 : 6);
            item.setCount(amount);
        }
        if (list.hasResult(notOldItemHandling ? 6 : 7)) {
            int $19 = list.getResult(notOldItemHandling ? 6 : 7);
            item.setDamage(data);
        }
        if (list.hasResult(notOldItemHandling ? 7 : 8)) {
            String $20 = list.getResult(notOldItemHandling ? 7 : 8);
            item.readItemJsonComponents(Item.ItemJsonComponents.fromJson(components));
        }
        int $21 = 0;
        for (Entity entity : entities) {
            switch (slotType) {
                case "slot.weapon.mainhand" -> {
                    if (entity instanceof Player player) {
                        Item $22 = player.getInventory().getItemInHand();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getInventory().setItemInHand(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                        }
                    } else if (entity instanceof EntityInventoryHolder entityMob) {
                        Item $23 = entityMob.getItemInHand();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (entityMob.setItemInHand(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                        }
                    }
                }
                case "slot.weapon.offhand" -> {
                    if (entity instanceof Player player) {
                        Item $24 = player.getOffhandInventory().getItem(0);
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getOffhandInventory().setItem(0, item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                        }
                    } else if (entity instanceof EntityInventoryHolder entityMob) {
                        Item $25 = entityMob.getItemInOffhand();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (entityMob.setItemInOffhand(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                        }
                    }
                }
                case "slot.armor.head" -> {
                    if (entity instanceof Player player) {
                        Item $26 = player.getInventory().getHelmet();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getInventory().setHelmet(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            continue;
                        }
                    }
                    if (entity instanceof EntityInventoryHolder entityMob) {
                        Item $27 = entityMob.getHelmet();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (entityMob.setHelmet(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                        }
                    }
                }
                case "slot.armor.chest" -> {
                    if (entity instanceof Player player) {
                        Item $28 = player.getInventory().getChestplate();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getInventory().setChestplate(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            continue;
                        }
                    }
                    if (entity instanceof EntityInventoryHolder entityMob) {
                        Item $29 = entityMob.getChestplate();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (entityMob.setChestplate(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                        }
                    }
                }
                case "slot.armor.legs" -> {
                    if (entity instanceof Player player) {
                        Item $30 = player.getInventory().getLeggings();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getInventory().setLeggings(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            continue;
                        }
                    }
                    if (entity instanceof EntityInventoryHolder entityMob) {
                        Item $31 = entityMob.getLeggings();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (entityMob.setLeggings(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                        }
                    }
                }
                case "slot.armor.feet" -> {
                    if (entity instanceof Player player) {
                        Item $32 = player.getInventory().getBoots();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getInventory().setBoots(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            continue;
                        }
                    }
                    if (entity instanceof EntityInventoryHolder entityMob) {
                        Item $33 = entityMob.getBoots();
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (entityMob.setBoots(item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                        }
                    }
                }
                case "slot.enderchest" -> {
                    if (slotId < 0 || slotId >= 27) {
                        log.addError("commands.replaceitem.badSlotNumber", slotType, "0", "26");
                        continue;
                    }
                    if (entity instanceof Player player) {
                        Item $34 = player.getEnderChestInventory().getItem(slotId);
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getEnderChestInventory().setItem(slotId, item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                        }
                    } else {
                        log.addError("commands.replaceitem.failed", slotType, "0", String.valueOf(item.getCount()), item.getDisplayName());
                    }
                }
                case "slot.hotbar" -> {
                    if (slotId < 0 || slotId >= 9) {
                        log.addError("commands.replaceitem.badSlotNumber", slotType, "0", "8");
                        continue;
                    }
                    if (entity instanceof Player player) {
                        Item $35 = player.getInventory().getItem(slotId);
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (player.getInventory().setItem(slotId, item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                        }
                    } else {
                        log.addError("commands.replaceitem.failed", slotType, "0", String.valueOf(item.getCount()), item.getDisplayName());
                    }
                }
                case "slot.inventory" -> {
                    if (entity instanceof Player player) {
                        Item $36 = player.getInventory().getItem(slotId);
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (slotId < 0 || slotId >= player.getInventory().getSize()) {
                            log.addError("commands.replaceitem.badSlotNumber", slotType, "0", String.valueOf(player.getInventory().getSize()));
                            continue;
                        }
                        if (player.getInventory().setItem(slotId, item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, "0", String.valueOf(item.getCount()), item.getDisplayName());
                        }
                    } else if (entity instanceof EntityInventoryHolder entityMob) {
                        Item $37 = entityMob.getInventory().getItem(slotId);
                        if (oldItemHandling.equals("keep") && !old.isNull()) {
                            log.addError("commands.replaceitem.keepFailed", slotType, String.valueOf(slotId));
                            continue;
                        }
                        if (slotId < 0 || slotId >= entityMob.getInventory().getSize()) {
                            log.addError("commands.replaceitem.badSlotNumber", slotType, "0", String.valueOf(entityMob.getInventory().getSize()));
                            continue;
                        }
                        if (entityMob.getInventory().setItem(slotId, item)) {
                            log.addSuccess("commands.replaceitem.success.entity", entity.getName(), slotType, String.valueOf(old.getId()), String.valueOf(item.getCount()), item.getDisplayName());
                            successCount++;
                        } else {
                            log.addError("commands.replaceitem.failed", slotType, "0", String.valueOf(item.getCount()), item.getDisplayName());
                        }
                    }
                }
            }
        }
        log.successCount(successCount).output();
        return successCount;
    }
}
