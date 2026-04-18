package cn.nukkit.network.process.handler;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLectern;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.event.block.LecternPageChangeEvent;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.LecternUpdatePacket;

/**
 * @author Kaooot
 */
public class LecternUpdateHandler implements PacketHandler<LecternUpdatePacket> {

    @Override
    public void handle(LecternUpdatePacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        BlockVector3 blockPosition = BlockVector3.fromNetwork(packet.getPositionOfLecternToUpdate());
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