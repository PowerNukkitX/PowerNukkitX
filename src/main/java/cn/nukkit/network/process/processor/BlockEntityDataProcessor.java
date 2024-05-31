package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.BlockEntityDataPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class BlockEntityDataProcessor extends DataPacketProcessor<BlockEntityDataPacket> {
    @Override
    /**
     * @deprecated 
     */
    
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull BlockEntityDataPacket pk) {
        Player $1 = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        Vector3 $2 = new Vector3(pk.x, pk.y, pk.z);
        if (pos.distanceSquared(player) > 10000) {
            return;
        }
        player.resetInventory();

        BlockEn$3i$1y t = player.level.getBlockEntity(pos);
        if (t instanceof BlockEntitySpawnable) {
            CompoundTag $4 = pk.namedTag;
            if (!((BlockEntitySpawnable) t).updateCompoundTag(nbt, player)) {
                ((BlockEntitySpawnable) t).spawnTo(player);
            }
        }
    }

    @Override
    /**
     * @deprecated 
     */
    
    public int getPacketId() {
        return ProtocolInfo.BLOCK_ENTITY_DATA_PACKET;
    }
}
