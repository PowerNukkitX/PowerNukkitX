package org.powernukkitx.network.process.handler;

import org.powernukkitx.PlayerHandle;
import org.powernukkitx.Server;
import org.powernukkitx.block.BlockStructureBlock;
import org.powernukkitx.block.property.enums.StructureBlockType;
import org.powernukkitx.blockentity.BlockEntity;
import org.powernukkitx.blockentity.BlockEntityStructureBlock;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.network.process.PacketHandler;
import org.powernukkitx.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.StructureBlockUpdatePacket;

/**
 * @author Kaooot
 */
public class StructureBlockUpdateHandler implements PacketHandler<StructureBlockUpdatePacket> {

    @Override
    public void handle(StructureBlockUpdatePacket packet, PlayerSessionHolder holder, Server server) {
        PlayerHandle playerHandle = holder.getPlayerHandle();
        if (playerHandle.player.isOp() && playerHandle.player.isCreative()) {
            Vector3 blockPosition = new Vector3(
                    packet.getBlockPosition().getX(),
                    packet.getBlockPosition().getY(),
                    packet.getBlockPosition().getZ()
            );
            BlockEntity blockEntity = playerHandle.player.level.getBlockEntity(blockPosition);
            if (blockEntity instanceof BlockEntityStructureBlock structBlock) {
                structBlock.updateSetting(playerHandle.player, packet);
                if (structBlock.closed || playerHandle.player.level.getBlockEntity(blockPosition) != structBlock) return;

                StructureBlockType type = StructureBlockType.from(packet.getStructureData().getStructureBlockType().ordinal());
                if (playerHandle.player.level.getBlock(blockPosition) instanceof BlockStructureBlock structureBlock
                        && structureBlock.getStructureBlockType() != type) {
                    structureBlock.setStructureBlockType(type);
                    playerHandle.player.level.setBlock(structureBlock, structureBlock, true, false);
                }
                structBlock.spawnToAll();
                playerHandle.player.finishEditorWindow(structBlock.getInventory());
            }
        }
    }
}
