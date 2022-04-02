package cn.nukkit.command.defaults;

import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.utils.CommandParser;
import cn.nukkit.utils.CommandSyntaxException;
import cn.nukkit.utils.TextFormat;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.nukkit.utils.Utils.getLevelBlocks;

public class TestForBlocksCommand extends VanillaCommand {

    public TestForBlocksCommand(String name) {
        super(name, "commands.testforblocks.description", "commands.testforblocks.usage");
        this.setPermission("nukkit.command.testforblocks");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("begin",false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("end",false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("destination",false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("mode", true, Arrays.stream(TestForBlocksMode.values()).map(m -> m.name()).collect(Collectors.toList()).toArray(new String[0]))
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        CommandParser parser = new CommandParser(this, sender, args);
        try {
            Position begin = parser.parsePosition().floor();
            Position end = parser.parsePosition().floor();
            Position destination = parser.parsePosition().floor();
            TestForBlocksMode mode = TestForBlocksMode.ALL;

            if (args.length > 9) {
                mode = parser.parseEnum(TestForBlocksMode.class);
            }

            AxisAlignedBB blocksAABB = new SimpleAxisAlignedBB(Math.min(begin.getX(), end.getX()), Math.min(begin.getY(), end.getY()), Math.min(begin.getZ(), end.getZ()), Math.max(begin.getX(), end.getX()), Math.max(begin.getY(), end.getY()), Math.max(begin.getZ(), end.getZ()));
            int size = NukkitMath.floorDouble((blocksAABB.getMaxX() - blocksAABB.getMinX() + 1) * (blocksAABB.getMaxY() - blocksAABB.getMinY() + 1) * (blocksAABB.getMaxZ() - blocksAABB.getMinZ() + 1));

            if (size > 16 * 16 * 256 * 8) {
                sender.sendMessage(String.format(TextFormat.RED + "Too many blocks in the specified area (%1$d > %2$d)", size, 16 * 16 * 256 * 8));
                return false;
            }

            Position to = new Position(destination.getX() + (blocksAABB.getMaxX() - blocksAABB.getMinX()), destination.getY() + (blocksAABB.getMaxY() - blocksAABB.getMinY()), destination.getZ() + (blocksAABB.getMaxZ() - blocksAABB.getMinZ()));
            AxisAlignedBB destinationAABB = new SimpleAxisAlignedBB(Math.min(destination.getX(), to.getX()), Math.min(destination.getY(), to.getY()), Math.min(destination.getZ(), to.getZ()), Math.max(destination.getX(), to.getX()), Math.max(destination.getY(), to.getY()), Math.max(destination.getZ(), to.getZ()));

            if (blocksAABB.getMinY() < 0 || blocksAABB.getMaxY() > 255 || destinationAABB.getMinY() < 0 || destinationAABB.getMaxY() > 255) {
                sender.sendMessage(TextFormat.RED + "Cannot access blocks outside of the world");
                return false;
            }

            Level level = begin.getLevel();

            for (int sourceChunkX = NukkitMath.floorDouble(blocksAABB.getMinX()) >> 4, destinationChunkX = NukkitMath.floorDouble(destinationAABB.getMinX()) >> 4; sourceChunkX <= NukkitMath.floorDouble(blocksAABB.getMaxX()) >> 4; sourceChunkX++, destinationChunkX++) {
                for (int sourceChunkZ = NukkitMath.floorDouble(blocksAABB.getMinZ()) >> 4, destinationChunkZ = NukkitMath.floorDouble(destinationAABB.getMinZ()) >> 4; sourceChunkZ <= NukkitMath.floorDouble(blocksAABB.getMaxZ()) >> 4; sourceChunkZ++, destinationChunkZ++) {
                    if (level.getChunkIfLoaded(sourceChunkX, sourceChunkZ) == null) {
                        sender.sendMessage(TextFormat.RED + "Cannot access blocks outside of the world");
                        return false;
                    }
                    if (level.getChunkIfLoaded(destinationChunkX, destinationChunkZ) == null) {
                        sender.sendMessage(TextFormat.RED + "Cannot access blocks outside of the world");
                        return false;
                    }
                }
            }

            Block[] blocks = getLevelBlocks(level, blocksAABB);
            Block[] destinationBlocks = getLevelBlocks(level, destinationAABB);
            int count = 0;

            switch (mode) {
                case ALL:
                    for (int i = 0; i < blocks.length; i++) {
                        Block block = blocks[i];
                        Block destinationBlock = destinationBlocks[i];

                        if (block.getId() == destinationBlock.getId() && block.getDamage() == destinationBlock.getDamage()) {
                            ++count;
                        } else {
                            sender.sendMessage(TextFormat.RED + "Source and destination are not identical");
                            return false;
                        }
                    }

                    break;
                case MASKED:
                    for (int i = 0; i < blocks.length; i++) {
                        Block block = blocks[i];
                        Block destinationBlock = destinationBlocks[i];

                        if (block.getId() == destinationBlock.getId() && block.getDamage() == destinationBlock.getDamage()) {
                            ++count;
                        } else if (block.getId() != Block.AIR) {
                            sender.sendMessage(TextFormat.RED + "Source and destination are not identical");
                            return false;
                        }
                    }

                    break;
            }

            sender.sendMessage(String.format("%1$d blocks compared", count));
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
            return false;
        }

        return true;
    }

    private enum TestForBlocksMode {
        ALL,
        MASKED
    }
}
