package org.powernukkitx.network.process.handler;

import org.powernukkitx.Player;
import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntitySpawnable;
import org.powernukkitx.math.Vector3;
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

        Vector3 pos = new Vector3(packet.getBlockPosition().getX(), packet.getBlockPosition().getY(), packet.getBlockPosition().getZ());
        if (pos.distanceSquared(player) > 10000) {
            return;
        }
        player.resetInventory();

        BlockEntity t = player.level.getBlockEntity(pos);
        if (t instanceof BlockEntitySpawnable) {
            NbtMap nbt = packet.getActorDataTags();
            if (!((BlockEntitySpawnable) t).updateCompoundTag(nbt, player)) {
                ((BlockEntitySpawnable) t).spawnTo(player);
            }
        }
    }
}