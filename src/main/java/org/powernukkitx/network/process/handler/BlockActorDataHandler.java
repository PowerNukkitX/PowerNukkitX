package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntitySpawnable;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.packet.BlockActorDataPacket;

/**
 * @author Kaooot
 */
public class BlockActorDataHandler implements PacketHandler<BlockActorDataPacket> {

    @Override
    public void handle(BlockActorDataPacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        BlockVector3 position = BlockVector3.fromNetwork(packet.getBlockPosition());
        player.temporalVector.setComponents(position.x, position.y, position.z);
        if (!player.canInteract(player.temporalVector.add(0.5, 0.5, 0.5))) {
            return;
        }

        player.resetInventory();

        BlockEntity t = player.level.getBlockEntity(position);
        if (t instanceof BlockEntitySpawnable spawnable) {
            NbtMap nbt = packet.getActorDataTags();
            if (!spawnable.updateCompoundTag(nbt, player)) {
                spawnable.spawnTo(player);
            }
        }
    }
}
