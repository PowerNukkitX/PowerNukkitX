package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockLectern;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.event.block.LecternPageChangeEvent;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.LecternUpdatePacket;
import org.jetbrains.annotations.NotNull;

public class LecternUpdateProcessor extends DataPacketProcessor<LecternUpdatePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull LecternUpdatePacket pk) {
        var networkPos = pk.getBlockPosition();
        BlockVector3 blockPosition = new BlockVector3(networkPos.getX(), networkPos.getY(), networkPos.getZ());
        playerHandle.player.temporalVector.setComponents(blockPosition.x, blockPosition.y, blockPosition.z);
        BlockEntity blockEntityLectern = playerHandle.player.level.getBlockEntity(playerHandle.player.temporalVector);
        if (blockEntityLectern instanceof BlockEntityLectern lectern) {
            LecternPageChangeEvent lecternPageChangeEvent = new LecternPageChangeEvent(playerHandle.player, lectern, pk.getPage());
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

    @Override
    public Class<LecternUpdatePacket> getPacketClass() {
        return LecternUpdatePacket.class;
    }
}
