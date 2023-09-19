package cn.nukkit.network.process.processor;

import cn.nukkit.block.Block;
import cn.nukkit.block.impl.BlockStructure;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityStructBlock;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.DataPacketProcessor;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.network.protocol.StructureBlockUpdatePacket;
import cn.nukkit.player.Player;
import cn.nukkit.player.PlayerHandle;
import org.jetbrains.annotations.NotNull;

public class StructureBlockUpdateProcessor extends DataPacketProcessor<StructureBlockUpdatePacket> {

    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull StructureBlockUpdatePacket pk) {
        Player player = playerHandle.getPlayer();
        if (player.isOp() && player.isCreative()) {
            BlockEntity blockEntity = player.getLevel()
                    .getBlockEntity(new Vector3(pk.blockPosition.x, pk.blockPosition.y, pk.blockPosition.z));
            if (blockEntity instanceof BlockEntityStructBlock structBlock) {
                Block sBlock = structBlock.getLevelBlock();
                sBlock.setPropertyValue(BlockStructure.STRUCTURE_BLOCK_TYPE, pk.editorData.getType());
                structBlock.updateSetting(pk);
                player.getLevel().setBlock(structBlock, sBlock, true);
                structBlock.spawnTo(player);
            }
        }
    }

    @Override
    public int getPacketId() {
        return ProtocolInfo.toNewProtocolID(ProtocolInfo.STRUCTURE_BLOCK_UPDATE_PACKET);
    }
}
