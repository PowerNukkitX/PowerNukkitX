package org.powernukkitx.network.process.handler;

import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.block.Block;
import org.powernukkitx.block.property.enums.StructureBlockType;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityStructBlock;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.payload.structure.StructureEditorData;
import org.cloudburstmc.protocol.bedrock.packet.StructureBlockUpdatePacket;
import org.cloudburstmc.math.vector.Vector3i;

import static org.powernukkitx.block.property.CommonBlockProperties.STRUCTURE_BLOCK_TYPE;

/**
 * @author Kaooot
 */
@Slf4j
public class StructureBlockUpdateHandler implements PacketHandler<StructureBlockUpdatePacket> {

    @Override
    public void handle(StructureBlockUpdatePacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        if (!playerHandle.player.isOp() || !playerHandle.player.isCreative()) {
            return;
        }

        final Vector3i blockPosition = packet.getBlockPosition();
        final StructureEditorData structureData = packet.getStructureData();
        if (blockPosition == null || structureData == null || structureData.getStructureBlockType() == null) {
            log.debug("Player {} sent a malformed StructureBlockUpdatePacket", playerHandle.player.getName());
            return;
        }

        final StructureBlockType blockType;
        try {
            blockType = StructureBlockType.valueOf(structureData.getStructureBlockType().name());
        } catch (IllegalArgumentException e) {
            log.debug("Player {} sent an unknown structure block type {}", playerHandle.player.getName(),
                    structureData.getStructureBlockType());
            return;
        }

        BlockEntity blockEntity = playerHandle.player.level.getBlockEntity(
                new Vector3(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ())
        );
        if (blockEntity instanceof BlockEntityStructBlock structBlock) {
            Block sBlock = structBlock.getLevelBlock();
            sBlock.setPropertyValue(STRUCTURE_BLOCK_TYPE, blockType);
            structBlock.updateSetting(packet);
            playerHandle.player.level.setBlock(structBlock, sBlock, true);
            structBlock.spawnTo(playerHandle.player);
        }
    }
}