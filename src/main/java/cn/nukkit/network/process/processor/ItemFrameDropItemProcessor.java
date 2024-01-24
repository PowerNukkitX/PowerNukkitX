package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ItemFrameDropItemPacket;
import cn.nukkit.network.protocol.ProtocolInfo;
import org.jetbrains.annotations.NotNull;

public class ItemFrameDropItemProcessor extends DataPacketProcessor<ItemFrameDropItemPacket> {

    // PowerNukkit Note: This packed is not being sent anymore since 1.16.210
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull ItemFrameDropItemPacket pk) {
        Vector3 vector3 = playerHandle.player.temporalVector.setComponents(pk.x, pk.y, pk.z);
        if (vector3.distanceSquared(playerHandle.player) < 1000) {
            BlockEntity itemFrame = playerHandle.player.level.getBlockEntity(vector3);
            if (itemFrame instanceof BlockEntityItemFrame) {
                ((BlockEntityItemFrame) itemFrame).dropItem(playerHandle.player);
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET;
    }
}
