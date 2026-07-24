package org.powernukkitx.network.process.handler;

import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.BlockLectern;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityLectern;
import org.powernukkitx.event.block.LecternPageChangeEvent;
import org.powernukkitx.math.BlockVector3;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.LecternUpdatePacket;

/**
 * @author Kaooot
 */
public class LecternUpdateHandler implements PacketHandler<LecternUpdatePacket> {

    @Override
    public void handle(LecternUpdatePacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();

        BlockVector3 blockPosition = BlockVector3.fromNetwork(packet.getPositionOfLecternToUpdate());

        Vector3 pos = new Vector3(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
        if (pos.distanceSquared(holder.getPlayer()) > 10000) {
            return;
        }

        playerHandle.player.temporalVector.setComponents(blockPosition.x, blockPosition.y, blockPosition.z);
        BlockEntity blockEntityLectern = playerHandle.player.level.getBlockEntity(playerHandle.player.temporalVector);

        if (blockEntityLectern instanceof BlockEntityLectern lectern) {
            LecternPageChangeEvent lecternPageChangeEvent = new LecternPageChangeEvent(playerHandle.player, lectern, packet.getNewPageToShow());
            playerHandle.player.getServer().getPluginManager().callEvent(lecternPageChangeEvent);
            if (!lecternPageChangeEvent.isCancelled()) {
                lectern.setRawPage(lecternPageChangeEvent.getNewRawPage());
                lectern.spawnToAll();
                Block blockLectern = lectern.getBlock();
                if (blockLectern instanceof BlockLectern) {
                    ((BlockLectern) blockLectern).executeRedstonePulse();
                }
            }
        }
    }
}
