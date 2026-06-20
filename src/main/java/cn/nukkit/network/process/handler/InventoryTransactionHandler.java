package cn.nukkit.network.process.handler;

import cn.nukkit.AdventureSettings;
import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityLiving;
import cn.nukkit.entity.item.EntityArmorStand;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerHackDetectedEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemMace;
import cn.nukkit.item.ItemSpear;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Sound;
import cn.nukkit.level.vibration.VibrationEvent;
import cn.nukkit.level.vibration.VibrationType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.data.actor.ActorFlags;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.InventorySourceFlags;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.InventorySourceType;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.ItemReleaseActionType;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.ItemUseActionType;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.ItemUseOnActorActionType;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.data.InventoryTransactionDataType;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.data.ItemReleaseInventoryTransaction;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.data.ItemUseInventoryTransaction;
import org.cloudburstmc.protocol.bedrock.data.payload.inventory.transaction.data.ItemUseOnActorInventoryTransaction;
import org.cloudburstmc.protocol.bedrock.packet.InventoryTransactionPacket;
import org.cloudburstmc.protocol.bedrock.packet.UpdateBlockPacket;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Kaooot
 */
@Slf4j
public class InventoryTransactionHandler implements PacketHandler<InventoryTransactionPacket> {

    private final java.util.Map<Long, Integer> lastEntityInteractTick = new java.util.HashMap<>();

    @Override
    public void handle(InventoryTransactionPacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        if (packet.getTransaction().getType().equals(InventoryTransactionDataType.ITEM_USE)) {
            handleUseItem(playerHandle, (ItemUseInventoryTransaction) packet.getTransaction());
        } else if (packet.getTransaction().getType().equals(InventoryTransactionDataType.ITEM_USE_ON_ACTOR)) {
            handleUseItemOnEntity(playerHandle, (ItemUseOnActorInventoryTransaction) packet.getTransaction());
        } else if (packet.getTransaction().getType().equals(InventoryTransactionDataType.ITEM_RELEASE)) {
            try {
                final ItemReleaseInventoryTransaction releaseInventoryTransaction =
                        (ItemReleaseInventoryTransaction) packet.getTransaction();
                ItemReleaseActionType type = releaseInventoryTransaction.getActionType();
                final Item itemFromNetwork = Item.fromNetwork(releaseInventoryTransaction.getItem());
                if (type.equals(ItemReleaseActionType.RELEASE)) {
                    int lastUseTick = player.getLastUseTick(itemFromNetwork.getId());
                    if (lastUseTick != -1) {
                        Item item = player.getInventory().getItemInMainHand();

                        int ticksUsed = player.getLevel().getTick() - lastUseTick;
                        if (!item.onRelease(player, ticksUsed)) {
                            player.getInventory().sendContents(player);
                        }

                        player.clearLastUsedItem();
                    } else {
                        player.getInventory().sendContents(player);
                    }
                } else {
                    log.debug("Unexpected release item action consume from {}", player.getName());
                }
            } finally {
                player.clearLastUsedItem();
            }
        } else if (packet.getTransaction().getType().equals(InventoryTransactionDataType.NORMAL)) {
            // looks like an action index swap for u3
            if (packet.getTransaction().getActions().getActions().size() == 2 &&
                    packet.getTransaction().getActions().getActions().get(1).getSource().getSourceType().equals(InventorySourceType.WORLD_INTERACTION) &&
                    packet.getTransaction().getActions().getActions().get(1).getSource().getBitFlags().equals(InventorySourceFlags.NO_FLAG) &&
                    packet.getTransaction().getActions().getActions().getFirst().getSource().getSourceType().equals(InventorySourceType.CONTAINER_INVENTORY) &&
                    packet.getTransaction().getActions().getActions().getFirst().getSource().getBitFlags().equals(InventorySourceFlags.NO_FLAG)) { //handle throw hotbar item for player
                final int slot = packet.getTransaction().getActions().getActions().getFirst().getSlot();
                final int count = Math.min(packet.getTransaction().getActions().getActions().get(1).getToItem().getCount(), player.getInventory().getItem(slot).getCount());
                dropHotBarItemForPlayer(slot, count, player);
            }
        }
    }

    private static void dropHotBarItemForPlayer(int hotbarSlot, int dropCount, Player player) {
        final HumanInventory inventory = player.getInventory();
        Item item = inventory.getItem(hotbarSlot);
        if (item.isNull()) return;

        int c = item.getCount() - dropCount;
        if (c < 0) {
            player.getInventory().sendContents(player);
            log.warn("cannot drop more items than the current amount!");
            return;
        }

        PlayerDropItemEvent ev;
        player.getServer().getPluginManager().callEvent(ev = new PlayerDropItemEvent(player, item));
        if (ev.isCancelled()) {
            player.getInventory().sendContents(player);
            return;
        }

        if (c == 0) {
            inventory.clear(hotbarSlot);
        } else {
            item.setCount(c);
            inventory.setItem(hotbarSlot, item);
        }
        item.setCount(dropCount);
        player.dropItem(item);
    }

    private void handleUseItemOnEntity(@NotNull PlayerHandle playerHandle, @NotNull ItemUseOnActorInventoryTransaction transaction) {
        Player player = playerHandle.player;
        Entity target = player.level.getEntity(transaction.getRuntimeId());
        if (target == null) {
            return;
        }
        ItemUseOnActorActionType type = transaction.getActionType();
        if (player.getInventory().getHeldItemIndex() != transaction.getSlot()) {
            player.getInventory().equipItem(transaction.getSlot());
        }
        final Item itemFromNetwork = Item.fromNetwork(transaction.getItem());
        if (!itemFromNetwork.equalsExact(player.getInventory().getItemInMainHand())) {
            player.getInventory().sendHeldItem(player);
        }
        Item item = player.getInventory().getItemInMainHand();
        if (type.equals(ItemUseOnActorActionType.INTERACT)) {
            PlayerInteractEntityEvent playerInteractEntityEvent = new PlayerInteractEntityEvent(player, target, item, Vector3.fromNetwork(transaction.getFromPosition()));
            if (player.isSpectator() || (player.getDataFlag(ActorFlags.SILENT) && !(target instanceof InventoryHolder)))
                playerInteractEntityEvent.setCancelled();
            playerHandle.setInteract();
            player.getServer().getPluginManager().callEvent(playerInteractEntityEvent);
            if (playerInteractEntityEvent.isCancelled()) {
                return;
            }
            lastEntityInteractTick.put(player.getId(), player.getLevel().getTick());
            if (!(target instanceof EntityArmorStand)) {
                player.level.getVibrationManager().callVibrationEvent(new VibrationEvent(target, target.getLocation(), VibrationType.ENTITY_INTERACT));
            } else {
                player.level.getVibrationManager().callVibrationEvent(new VibrationEvent(target, target.getLocation(), VibrationType.EQUIP));
            }
            if (target.onInteract(player, item, Vector3.fromNetwork(transaction.getFromPosition())) && (player.isSurvival() || player.isAdventure())) {
                boolean forceSetSlot = false;

                if (item.isTool()) {
                    if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                        player.getLevel().addSound(player, Sound.RANDOM_BREAK);
                        item = new ItemBlock(Block.get(BlockID.AIR));
                    }
                } else if (item.isFilledBucketItem()) {
                    item = Item.get(Item.BUCKET);
                    forceSetSlot = true;
                } else {
                    if (item.count > 1) {
                        item.count--;
                    } else {
                        item = new ItemBlock(Block.get(BlockID.AIR));
                    }
                }

                if (forceSetSlot || item.isNull() || Objects.equals(player.getInventory().getItemInMainHand().getId(), item.getId())) {
                    player.getInventory().setItem(transaction.getSlot(), item);
                } else {
                    logTriedToSetButHadInHand(playerHandle, item, player.getInventory().getItemInMainHand());
                }
            } else {
                //Otherwise nametag still gets consumed on client side
                player.getInventory().sendContents(player);
            }
        } else if (type.equals(ItemUseOnActorActionType.ATTACK)) {
            if (target instanceof Player && !player.getAdventureSettings().get(AdventureSettings.Type.ATTACK_PLAYERS)
                    || !(target instanceof Player) && !player.getAdventureSettings().get(AdventureSettings.Type.ATTACK_MOBS))
                return;
            if (target.getId() == player.getId()) {
                PlayerHackDetectedEvent event = new PlayerHackDetectedEvent(player, PlayerHackDetectedEvent.HackType.INVALID_PVP);
                player.getServer().getPluginManager().callEvent(event);

                if (event.isKick())
                    player.kick(PlayerKickEvent.Reason.INVALID_PVP, "Attempting to attack yourself");

                log.warn("{} tried to attack oneself", player.getName());
                return;
            }
            if (!player.canInteract(target, player.isCreative() ? 8 : 5)) {
                return;
            } else if (target instanceof Player) {
                if ((((Player) target).getGamemode() & 0x01) > 0) {
                    return;
                } else if (!player.getServer().getSettings().gameplaySettings().pvp()) {
                    return;
                }
            }

            player.interruptShieldBlockingForAttack();

            float itemDamage = item.getAttackDamage(player);
            Enchantment[] enchantments = item.getEnchantments();
            if (item.applyEnchantments()) {
                for (Enchantment enchantment : enchantments) {
                    itemDamage += enchantment.getDamageBonus(target, player);
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

            //lastAttackEntity
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
            if (item instanceof ItemMace mace) {
                mace.onPostAttack(target, itemDamage);
            }
            if (item.isTool() && (player.isSurvival() || player.isAdventure())) {
                if (item.useOn(target) && item.getDamage() >= item.getMaxDurability()) {
                    player.getLevel().addSound(player, Sound.RANDOM_BREAK);
                    player.getInventory().setItem(transaction.getSlot(), Item.AIR);
                } else {
                    if (item.isNull() || Objects.equals(player.getInventory().getItemInMainHand().getId(), item.getId())) {
                        player.getInventory().setItem(transaction.getSlot(), item);
                    } else {
                        logTriedToSetButHadInHand(playerHandle, item, player.getInventory().getItemInMainHand());
                    }
                }
            }
        }
    }

    private void handleUseItem(@NotNull PlayerHandle playerHandle, @NotNull ItemUseInventoryTransaction transaction) {
        Player player = playerHandle.player;
        BlockVector3 blockVector = BlockVector3.fromNetwork(transaction.getPosition());
        BlockFace face = BlockFace.fromIndex(transaction.getFace());
        ItemUseActionType type = transaction.getActionType();
        if (player.getInventory().getHeldItemIndex() != transaction.getSlot()) {
            player.getInventory().equipItem(transaction.getSlot());
        }
        switch (type) {
            case PLACE -> {
                final Item itemInHandNet = Item.fromNetwork(transaction.getItem());
                if (!itemInHandNet.canBeActivated()) player.setDataFlag(ActorFlags.USING_ITEM, false);
                if (player.canInteract(blockVector.add(0.5, 0.5, 0.5), player.isCreative() ? 13 : 7)) {
                    if (player.isCreative()) {
                        Item i = player.getInventory().getItemInMainHand();
                        if (player.level.useItemOn(blockVector.asVector3(), i, face, transaction, player) != null) {
                            return;
                        }
                    } else if (player.getInventory().getItemInMainHand().equals(itemInHandNet, true, false)) {
                        Item i = player.getInventory().getItemInMainHand();
                        Item oldItem = i.clone();
                        //TODO: Implement adventure mode checks
                        if ((i = player.level.useItemOn(blockVector.asVector3(), i, face, transaction, player)) != null) {
                            if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                                if (Objects.equals(oldItem.getId(), i.getId()) || i.isNull()) {
                                    player.getInventory().setItem(transaction.getSlot(), i);
                                } else {
                                    logTriedToSetButHadInHand(playerHandle, i, oldItem);
                                }
                                player.getInventory().sendSlot(transaction.getSlot(), player.getViewers().values());
                            }
                            return;
                        }
                    }
                }
                player.getInventory().sendSlot(transaction.getSlot(), player);
                if (blockVector.distanceSquared(player) > 10000) {
                    return;
                }
                Block target = player.level.getBlock(blockVector.asVector3());
                Block block = target.getSide(face);
                player.level.sendBlocks(new Player[]{player}, new Block[]{target, block}, Set.of(UpdateBlockPacket.Flag.NO_GRAPHIC));
                player.level.sendBlocks(new Player[]{player}, new Block[]{target.getLevelBlockAtLayer(1), block.getLevelBlockAtLayer(1)}, Set.of(UpdateBlockPacket.Flag.NO_GRAPHIC), 1);
            }
            case USE -> {
                Integer lastTick = lastEntityInteractTick.get(player.getId());
                int now = player.getLevel().getTick();
                if (lastTick != null && (now - lastTick) <= 1) return;

                Item item;
                Item useItemDataItem = Item.fromNetwork(transaction.getItem());
                Item serverItemInHand = player.getInventory().getItemInMainHand();
                Vector3 directionVector = player.getDirectionVector();
                // Removes Damage Tag that the client adds, but we do not store.
                if (useItemDataItem.hasNbt() && (!serverItemInHand.hasNbt() || !serverItemInHand.getNbt().contains("Damage"))) {
                    if (useItemDataItem.getNbt().contains("Damage")) {
                        useItemDataItem.setNbt(useItemDataItem.getNbt().remove("Damage"));
                    }
                }

                if (player.isCreative()) {
                    item = serverItemInHand;
                } else if (!serverItemInHand.equals(useItemDataItem)) {
                    player.getServer().getLogger().debug("Item received did not match item in hand."); //Client seems to send multiple packets with the same durability.
                    player.getInventory().sendHeldItem(player);
                    return;
                } else {
                    item = serverItemInHand;
                }
                PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, item.clone(), directionVector, face, PlayerInteractEvent.Action.RIGHT_CLICK_AIR);
                player.getServer().getPluginManager().callEvent(interactEvent);
                playerHandle.setInteract();
                if (interactEvent.isCancelled()) {
                    if (interactEvent.getItem() != null && interactEvent.getItem().isArmor()) {
                        player.getInventory().sendArmorContents(player);
                    }
                    player.getInventory().sendSlot(transaction.getSlot(), player);
                    return;
                }
                if (item.onClickAir(player, directionVector)) {
                    if (!player.isCreative()) {
                        if (item.isNull() || Objects.equals(player.getInventory().getItemInMainHand().getId(), item.getId())) {
                            player.getInventory().setItem(transaction.getSlot(), item);
                        } else {
                            logTriedToSetButHadInHand(playerHandle, item, player.getInventory().getItemInMainHand());
                        }
                    }
                    if (!player.isUsingItem(item.getId())) {
                        player.setLastUsedItem(item);
                        if (item.getUsingTicks() <= 0) {
                            if (item.onUse(player, 0)) {
                                item.afterUse(player);
                            }
                            player.clearLastUsedItem();
                            return;
                        }
                        return;
                    }

                    Item lastUsedItem = player.getLastUsedItem();
                    int ticksUsed = player.getLevel().getTick() - player.getLastUseTick(lastUsedItem.getId());
                    if (lastUsedItem.onUse(player, ticksUsed)) {
                        lastUsedItem.afterUse(player);
                        player.clearLastUsedItem();
                    }
                }
            }
            case DESTROY -> {
                //Creative mode use PlayerActionPacket.ACTION_CREATIVE_PLAYER_DESTROY_BLOCK
                if (!player.spawned || !player.isAlive() || player.isCreative()) {
                    return;
                }
                player.resetInventory();
                Item i = player.getInventory().getItemInMainHand();
                Item oldItem = i.clone();
                if (player.isSurvival() || player.isAdventure()) {
                    if (player.canInteract(blockVector.add(0.5, 0.5, 0.5), 7) && (i = player.level.useBreakOn(blockVector.asVector3(), face, i, player, true)) != null) {
                        player.getFoodData().exhaust(0.005);
                        if (!i.equals(oldItem) || i.getCount() != oldItem.getCount()) {
                            if (Objects.equals(oldItem.getId(), i.getId()) || i.isNull()) {
                                player.getInventory().setItem(transaction.getSlot(), i);
                            } else {
                                logTriedToSetButHadInHand(playerHandle, i, oldItem);
                            }
                            player.getInventory().sendSlot(transaction.getSlot(), player.getViewers().values());
                        }
                        return;
                    }
                }
                player.getInventory().sendContents(player);
                if (blockVector.distanceSquared(player) < 10000) {
                    Block target = player.level.getBlock(blockVector.asVector3());
                    player.level.sendBlocks(new Player[]{player}, new Block[]{target}, UpdateBlockPacket.FLAG_ALL_PRIORITY, 0);

                    BlockEntity blockEntity = player.level.getBlockEntity(blockVector.asVector3());
                    if (blockEntity instanceof BlockEntitySpawnable) {
                        ((BlockEntitySpawnable) blockEntity).spawnTo(player);
                    }
                }
            }
            case USE_AS_ATTACK -> {
                Item item;
                Item useItemDataItem = Item.fromNetwork(transaction.getItem());
                Item serverItemInHand = player.getInventory().getItemInMainHand();
                Vector3 directionVector = player.getDirectionVector();
                // Removes Damage Tag that the client adds, but we do not store.
                if (useItemDataItem.hasNbt() && (!serverItemInHand.hasNbt() || !serverItemInHand.getNbt().contains("Damage"))) {
                    if (useItemDataItem.getNbt().contains("Damage")) {
                        useItemDataItem.getNbt().remove("Damage");
                    }
                }

                if (player.isCreative()) {
                    item = serverItemInHand;
                } else if (!serverItemInHand.equals(useItemDataItem)) {
                    player.getServer().getLogger().debug("Item received did not match item in hand.");
                    player.getInventory().sendHeldItem(player);
                    return;
                } else {
                    item = serverItemInHand;
                }

                PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, item.clone(), directionVector, face, PlayerInteractEvent.Action.RIGHT_CLICK_AIR);
                player.getServer().getPluginManager().callEvent(interactEvent);
                playerHandle.setInteract();

                if (interactEvent.isCancelled()) {
                    player.getInventory().sendSlot(transaction.getSlot(), player);
                    return;
                }

                if (!(item instanceof ItemSpear spear)) {
                    return;
                }

                float spearSpeed = player.getRiding() != null
                        ? (float) player.getRiding().getMotion().length()
                        : player.getMovementSpeed();
                spear.onSpearStab(player, spearSpeed);
            }
            default -> log.debug("{} sent invalid item use action type {}", player.getName(), type);
        }
    }

    private void logTriedToSetButHadInHand(PlayerHandle playerHandle, Item tried, Item had) {
        log.debug("Tried to set item {} but {} had item {} in their hand slot", tried.getId(), playerHandle.getUsername(), had.getId());
    }
}
