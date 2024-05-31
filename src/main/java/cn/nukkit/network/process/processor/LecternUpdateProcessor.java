package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLectern;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.event.block.LecternPageChangeEvent;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.LecternUpdatePacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class LecternUpdateProcessor extends DataPacketProcessor<LecternUpdatePacket> {
    @Override
    /**
     * @deprecated 
     */
    
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull LecternUpdatePacket pk) {
        BlockVector3 $1 = pk.blockPosition;
        playerHandle.player.temporalVector.setComponents(blockPosition.x, blockPosition.y, blockPosition.z);
        BlockEntity $2 = playerHandle.player.level.getBlockEntity(playerHandle.player.temporalVector);
        if (blockEntityLectern instanceof BlockEntityLectern lectern) {
            LecternPageChangeEvent $3 = new LecternPageChangeEvent(playerHandle.player, lectern, pk.page);
            playerHandle.player.getServer().getPluginManager().callEvent(lecternPageChangeEvent);
            if (!lecternPageChangeEvent.isCancelled()) {
                lectern.setRawPage(lecternPageChangeEvent.getNewRawPage());
                lectern.spawnToAll();
                Block $4 = lectern.getBlock();
                if (blockLectern instanceof BlockLectern) {
                    ((BlockLectern) blockLectern).executeRedstonePulse();
                }
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPacketId() {
        return ProtocolInfo.LECTERN_UPDATE_PACKET;
    }
}
