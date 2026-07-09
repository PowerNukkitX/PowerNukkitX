package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.event.player.PlayerBlockPickEvent;
import org.powernukkitx.inventory.HumanInventory;
import org.powernukkitx.item.Item;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.nbt.tag.CompoundTag;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.BlockPickRequestPacket;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Kaooot
 */
@Slf4j
public class BlockPickRequestHandler implements PacketHandler<BlockPickRequestPacket> {

    @Override
    public void handle(BlockPickRequestPacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        Block block = player.level.getBlock(packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ(), false);

        if (!player.spawned || !player.isAlive()) {
            log.debug("Player {} tried to send a block pick request while not spawned or dead", playerHandle.getUsername());
            return;
        }

        if (block.distanceSquared(player) > 1000) {
            log.debug("{}: Block pick request for a block too far away", playerHandle.getUsername());
            return;
        }
        Item item = block.toItem();

        if (packet.isWithData()) {
            BlockEntity blockEntity = player.getLevel().getBlockEntity(new Vector3(packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ()));
            if (blockEntity != null) {
                CompoundTag nbt = blockEntity.getCleanedNBT();
                if (nbt != null) {
                    item.setCustomBlockData(nbt);
                    item.setLore("+(DATA)");
                }
            }
        }

        PlayerBlockPickEvent pickEvent = new PlayerBlockPickEvent(player, block, item);
        if (player.isSpectator()) {
            log.debug("Got block-pick request from {} when in spectator mode", player.getName());
            pickEvent.setCancelled();
        }

        player.getServer().getPluginManager().callEvent(pickEvent);

        if (!pickEvent.isCancelled()) {
            boolean itemExists = false;
            int itemSlot = -1;
            for (int slot = 0; slot < player.getInventory().getSize(); slot++) {
                if (player.getInventory().getItem(slot).equals(pickEvent.getItem())) {
                    if (slot < player.getInventory().getHotbarSize()) {
                        player.getInventory().setHeldItemSlot(slot);
                    } else {
                        itemSlot = slot;
                    }
                    itemExists = true;
                    break;
                }
            }

            for (int slot = 0; slot < player.getInventory().getHotbarSize(); slot++) {
                if (player.getInventory().getItem(slot).isNull()) {
                    if (!itemExists && player.isCreative()) {
                        player.getInventory().setHeldItemSlot(slot);
                        player.getInventory().setItemInMainHand(pickEvent.getItem());
                        return;
                    } else if (itemSlot > -1) {
                        player.getInventory().setHeldItemSlot(slot);
                        player.getInventory().setItemInMainHand(player.getInventory().getItem(itemSlot));
                        player.getInventory().clear(itemSlot, true);
                        return;
                    }
                }
            }

            if (!itemExists && player.isCreative()) {
                Item itemInHand = player.getInventory().getItemInMainHand();
                player.getInventory().setItemInMainHand(pickEvent.getItem());
                if (!player.getInventory().isFull()) {
                    for (int slot = 0; slot < HumanInventory.ARMORS_INDEX; slot++) {
                        if (player.getInventory().getItem(slot).isNull()) {
                            player.getInventory().setItem(slot, itemInHand);
                            break;
                        }
                    }
                }
            } else if (itemSlot > -1) {
                Item itemInHand = player.getInventory().getItemInMainHand();
                player.getInventory().setItemInMainHand(player.getInventory().getItem(itemSlot));
                player.getInventory().setItem(itemSlot, itemInHand);
            }
        }
    }
}