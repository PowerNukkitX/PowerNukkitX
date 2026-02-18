package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.block.Block;
import cn.nukkit.block.property.enums.StructureBlockType;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityStructBlock;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.packet.StructureBlockUpdatePacket;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.STRUCTURE_BLOCK_TYPE;

public class StructureBlockUpdateProcessor extends DataPacketProcessor<StructureBlockUpdatePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull StructureBlockUpdatePacket pk) {
        if (playerHandle.player.isOp() && playerHandle.player.isCreative()) {
            var blockPos = pk.getBlockPosition();
            BlockEntity blockEntity = playerHandle.player.level.getBlockEntity(new Vector3(blockPos.getX(),
                    blockPos.getY(),
                    blockPos.getZ()));
            if (blockEntity instanceof BlockEntityStructBlock structBlock) {
                Block sBlock = structBlock.getLevelBlock();
                sBlock.setPropertyValue(STRUCTURE_BLOCK_TYPE, StructureBlockType.from(pk.getEditorData().getType().ordinal()));
                structBlock.updateSetting(pk);
                playerHandle.player.level.setBlock(structBlock, sBlock, true);
                structBlock.spawnTo(playerHandle.player);
            }
        }
    }
    @Override
    public Class<StructureBlockUpdatePacket> getPacketClass() {
        return StructureBlockUpdatePacket.class;
    }
}
