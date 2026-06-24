package cn.nukkit.network.process.handler;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.property.enums.StructureBlockType;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityStructBlock;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import org.cloudburstmc.protocol.bedrock.packet.StructureBlockUpdatePacket;

import static cn.nukkit.block.property.CommonBlockProperties.STRUCTURE_BLOCK_TYPE;

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