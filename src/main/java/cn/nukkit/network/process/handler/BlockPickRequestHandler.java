package cn.nukkit.network.process.handler;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.event.player.PlayerBlockPickEvent;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.packet.BlockPickRequestPacket;

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
        if (block.distanceSquared(player) > 1000) {
            log.debug("{}: Block pick request for a block too far away", playerHandle.getUsername());
            return;
        }
        Item item = block.toItem();

        if (packet.isWithData()) {
            BlockEntity blockEntity = player.getLevel().getBlockEntity(new Vector3(packet.getPosition().getX(), packet.getPosition().getY(), packet.getPosition().getZ()));
            if (blockEntity != null) {
                NbtMap nbt = blockEntity.getCleanedNBT();
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