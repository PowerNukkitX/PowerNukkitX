package cn.nukkit.network.process.processor;

import cn.nukkit.Player;
import cn.nukkit.PlayerHandle;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.BlockEntityDataPacket;
import org.jetbrains.annotations.NotNull;

public class BlockEntityDataProcessor extends DataPacketProcessor<BlockEntityDataPacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull BlockEntityDataPacket pk) {
        Player player = playerHandle.player;
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        var blockPosition = pk.getBlockPosition();
        Vector3 pos = new Vector3(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
        if (pos.distanceSquared(player) > 10000) {
            return;
        }
        player.resetInventory();

        BlockEntity t = player.level.getBlockEntity(pos);
        if (t instanceof BlockEntitySpawnable) {
            ((BlockEntitySpawnable) t).spawnTo(player);
        }
    }

    @Override
    public Class<BlockEntityDataPacket> getPacketClass() {
        return BlockEntityDataPacket.class;
    }
}
