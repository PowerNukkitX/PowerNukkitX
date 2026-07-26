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
import org.cloudburstmc.protocol.bedrock.packet.StructureBlockUpdatePacket;

import static org.powernukkitx.block.property.CommonBlockProperties.STRUCTURE_BLOCK_TYPE;

/**
 * @author Kaooot
 */
public class StructureBlockUpdateHandler implements PacketHandler<StructureBlockUpdatePacket> {

    @Override
    public void handle(StructureBlockUpdatePacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        if (playerHandle.player.isOp() && playerHandle.player.isCreative()) {
            BlockEntity blockEntity = playerHandle.player.level.getBlockEntity(
                    new Vector3(
                            packet.getBlockPosition().getX(),
                            packet.getBlockPosition().getY(),
                            packet.getBlockPosition().getZ()
                    )
            );
            if (blockEntity instanceof BlockEntityStructBlock structBlock) {
                Block sBlock = structBlock.getLevelBlock();
                sBlock.setPropertyValue(STRUCTURE_BLOCK_TYPE, StructureBlockType.valueOf(packet.getStructureData().getStructureBlockType().name()));
                structBlock.updateSetting(packet);
                playerHandle.player.level.setBlock(structBlock, sBlock, true);
                structBlock.spawnTo(playerHandle.player);
            }
        }
    }
}