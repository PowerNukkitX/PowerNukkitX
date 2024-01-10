package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityStructBlock;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.StructureBlockUpdatePacket;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.STRUCTURE_BLOCK_TYPE;

public class StructureBlockUpdateProcessor extends DataPacketProcessor<StructureBlockUpdatePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull StructureBlockUpdatePacket pk) {
        if (playerHandle.player.isOp() && playerHandle.player.isCreative()) {
            BlockEntity blockEntity = playerHandle.player.level.getBlockEntity(new Vector3(pk.blockPosition.x,
                    pk.blockPosition.y,
                    pk.blockPosition.z));
            if (blockEntity instanceof BlockEntityStructBlock structBlock) {
                Block sBlock = structBlock.getLevelBlock();
                sBlock.setPropertyValue(STRUCTURE_BLOCK_TYPE, pk.editorData.getType());
                structBlock.updateSetting(pk);
                playerHandle.player.level.setBlock(structBlock, sBlock, true);
                structBlock.spawnTo(playerHandle.player);
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.STRUCTURE_BLOCK_UPDATE_PACKET;
    }
}
