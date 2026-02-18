package cn.nukkit.network.process.processor;

import cn.nukkit.PlayerHandle;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCommandBlock;
import cn.nukkit.blockentity.ICommandBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.process.DataPacketProcessor;
import org.cloudburstmc.protocol.bedrock.data.CommandBlockMode;
import org.cloudburstmc.protocol.bedrock.packet.CommandBlockUpdatePacket;
import org.jetbrains.annotations.NotNull;

public class CommandBlockUpdateProcessor extends DataPacketProcessor<CommandBlockUpdatePacket> {
    @Override
    public void handle(@NotNull PlayerHandle playerHandle, @NotNull CommandBlockUpdatePacket pk) {
        if (playerHandle.player.isOp() && playerHandle.player.isCreative()) {
            if (pk.isBlock()) {
                var blockPosition = pk.getBlockPosition();
                BlockEntity blockEntity = playerHandle.player.level.getBlockEntity(new Vector3(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()));
                if (blockEntity instanceof BlockEntityCommandBlock commandBlock) {
                    Block cmdBlock = commandBlock.getLevelBlock();

                    //change commandblock type
                    switch (pk.getMode()) {
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

                    boolean conditional = pk.isConditional();
                    cmdBlock.setPropertyValue(CommonBlockProperties.CONDITIONAL_BIT, conditional);

                    commandBlock.setCommand(pk.getCommand());
                    commandBlock.setName(pk.getName());
                    commandBlock.setTrackOutput(pk.isOutputTracked());
                    commandBlock.setConditional(conditional);
                    commandBlock.setTickDelay((int) pk.getTickDelay());
                    commandBlock.setExecutingOnFirstTick(pk.isExecutingOnFirstTick());

                    //redstone mode / auto
                    boolean isRedstoneMode = pk.isRedstoneMode();
                    commandBlock.setAuto(!isRedstoneMode);
                    if (!isRedstoneMode && pk.getMode() == CommandBlockMode.NORMAL) {
                        commandBlock.trigger();
                    }
                    commandBlock.getLevelBlockAround().forEach(b -> b.onUpdate(Level.BLOCK_UPDATE_REDSTONE));//update redstone
                    playerHandle.player.level.setBlock(commandBlock, cmdBlock, true);
                }
            }
        }
    }

    @Override
    public Class<CommandBlockUpdatePacket> getPacketClass() {
        return CommandBlockUpdatePacket.class;
    }
}
