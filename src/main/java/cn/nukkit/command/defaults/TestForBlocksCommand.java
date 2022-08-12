package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.command.utils.CommandParser;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.TextFormat;

import static cn.nukkit.utils.Utils.getLevelBlocks;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class TestForBlocksCommand extends VanillaCommand {

    public TestForBlocksCommand(String name) {
        super(name, "commands.testforblocks.description");
        this.setPermission("nukkit.command.testforblocks");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("begin",false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("end",false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("destination",false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("mode", true, new String[]{"all","masked"})
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

            if (parser.hasNext()) {
                mode = parser.parseEnum(TestForBlocksMode.class);
            }

            AxisAlignedBB blocksAABB = new SimpleAxisAlignedBB(Math.min(begin.getX(), end.getX()), Math.min(begin.getY(), end.getY()), Math.min(begin.getZ(), end.getZ()), Math.max(begin.getX(), end.getX()), Math.max(begin.getY(), end.getY()), Math.max(begin.getZ(), end.getZ()));
            int size = NukkitMath.floorDouble((blocksAABB.getMaxX() - blocksAABB.getMinX() + 1) * (blocksAABB.getMaxY() - blocksAABB.getMinY() + 1) * (blocksAABB.getMaxZ() - blocksAABB.getMinZ() + 1));

            if (size > 16 * 16 * 256 * 8) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.fill.tooManyBlocks", String.valueOf(size),String.valueOf(16 * 16 * 256 * 8)));
                sender.sendMessage(TextFormat.RED + "Operation will continue, but too many blocks may cause stuttering");
            }

            Position to = new Position(destination.getX() + (blocksAABB.getMaxX() - blocksAABB.getMinX()), destination.getY() + (blocksAABB.getMaxY() - blocksAABB.getMinY()), destination.getZ() + (blocksAABB.getMaxZ() - blocksAABB.getMinZ()));
            AxisAlignedBB destinationAABB = new SimpleAxisAlignedBB(Math.min(destination.getX(), to.getX()), Math.min(destination.getY(), to.getY()), Math.min(destination.getZ(), to.getZ()), Math.max(destination.getX(), to.getX()), Math.max(destination.getY(), to.getY()), Math.max(destination.getZ(), to.getZ()));

            if (blocksAABB.getMinY() < 0 || blocksAABB.getMaxY() > 255 || destinationAABB.getMinY() < 0 || destinationAABB.getMaxY() > 255) {
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.testforblock.outOfWorld"));
                return false;
            }

            Level level = begin.getLevel();

            for (int sourceChunkX = NukkitMath.floorDouble(blocksAABB.getMinX()) >> 4, destinationChunkX = NukkitMath.floorDouble(destinationAABB.getMinX()) >> 4; sourceChunkX <= NukkitMath.floorDouble(blocksAABB.getMaxX()) >> 4; sourceChunkX++, destinationChunkX++) {
                for (int sourceChunkZ = NukkitMath.floorDouble(blocksAABB.getMinZ()) >> 4, destinationChunkZ = NukkitMath.floorDouble(destinationAABB.getMinZ()) >> 4; sourceChunkZ <= NukkitMath.floorDouble(blocksAABB.getMaxZ()) >> 4; sourceChunkZ++, destinationChunkZ++) {
                    if (level.getChunkIfLoaded(sourceChunkX, sourceChunkZ) == null) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.testforblock.outOfWorld"));
                        return false;
                    }
                    if (level.getChunkIfLoaded(destinationChunkX, destinationChunkZ) == null) {
                        sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.testforblock.outOfWorld"));
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

                        if (block.equalsBlock(destinationBlock)) {
                            ++count;
                        } else {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.compare.failed"));
                            return false;
                        }
                    }

                    break;
                case MASKED:
                    for (int i = 0; i < blocks.length; i++) {
                        Block block = blocks[i];
                        Block destinationBlock = destinationBlocks[i];

                        if (block.equalsBlock(destinationBlock)) {
                            ++count;
                        } else if (block.getId() != Block.AIR) {
                            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.compare.failed"));
                            return false;
                        }
                    }

                    break;
            }

            sender.sendMessage(new TranslationContainer("%commands.compare.success", String.valueOf(count)));
        } catch (CommandSyntaxException e) {
             sender.sendMessage(new TranslationContainer("commands.generic.usage", "\n" + this.getCommandFormatTips()));
            return false;
        }

        return true;
    }

    public enum TestForBlocksMode {
        ALL,
        MASKED
    }
}
