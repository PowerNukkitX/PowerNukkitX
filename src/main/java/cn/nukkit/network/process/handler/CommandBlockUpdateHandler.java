package cn.nukkit.network.process.handler;

import cn.nukkit.PlayerHandle;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCommandBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.PacketHandler;
import cn.nukkit.network.process.PlayerSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.cloudburstmc.protocol.bedrock.data.payload.command.BlockCommandData;
import org.cloudburstmc.protocol.bedrock.data.payload.command.CommandBlockMode;
import org.cloudburstmc.protocol.bedrock.data.payload.command.CommandBlockUpdateTargetType;
import org.cloudburstmc.protocol.bedrock.packet.CommandBlockUpdatePacket;

/**
 * @author Kaooot
 */
@Slf4j
public class CommandBlockUpdateHandler implements PacketHandler<CommandBlockUpdatePacket> {

    @Override
    public void handle(CommandBlockUpdatePacket packet, PlayerSessionHolder holder, Server server) {
        final PlayerHandle playerHandle = holder.getPlayerHandle();
        if (!playerHandle.player.spawned || !playerHandle.player.isAlive()) {
            log.debug("Player {} tried to update a command block while not spawned or dead", playerHandle.getUsername());
            return;
        }
        if (playerHandle.player.isOp() && playerHandle.player.isCreative()) {
            if (packet.getTarget().getType().equals(CommandBlockUpdateTargetType.BLOCK)) {
                final BlockCommandData blockCommandData = (BlockCommandData) packet.getTarget();
                BlockEntity blockEntity = playerHandle.player.level.getBlockEntity(new Vector3(blockCommandData.getBlockPosition().getX(), blockCommandData.getBlockPosition().getY(), blockCommandData.getBlockPosition().getZ()));
                if (blockEntity instanceof BlockEntityCommandBlock commandBlock) {
                    Block cmdBlock = commandBlock.getLevelBlock();

                    //change commandblock type
                    switch (blockCommandData.getCommandBlockMode()) {
                        case REPEATING:
                            if (cmdBlock.getId() != BlockID.REPEATING_COMMAND_BLOCK) {
                                cmdBlock = Block.get(BlockID.REPEATING_COMMAND_BLOCK).setPropertyValues(cmdBlock.getPropertyValues());
                                commandBlock.scheduleUpdate();
                            }
                            break;
                        case CHAIN:
                            if (cmdBlock.getId() != BlockID.CHAIN_COMMAND_BLOCK) {
                                cmdBlock = Block.get(BlockID.CHAIN_COMMAND_BLOCK).setPropertyValues(cmdBlock.getPropertyValues());
                            }
                            break;
                        case NORMAL:
                        default:
                            if (cmdBlock.getId() != BlockID.COMMAND_BLOCK) {
                                cmdBlock = Block.get(BlockID.COMMAND_BLOCK).setPropertyValues(cmdBlock.getPropertyValues());
                            }
                            break;
                    }

                    boolean conditional = blockCommandData.isConditional();
                    cmdBlock.setPropertyValue(CommonBlockProperties.CONDITIONAL_BIT, conditional);

                    commandBlock.setCommand(packet.getCommand());
                    commandBlock.setName(packet.getName());
                    commandBlock.setTrackOutput(packet.isTrackOutput());
                    commandBlock.setConditional(conditional);
                    commandBlock.setTickDelay((int) packet.getTickDelay());
                    commandBlock.setExecutingOnFirstTick(packet.isExecuteOnFirstTick());

                    //redstone mode / auto
                    boolean isRedstoneMode = blockCommandData.isRedstoneMode();
                    commandBlock.setAuto(!isRedstoneMode);
                    if (!isRedstoneMode && blockCommandData.getCommandBlockMode().equals(CommandBlockMode.NORMAL)) {
                        commandBlock.trigger();
                    }
                    commandBlock.getLevelBlockAround().forEach(b -> b.onUpdate(Level.BLOCK_UPDATE_REDSTONE));//update redstone
                    playerHandle.player.level.setBlock(commandBlock, cmdBlock, true);
                }
            }
        }
    }
}