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
import cn.nukkit.entity.data.EntityFlag;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.inventory.HumanInventory;
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
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import cn.nukkit.network.protocol.types.inventory.transaction.InventorySource;
import cn.nukkit.network.protocol.types.inventory.transaction.ReleaseItemData;
import cn.nukkit.network.protocol.types.inventory.transaction.UseItemData;
import cn.nukkit.network.protocol.types.inventory.transaction.UseItemOnEntityData;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class InventoryTransactionProcessor extends DataPacketProcessor<InventoryTransactionPacket> {
    Item lastUsedItem = null;


    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull InventoryTransactionPacket pk) {
        Player player = playerHandle.player;
        if (player.isSpectator()) {
            player.sendAllInventories();
            return;
        }
        if (pk.transactionType == InventoryTransactionPacket.TYPE_USE_ITEM) {
            handleUseItem(playerHandle, pk);
        } else if (pk.transactionType == InventoryTransactionPacket.TYPE_USE_ITEM_ON_ENTITY) {
            handleUseItemOnEntity(playerHandle, pk);
        } else if (pk.transactionType == InventoryTransactionPacket.TYPE_RELEASE_ITEM) {
            ReleaseItemData releaseItemData = (ReleaseItemData) pk.transactionData;
            try {
                int type = releaseItemData.actionType;
                switch (type) {
                    case InventoryTransactionPacket.RELEASE_ITEM_ACTION_RELEASE -> {
                        int lastUseTick = player.getLastUseTick(releaseItemData.itemInHand.getId());
                        if (lastUseTick != -1) {
                            Item item = player.getInventory().getItemInHand();

                            int ticksUsed = player.getServer().getTick() - lastUseTick;
                            if (!item.onRelease(player, ticksUsed)) {
                                player.getInventory().sendContents(player);
                            }

                            player.removeLastUseTick(releaseItemData.itemInHand.getId());
                        } else {
                            player.getInventory().sendContents(player);
                        }
                    }
                    case InventoryTransactionPacket.RELEASE_ITEM_ACTION_CONSUME -> {
                        log.debug("Unexpected release item action consume from {}", player.getName());
                    }
                }
            } finally {
                player.removeLastUseTick(releaseItemData.itemInHand.getId());
            }
        } else if (pk.transactionType == InventoryTransactionPacket.TYPE_NORMAL) {
            if (pk.actions.length == 2 && pk.actions[0].getInventorySource().getType().equals(InventorySource.Type.WORLD_INTERACTION) &&
                    pk.actions[0].getInventorySource().getFlag().equals(InventorySource.Flag.DROP_ITEM) &&
                    pk.actions[1].getInventorySource().getType().equals(InventorySource.Type.CONTAINER)
                    && pk.actions[1].getInventorySource().getFlag().equals(InventorySource.Flag.NONE)) {//handle throw hotbar item for player
                dropHotBarItemForPlayer(pk.actions[1].inventorySlot, pk.actions[0].newItem.count, player);
            }
        }
    }

    private static void dropHotBarItemForPlayer(int hotbarSlot, int dropCount, Player player) {
        final HumanInventory inventory = player.getInventory();
        Item item = inventory.getItem(hotbarSlot);
        if (item.isNull()) return;

        PlayerDropItemEvent ev;
        player.getServer().getPluginManager().callEvent(ev = new PlayerDropItemEvent(player, item));
        if (ev.isCancelled()) {
            player.getInventory().sendContents(player);
            return;
        }

        int c = item.getCount() - dropCount;
        if (c <= 0) {
            inventory.clear(hotbarSlot);
        } else {
            item.setCount(c);
            inventory.setItem(hotbarSlot, item);
        }
        item.setCount(dropCount);
        player.dropItem(item);
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.INVENTORY_TRANSACTION_PACKET;
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
                        if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
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

                    if (item.isNull() || player.getInventory().getItemInHand().getId() == item.getId()) {
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
                    if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                        player.getLevel().addSound(player, Sound.RANDOM_BREAK);
                        player.getInventory().setItemInHand(Item.AIR);
                    } else {
                        if (item.isNull() || player.getInventory().getItemInHand().getId() == item.getId()) {
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
                if (spamBug && player.getInventory().getItemInHand().getBlock().isAir()) {//Normal blocks are not detected, as they can be placed continuously
                    return;
                }
                player.setDataFlag(EntityFlag.USING_ITEM, false);
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
                                if (Objects.equals(oldItem.getId(), i.getId()) || i.isNull()) {
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
                player.resetInventory();
                Item i = player.getInventory().getItemInHand();
                Item oldItem = i.clone();
                if (player.isSurvival() || player.isAdventure()) {
                    if (player.canInteract(blockVector.add(0.5, 0.5, 0.5), 7) && (i = player.level.useBreakOn(blockVector.asVector3(), face, i, player, true)) != null) {
                        player.getFoodData().exhaust(0.005);
                        if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                            if (Objects.equals(oldItem.getId(), i.getId()) || i.isNull()) {
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
                    if (interactEvent.getItem() != null && interactEvent.getItem().isArmor()) {
                        player.getInventory().sendArmorContents(player);
                    }
                    player.getInventory().sendHeldItem(player);
                    return;
                }
                if (item.onClickAir(player, directionVector)) {
                    if (!player.isCreative()) {
                        if (item.isNull() || Objects.equals(player.getInventory().getItemInHand().getId(), item.getId())) {
                            player.getInventory().setItemInHand(item);
                        } else {
                            logTriedToSetButHadInHand(playerHandle, item, player.getInventory().getItemInHand());
                        }
                    }
                    if (!player.isUsingItem(item.getId())) {
                        lastUsedItem = item;
                        player.setLastUseTick(item.getId(), player.getServer().getTick());//set lastUsed tick
                        return;
                    }

                    int ticksUsed = player.getServer().getTick() - player.getLastUseTick(lastUsedItem.getId());
                    if (lastUsedItem.onUse(player, ticksUsed)) {
                        lastUsedItem.afterUse(player);
                        player.removeLastUseTick(item.getId());
                        lastUsedItem = null;
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
