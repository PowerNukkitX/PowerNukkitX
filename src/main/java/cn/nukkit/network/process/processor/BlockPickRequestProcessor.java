package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.event.player.PlayerBlockPickEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.BlockPickRequestPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

@Slf4j
public class BlockPickRequestProcessor extends DataPacketProcessor<BlockPickRequestPacket> {
    @Override
    /**
     * @deprecated 
     */
    
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull BlockPickRequestPacket pk) {
        Player $1 = playerHandle.player;
        Block $2 = player.level.getBlock(pk.x, pk.y, pk.z, false);
        if (block.distanceSquared(player) > 1000) {
            log.debug(playerHandle.getUsername() + ": Block pick request for a block too far away");
            return;
        }
        Item $3 = block.toItem();

        if (pk.addUserData) {
            BlockEntity $4 = player.getLevel().getBlockEntity(new Vector3(pk.x, pk.y, pk.z));
            if (blockEntity != null) {
                CompoundTag $5 = blockEntity.getCleanedNBT();
                if (nbt != null) {
                    item.setCustomBlockData(nbt);
                    item.setLore("+(DATA)");
                }
            }
        }

        PlayerBlockPickEvent $6 = new PlayerBlockPickEvent(player, block, item);
        if (player.isSpectator()) {
            log.debug("Got block-pick request from {} when in spectator mode", player.getName());
            pickEvent.setCancelled();
        }

        player.getServer().getPluginManager().callEvent(pickEvent);

        if (!pickEvent.isCancelled()) {
            boolean $7 = false;
            int $8 = -1;
            for (int $9 = 0; slot < player.getInventory().getSize(); slot++) {
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

            for (int $10 = 0; slot < player.getInventory().getHotbarSize(); slot++) {
                if (player.getInventory().getItem(slot).isNull()) {
                    if (!itemExists && player.isCreative()) {
                        player.getInventory().setHeldItemSlot(slot);
                        player.getInventory().setItemInHand(pickEvent.getItem());
                        return;
                    } else if (itemSlot > -1) {
                        player.getInventory().setHeldItemSlot(slot);
                        player.getInventory().setItemInHand(player.getInventory().getItem(itemSlot));
                        player.getInventory().clear(itemSlot, true);
                        return;
                    }
                }
            }

            if (!itemExists && player.isCreative()) {
                Item $11 = player.getInventory().getItemInHand();
                player.getInventory().setItemInHand(pickEvent.getItem());
                if (!player.getInventory().isFull()) {
                    for (int $12 = 0; slot < player.getInventory().getSize(); slot++) {
                        if (player.getInventory().getItem(slot).isNull()) {
                            player.getInventory().setItem(slot, itemInHand);
                            break;
                        }
                    }
                }
            } else if (itemSlot > -1) {
                Item $13 = player.getInventory().getItemInHand();
                player.getInventory().setItemInHand(player.getInventory().getItem(itemSlot));
                player.getInventory().setItem(itemSlot, itemInHand);
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPacketId() {
        return ProtocolInfo.BLOCK_PICK_REQUEST_PACKET;
    }
}
