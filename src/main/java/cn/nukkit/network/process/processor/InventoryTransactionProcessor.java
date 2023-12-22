package cn.nukkit.network.process.processor;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.entity.passive.EntityVillager;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.SmithingInventory;
import cn.nukkit.inventory.TradeInventory;
import cn.nukkit.inventory.transaction.*;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.inventory.transaction.data.UseItemOnEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Sound;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.network.protocol.types.ContainerIds;
import cn.nukkit.network.protocol.types.InventorySource;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Log4j2
public class InventoryTransactionProcessor extends DataPacketProcessor<InventoryTransactionPacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull InventoryTransactionPacket pk) {
        Player player = playerHandle.player;
        if (player.isSpectator()) {
            player.sendAllInventories();
            return;
        }
        if (pk.transactionType == InventoryTransactionPacket.TYPE_USE_ITEM) {
            handleUseItem(playerHandle, pk);
            return;
        } else if (pk.transactionType == InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY) {
            handleUseItemOnEntity(playerHandle, pk);
            return;
        } else if (pk.transactionType == InventoryTransactionPacket.TYPE_RELEASE_ITEM) {
            ReleaseItemData releaseItemData = (ReleaseItemData) pk.transactionData;
            try {
                int type = releaseItemData.actionType;
                switch (type) {
                    case InventoryTransactionPacket.RELEASE_ITEM_ACTION_RELEASE -> {
                        if (player.getStartActionTick() != -1) {
                            Item item = player.getInventory().getItemInHand();

                            int ticksUsed = player.getServer().getTick() - player.getStartActionTick();
                            if (!item.onRelease(player, ticksUsed)) {
                                player.getInventory().sendContents(player);
                            }

                            player.setUsingItem(false);
                        } else {
                            player.getInventory().sendContents(player);
                        }
                    }
                    case InventoryTransactionPacket.RELEASE_ITEM_ACTION_CONSUME -> {
                        log.debug("Unexpected release item action consume from {}", player::getName);
                    }
                }
            } finally {
                player.setUsingItem(false);
            }
            return;
        }
        // Nasty hack because the client won't change the right packet in survival when creating netherite stuff
        // so we are emulating what Mojang should be sending
        // 用来解决生存模式锻造无法使用的hack实现
        if (pk.transactionType == InventoryTransactionPacket.TYPE_MISMATCH
                && !player.isCreative()
                && player.getWindowById(Player.SMITHING_WINDOW_ID) instanceof SmithingInventory smithingInventory) {
            if (!smithingInventory.getResult().isNull()) {
                InventoryTransactionPacket fixedPacket = new InventoryTransactionPacket();
                fixedPacket.isRepairItemPart = true;
                fixedPacket.actions = new NetworkInventoryAction[6];

                Item fromIngredient = smithingInventory.getIngredient().clone();
                Item toIngredient = fromIngredient.decrement(1);

                Item fromEquipment = smithingInventory.getEquipment().clone();
                Item toEquipment = fromEquipment.decrement(1);

                Item fromResult = Item.getBlock(BlockID.AIR);
                Item toResult = smithingInventory.getResult().clone();

                NetworkInventoryAction action = new NetworkInventoryAction();
                action.setInventorySource(InventorySource.fromContainerWindowId(ContainerIds.UI));
                action.inventorySlot = SmithingInventory.SMITHING_INGREDIENT_UI_SLOT;
                action.oldItem = fromIngredient.clone();
                action.newItem = toIngredient.clone();
                fixedPacket.actions[0] = action;

                action = new NetworkInventoryAction();
                action.setInventorySource(InventorySource.fromContainerWindowId(ContainerIds.UI));
                action.inventorySlot = SmithingInventory.SMITHING_EQUIPMENT_UI_SLOT;
                action.oldItem = fromEquipment.clone();
                action.newItem = toEquipment.clone();
                fixedPacket.actions[1] = action;

                int emptyPlayerSlot = -1;
                for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                    if (player.getInventory().getItem(slot).isNull()) {
                        emptyPlayerSlot = slot;
                        break;
                    }
                }
                if (emptyPlayerSlot == -1) {
                    player.sendAllInventories();
                    player.getCursorInventory().sendContents(player);
                } else {
                    action = new NetworkInventoryAction();
                    action.setInventorySource(InventorySource.fromContainerWindowId(ContainerIds.INVENTORY));
                    action.inventorySlot = emptyPlayerSlot; // Cursor
                    action.oldItem = Item.getBlock(BlockID.AIR);
                    action.newItem = toResult.clone();
                    fixedPacket.actions[2] = action;

                    action = new NetworkInventoryAction();
                    action.setInventorySource(InventorySource.fromNonImplementedTodo(NetworkInventoryAction.SOURCE_TYPE_ANVIL_RESULT));
                    action.inventorySlot = 2; // result
                    action.oldItem = toResult.clone();
                    action.newItem = fromResult.clone();
                    fixedPacket.actions[3] = action;

                    action = new NetworkInventoryAction();
                    action.setInventorySource(InventorySource.fromNonImplementedTodo(NetworkInventoryAction.SOURCE_TYPE_ANVIL_INPUT));
                    action.inventorySlot = 0; // equipment
                    action.oldItem = toEquipment.clone();
                    action.newItem = fromEquipment.clone();
                    fixedPacket.actions[4] = action;

                    action = new NetworkInventoryAction();
                    action.setInventorySource(InventorySource.fromNonImplementedTodo(NetworkInventoryAction.SOURCE_TYPE_ANVIL_MATERIAL));
                    action.inventorySlot = 1; // material
                    action.oldItem = toIngredient.clone();
                    action.newItem = fromIngredient.clone();
                    fixedPacket.actions[5] = action;

                    pk = fixedPacket;
                }
            }
        }

        List<InventoryAction> actions = new ArrayList<>();
        for (NetworkInventoryAction networkInventoryAction : pk.actions) {
            if (playerHandle.getCraftingTransaction() != null) {
                if (player.craftingType == Player.CRAFTING_STONECUTTER && networkInventoryAction.getInventorySource().getType() == InventorySource.Type.NON_IMPLEMENTED_TODO) {
                    networkInventoryAction.setInventorySource(InventorySource.fromNonImplementedTodo(NetworkInventoryAction.SOURCE_TYPE_CRAFTING_RESULT));
                } else if (player.craftingType == Player.CRAFTING_CARTOGRAPHY && pk.actions.length == 2 && pk.actions[1].getInventorySource().getContainerId() == ContainerIds.UI
                        && networkInventoryAction.inventorySlot == 0) {
                    int slot = pk.actions[1].inventorySlot;
                    if (slot == 50) {
                        networkInventoryAction.setInventorySource(InventorySource.fromContainerWindowId(NetworkInventoryAction.SOURCE_TYPE_CRAFTING_RESULT));
                    } else {
                        networkInventoryAction.inventorySlot = slot - 12;
                    }
                }
            }

            InventoryAction a = networkInventoryAction.createInventoryAction(player);
            if (a == null) {
                log.debug("Unmatched inventory action from {}: {}", player.getName(), networkInventoryAction);
                player.sendAllInventories();
                return;
            }
            actions.add(a);
        }

        if (pk.isCraftingPart) {
            if (playerHandle.getCraftingTransaction() == null) {
                playerHandle.setCraftingTransaction(new CraftingTransaction(player, actions));
            } else {
                for (InventoryAction action : actions) {
                    playerHandle.getCraftingTransaction().addAction(action);
                }
            }
            if (playerHandle.getCraftingTransaction().getPrimaryOutput() != null && (playerHandle.getCraftingTransaction().isReadyToExecute() || playerHandle.getCraftingTransaction().canExecute())) {
                //we get the actions for player in several packets, so we can't execute it until we get the result
                if (playerHandle.getCraftingTransaction().execute()) {
                    Sound sound = null;
                    switch (player.craftingType) {
                        case Player.CRAFTING_STONECUTTER -> sound = Sound.BLOCK_STONECUTTER_USE;
                        case Player.CRAFTING_CARTOGRAPHY -> sound = Sound.BLOCK_CARTOGRAPHY_TABLE_USE;
                    }
                    if (sound != null) {
                        Collection<Player> players = player.getLevel().getChunkPlayers(player.getChunkX(), player.getChunkZ()).values();
                        players.remove(player);
                        if (!players.isEmpty()) {
                            player.getLevel().addSound(player, sound, 1f, 1f, players);
                        }
                    }
                }
                playerHandle.setCraftingTransaction(null);
            }
            return;
        } else if (pk.isEnchantingPart) {
            if (playerHandle.getEnchantTransaction() == null) {
                playerHandle.setEnchantTransaction(new EnchantTransaction(player, actions));
            } else {
                for (InventoryAction action : actions) {
                    playerHandle.getEnchantTransaction().addAction(action);
                }
            }
            if (playerHandle.getEnchantTransaction().canExecute()) {
                playerHandle.getEnchantTransaction().execute();
                playerHandle.setEnchantTransaction(null);
            }
            return;
        } else if (pk.isRepairItemPart) {
            Sound sound = null;
            if (GrindstoneTransaction.checkForItemPart(actions)) {
                if (playerHandle.getGrindstoneTransaction() == null) {
                    playerHandle.setGrindstoneTransaction(new GrindstoneTransaction(player, actions));
                } else {
                    for (InventoryAction action : actions) {
                        playerHandle.getGrindstoneTransaction().addAction(action);
                    }
                }
                if (playerHandle.getGrindstoneTransaction().canExecute()) {
                    try {
                        if (playerHandle.getGrindstoneTransaction().execute()) {
                            sound = Sound.BLOCK_GRINDSTONE_USE;
                        }
                    } finally {
                        playerHandle.setGrindstoneTransaction(null);
                    }
                }
            } else if (SmithingTransaction.checkForItemPart(actions)) {
                if (playerHandle.getSmithingTransaction() == null) {
                    playerHandle.setSmithingTransaction(new SmithingTransaction(player, actions));
                } else {
                    for (InventoryAction action : actions) {
                        playerHandle.getSmithingTransaction().addAction(action);
                    }
                }
                if (playerHandle.getSmithingTransaction().canExecute()) {
                    try {
                        if (playerHandle.getSmithingTransaction().execute()) {
                            sound = Sound.SMITHING_TABLE_USE;
                        }
                    } finally {
                        playerHandle.setSmithingTransaction(null);
                    }
                }
            } else {
                if (playerHandle.getRepairItemTransaction() == null) {
                    playerHandle.setRepairItemTransaction(new RepairItemTransaction(player, actions));
                } else {
                    for (InventoryAction action : actions) {
                        playerHandle.getRepairItemTransaction().addAction(action);
                    }
                }
                if (playerHandle.getRepairItemTransaction().canExecute()) {
                    try {
                        playerHandle.getRepairItemTransaction().execute();
                    } finally {
                        playerHandle.setRepairItemTransaction(null);
                    }
                }
            }
            if (sound != null) {
                Collection<Player> players = player.getLevel().getChunkPlayers(player.getChunkX(), player.getChunkZ()).values();
                players.remove(player);
                if (!players.isEmpty()) {
                    player.getLevel().addSound(player, sound, 1f, 1f, players);
                }
            }
            return;
        } else if (pk.isTradeItemPart) {
            if (playerHandle.getTradingTransaction() == null) {
                playerHandle.setTradingTransaction(new TradingTransaction(player, actions));
            } else {
                for (InventoryAction action : actions) {
                    playerHandle.getTradingTransaction().addAction(action);
                }
            }
            if (playerHandle.getTradingTransaction().canExecute()) {
                playerHandle.getTradingTransaction().execute();

                for (Inventory inventory : playerHandle.getTradingTransaction().getInventories()) {

                    if (inventory instanceof TradeInventory tradeInventory) {
                        EntityVillager ent = tradeInventory.getHolder();
                        ent.namedTag.putBoolean("traded", true);
                        for (Tag tag : ent.getRecipes().getAll()) {
                            CompoundTag ta = (CompoundTag) tag;
                            if (ta.getCompound("buyA").getString("Name").equals(tradeInventory.getItem(0).getNamespaceId())) {
                                int tradeXP = ta.getInt("traderExp");
                                player.addExperience(ta.getByte("rewardExp"));
                                ent.addExperience(tradeXP);
                                player.level.addSound(player, Sound.RANDOM_ORB, 0,3f, player);
                            }
                        }
                    }
                }

                playerHandle.setTradingTransaction(null);
            }
            return;
        } else if (playerHandle.getCraftingTransaction() != null) {
            if (playerHandle.getCraftingTransaction().checkForCraftingPart(actions)) {
                for (InventoryAction action : actions) {
                    playerHandle.getCraftingTransaction().addAction(action);
                }
            } else {
                log.debug("Got unexpected normal inventory action with incomplete crafting transaction from {}, refusing to execute crafting", player.getName());
                player.removeAllWindows(false);
                player.sendAllInventories();
                playerHandle.setCraftingTransaction(null);
            }
            return;
        } else if (playerHandle.getEnchantTransaction() != null) {
            if (playerHandle.getEnchantTransaction().checkForEnchantPart(actions)) {
                for (InventoryAction action : actions) {
                    playerHandle.getEnchantTransaction().addAction(action);
                }
            } else {
                log.debug("Got unexpected normal inventory action with incomplete enchanting transaction from {}, refusing to execute enchant {}", player.getName(), pk.toString());
                player.removeAllWindows(false);
                player.sendAllInventories();
                playerHandle.setEnchantTransaction(null);
            }
            return;
        } else if (playerHandle.getRepairItemTransaction() != null) {
            if (RepairItemTransaction.checkForRepairItemPart(actions)) {
                for (InventoryAction action : actions) {
                    playerHandle.getRepairItemTransaction().addAction(action);
                }
            } else {
                log.debug("Got unexpected normal inventory action with incomplete repair item transaction from " + player.getName() + ", refusing to execute repair item " + pk);
                player.removeAllWindows(false);
                player.sendAllInventories();
                playerHandle.setRepairItemTransaction(null);
            }
            return;
        } else if (playerHandle.getGrindstoneTransaction() != null) {
            if (GrindstoneTransaction.checkForItemPart(actions)) {
                for (InventoryAction action : actions) {
                    playerHandle.getGrindstoneTransaction().addAction(action);
                }
            } else {
                log.debug("Got unexpected normal inventory action with incomplete grindstone transaction from {}, refusing to execute use the grindstone {}", player.getName(), pk.toString());
                player.removeAllWindows(false);
                player.sendAllInventories();
                playerHandle.setGrindstoneTransaction(null);
            }
            return;
        } else if (playerHandle.getSmithingTransaction() != null) {
            if (SmithingTransaction.checkForItemPart(actions)) {
                for (InventoryAction action : actions) {
                    playerHandle.getSmithingTransaction().addAction(action);
                }
            } else {
                log.debug("Got unexpected normal inventory action with incomplete smithing table transaction from {}, refusing to execute use the smithing table {}", player.getName(), pk.toString());
                player.removeAllWindows(false);
                player.sendAllInventories();
                playerHandle.setSmithingTransaction(null);
            }
            return;
        }

        switch (pk.transactionType) {
            case InventoryTransactionPacket.TYPE_NORMAL -> {
                InventoryTransaction transaction = new InventoryTransaction(player, actions);
                if (!transaction.execute()) {
                    log.debug("Failed to execute inventory transaction from {} with actions: {}", player.getName(), Arrays.toString(pk.actions));
                }
                //TODO: fix achievement for getting iron from furnace
            }

            case InventoryTransactionPacket.TYPE_MISMATCH -> {
                if (pk.actions.length > 0) {
                    log.debug("Expected 0 actions for mismatch, got {}, {}", pk.actions.length, Arrays.toString(pk.actions));
                }
                player.sendAllInventories();
            }
            default -> {
                player.getInventory().sendContents(player);
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.INVENTORY_TRANSACTION_PACKET);
    }

    private void handleUseItemOnEntity(@NotNull PlayerHandle playerHandle, @NotNull InventoryTransactionPacket pk) {
        Player player = playerHandle.player;
        UseItemOnEntityData useItemOnEntityData = (UseItemOnEntityData) pk.transactionData;
        Entity target = player.level.getEntity(useItemOnEntityData.entityRuntimeId);
        if (target == null) {
            return;
        }
        int type = useItemOnEntityData.actionType;
        if (!useItemOnEntityData.itemInHand.equalsExact(player.getInventory().getItemInHand())) {
            player.getInventory().sendHeldItem(player);
        }
        Item item = player.getInventory().getItemInHand();
        switch (type) {
            case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_INTERACT -> {
                PlayerInteractEntityEvent playerInteractEntityEvent = new PlayerInteractEntityEvent(player, target, item, useItemOnEntityData.clickPos);
                if (player.isSpectator()) playerInteractEntityEvent.setCancelled();
                player.getServer().getPluginManager().callEvent(playerInteractEntityEvent);
                if (playerInteractEntityEvent.isCancelled()) {
                    return;
                }
                if (!(target instanceof EntityArmorStand)) {
                    player.level.getVibrationManager().callVibrationEvent(new VibrationEvent(target, target.clone(), VibrationType.ENTITY_INTERACT));
                } else {
                    player.level.getVibrationManager().callVibrationEvent(new VibrationEvent(target, target.clone(), VibrationType.EQUIP));
                }
                if (target.onInteract(player, item, useItemOnEntityData.clickPos) && (player.isSurvival() || player.isAdventure())) {
                    if (item.isTool()) {
                        if (item.useOn(target) && item.getAux() >= item.getMaxDurability()) {
                            player.getLevel().addSound(player, Sound.RANDOM_BREAK);
                            item = new ItemBlock(Block.get(BlockID.AIR));
                        }
                    } else {
                        if (item.count > 1) {
                            item.count--;
                        } else {
                            item = new ItemBlock(Block.get(BlockID.AIR));
                        }
                    }

                    if (item.getId() == 0 || player.getInventory().getItemInHand().getId() == item.getId()) {
                        player.getInventory().setItemInHand(item);
                    } else {
                        logTriedToSetButHadInHand(playerHandle, item, player.getInventory().getItemInHand());
                    }
                }
            }
            case InventoryTransactionPacket.USE_ITEM_ON_ENTITY_ACTION_ATTACK -> {
                if (target instanceof Player && !player.getAdventureSettings().get(AdventureSettings.Type.ATTACK_PLAYERS)
                        || !(target instanceof Player) && !player.getAdventureSettings().get(AdventureSettings.Type.ATTACK_MOBS))
                    return;
                if (target.getId() == player.getId()) {
                    player.kick(PlayerKickEvent.Reason.INVALID_PVP, "Attempting to attack yourself");
                    log.warn(player.getName() + " tried to attack oneself");
                    return;
                }
                if (!player.canInteract(target, player.isCreative() ? 8 : 5)) {
                    return;
                } else if (target instanceof Player) {
                    if ((((Player) target).getGamemode() & 0x01) > 0) {
                        return;
                    } else if (!player.getServer().getPropertyBoolean("pvp")) {
                        return;
                    }
                }
                float itemDamage = item.getAttackDamage();
                Enchantment[] enchantments = item.getEnchantments();
                if (item.applyEnchantments()) {
                    for (Enchantment enchantment : enchantments) {
                        itemDamage += enchantment.getDamageBonus(target);
                    }
                }
                Map<EntityDamageEvent.DamageModifier, Float> damage = new EnumMap<>(EntityDamageEvent.DamageModifier.class);
                damage.put(EntityDamageEvent.DamageModifier.BASE, itemDamage);
                float knockBack = 0.3f;
                if (item.applyEnchantments()) {
                    Enchantment knockBackEnchantment = item.getEnchantment(Enchantment.ID_KNOCKBACK);
                    if (knockBackEnchantment != null) {
                        knockBack += knockBackEnchantment.getLevel() * 0.1f;
                    }
                }
                EntityDamageByEntityEvent entityDamageByEntityEvent = new EntityDamageByEntityEvent(player, target, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage, knockBack, item.applyEnchantments() ? enchantments : null);
                entityDamageByEntityEvent.setBreakShield(item.canBreakShield());
                if (player.isSpectator()) entityDamageByEntityEvent.setCancelled();
                if ((target instanceof Player) && !player.level.getGameRules().getBoolean(GameRule.PVP)) {
                    entityDamageByEntityEvent.setCancelled();
                }

                //保存攻击的目标在lastAttackEntity
                if (!entityDamageByEntityEvent.isCancelled()) {
                    playerHandle.setLastAttackEntity(entityDamageByEntityEvent.getEntity());
                }
                if (target instanceof EntityLiving living) {
                    living.preAttack(player);
                }
                try {
                    if (!target.attack(entityDamageByEntityEvent)) {
                        if (item.isTool() && player.isSurvival()) {
                            player.getInventory().sendContents(player);
                        }
                        return;
                    }
                } finally {
                    if (target instanceof EntityLiving living) {
                        living.postAttack(player);
                    }
                }
                if (item.isTool() && (player.isSurvival() || player.isAdventure())) {
                    if (item.useOn(target) && item.getAux() >= item.getMaxDurability()) {
                        player.getLevel().addSound(player, Sound.RANDOM_BREAK);
                        player.getInventory().setItemInHand(Item.get(0));
                    } else {
                        if (item.getId() == 0 || player.getInventory().getItemInHand().getId() == item.getId()) {
                            player.getInventory().setItemInHand(item);
                        } else {
                            logTriedToSetButHadInHand(playerHandle, item, player.getInventory().getItemInHand());
                        }
                    }
                }
            }
        }
    }

    private void handleUseItem(@NotNull PlayerHandle playerHandle, @NotNull InventoryTransactionPacket pk) {
        Player player = playerHandle.player;
        UseItemData useItemData = (UseItemData) pk.transactionData;
        BlockVector3 blockVector = useItemData.blockPos;
        BlockFace face = useItemData.face;

        int type = useItemData.actionType;
        switch (type) {
            case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_BLOCK -> {
                // Remove if client bug is ever fixed
                boolean spamBug = (playerHandle.getLastRightClickPos() != null && System.currentTimeMillis() - playerHandle.getLastRightClickTime() < 100.0 && blockVector.distanceSquared(playerHandle.getLastRightClickPos()) < 0.00001);
                playerHandle.setLastRightClickPos(blockVector.asVector3());
                playerHandle.setLastRightClickTime(System.currentTimeMillis());
                if (spamBug && player.getInventory().getItemInHand().getBlock().getId()==0) {
                    return;
                }
                player.setDataFlag(Entity.DATA_FLAGS, Entity.DATA_FLAG_ACTION, false);
                if (player.canInteract(blockVector.add(0.5, 0.5, 0.5), player.isCreative() ? 13 : 7)) {
                    if (player.isCreative()) {
                        Item i = player.getInventory().getItemInHand();
                        if (player.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, player) != null) {
                            return;
                        }
                    } else if (player.getInventory().getItemInHand().equals(useItemData.itemInHand)) {
                        Item i = player.getInventory().getItemInHand();
                        Item oldItem = i.clone();
                        //TODO: Implement adventure mode checks
                        if ((i = player.level.useItemOn(blockVector.asVector3(), i, face, useItemData.clickPos.x, useItemData.clickPos.y, useItemData.clickPos.z, player)) != null) {
                            if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                if (oldItem.getId() == i.getId() || i.getId() == 0) {
                                    player.getInventory().setItemInHand(i);
                                } else {
                                    logTriedToSetButHadInHand(playerHandle, i, oldItem);
                                }
                                player.getInventory().sendHeldItem(player.getViewers().values());
                            }
                            return;
                        }
                    }
                }
                player.getInventory().sendHeldItem(player);
                if (blockVector.distanceSquared(player) > 10000) {
                    return;
                }
                Block target = player.level.getBlock(blockVector.asVector3());
                Block block = target.getSide(face);
                player.level.sendBlocks(new Player[]{player}, new Block[]{target, block}, UpdateBlockPacket.FLAG_NOGRAPHIC);
                player.level.sendBlocks(new Player[]{player}, new Block[]{target.getLevelBlockAtLayer(1), block.getLevelBlockAtLayer(1)}, UpdateBlockPacket.FLAG_NOGRAPHIC, 1);
            }
            case InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK -> {
                //Creative mode use PlayerActionPacket.ACTION_CREATIVE_PLAYER_DESTROY_BLOCK
                if (!player.spawned || !player.isAlive() || player.isCreative()) {
                    return;
                }
                player.resetCraftingGridType();
                Item i = player.getInventory().getItemInHand();
                Item oldItem = i.clone();
                if (player.isSurvival() || player.isAdventure()) {
                    if (player.canInteract(blockVector.add(0.5, 0.5, 0.5), 7) && (i = player.level.useBreakOn(blockVector.asVector3(), face, i, player, true)) != null) {
                        player.getFoodData().updateFoodExpLevel(0.005);
                        if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                            if (oldItem.getId() == i.getId() || i.getId() == 0) {
                                player.getInventory().setItemInHand(i);
                            } else {
                                logTriedToSetButHadInHand(playerHandle, i, oldItem);
                            }
                            player.getInventory().sendHeldItem(player.getViewers().values());
                        }
                        return;
                    }
                }
                player.getInventory().sendContents(player);
                player.getInventory().sendHeldItem(player);
                if (blockVector.distanceSquared(player) < 10000) {
                    Block target = player.level.getBlock(blockVector.asVector3());
                    player.level.sendBlocks(new Player[]{player}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);

                    BlockEntity blockEntity = player.level.getBlockEntity(blockVector.asVector3());
                    if (blockEntity instanceof BlockEntitySpawnable) {
                        ((BlockEntitySpawnable) blockEntity).spawnTo(player);
                    }
                }
            }
            case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR -> {
                Item item;
                Vector3 directionVector = player.getDirectionVector();
                if (player.isCreative()) {
                    item = player.getInventory().getItemInHand();
                } else if (!player.getInventory().getItemInHand().equals(useItemData.itemInHand)) {
                    player.getInventory().sendHeldItem(player);
                    return;
                } else {
                    item = player.getInventory().getItemInHand();
                }
                PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, item, directionVector, face, PlayerInteractEvent.Action.RIGHT_CLICK_AIR);
                player.getServer().getPluginManager().callEvent(interactEvent);
                if (interactEvent.isCancelled()) {
                    player.getInventory().sendHeldItem(player);
                    return;
                }
                if (item.onClickAir(player, directionVector)) {
                    if (!player.isCreative()) {
                        if (item.getId() == 0 || player.getInventory().getItemInHand().getId() == item.getId()) {
                            player.getInventory().setItemInHand(item);
                        } else {
                            logTriedToSetButHadInHand(playerHandle, item, player.getInventory().getItemInHand());
                        }
                    }

                    if (!player.isUsingItem()) {
                        player.setUsingItem(true);
                        return;
                    }

                    // Used item
                    int ticksUsed = player.getServer().getTick() - player.getStartActionTick();
                    player.setUsingItem(false);

                    if (!item.onUse(player, ticksUsed)) {
                        player.getInventory().sendContents(player);
                    }
                }
            }
            default -> {
                //unknown
            }
        }
    }

    private void logTriedToSetButHadInHand(PlayerHandle playerHandle, Item tried, Item had) {
        log.debug("Tried to set item {} but {} had item {} in their hand slot", tried.getId(), playerHandle.getUsername(), had.getId());
    }
}
