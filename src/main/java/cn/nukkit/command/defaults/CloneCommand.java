package cn.nukkit.command.defaults;

import cn.nukkit.block.Block;
import cn.nukkit.command.CommandParser;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.exceptions.CommandSyntaxException;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;

import static cn.nukkit.utils.Utils.getLevelBlocks;

public class CloneCommand extends VanillaCommand {

    public CloneCommand(String name) {
        super(name, "commands.clone.description", "commands.clone.usage");
        this.setPermission("nukkit.command.clone");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("begin", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("end", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("destination", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("maskMode", true, new String[]{"masked", "replace"}),
                CommandParameter.newEnum("cloneMode", true, new String[]{"force", "move", "normal"})});
        this.addCommandParameters("filtered", new CommandParameter[]{
                CommandParameter.newType("begin", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("end", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("destination", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("maskMode", false, new String[]{"filtered"}),
                CommandParameter.newEnum("cloneMode", false, new String[]{"force", "move", "normal"}),
                CommandParameter.newType("tileId", false, CommandParamType.INT),
                CommandParameter.newType("tileData", false, CommandParamType.INT)
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
            MaskMode maskMode = MaskMode.REPLACE;
            CloneMode cloneMode = CloneMode.NORMAL;
            int tileId = 0;
            int tileData = 0;

            if (args.length > 9) {
                maskMode = parser.parseEnum(MaskMode.class);
                if (args.length > 10) {
                    cloneMode = parser.parseEnum(CloneMode.class);
                    if (args.length > 11) {
                        tileId = parser.parseInt();
                        tileData = parser.parseInt();
                    }
                }
            }

            AxisAlignedBB blocksAABB = new SimpleAxisAlignedBB(Math.min(begin.getX(), end.getX()), Math.min(begin.getY(), end.getY()), Math.min(begin.getZ(), end.getZ()), Math.max(begin.getX(), end.getX()), Math.max(begin.getY(), end.getY()), Math.max(begin.getZ(), end.getZ()));
            int size = NukkitMath.floorDouble((blocksAABB.getMaxX() - blocksAABB.getMinX() + 1) * (blocksAABB.getMaxY() - blocksAABB.getMinY() + 1) * (blocksAABB.getMaxZ() - blocksAABB.getMinZ() + 1));

            if (size > 16 * 16 * 256 * 8) {
                sender.sendMessage(new TranslationContainer("commands.clone.tooManyBlocks", String.valueOf(size), String.valueOf(16 * 16 * 256 * 8)));
                return false;
            }

            Position to = new Position(destination.getX() + (blocksAABB.getMaxX() - blocksAABB.getMinX()), destination.getY() + (blocksAABB.getMaxY() - blocksAABB.getMinY()), destination.getZ() + (blocksAABB.getMaxZ() - blocksAABB.getMinZ()));
            AxisAlignedBB destinationAABB = new SimpleAxisAlignedBB(Math.min(destination.getX(), to.getX()), Math.min(destination.getY(), to.getY()), Math.min(destination.getZ(), to.getZ()), Math.max(destination.getX(), to.getX()), Math.max(destination.getY(), to.getY()), Math.max(destination.getZ(), to.getZ()));

            if (blocksAABB.getMinY() < -64 || blocksAABB.getMaxY() > 320 || destinationAABB.getMinY() < -64 || destinationAABB.getMaxY() > 320) {
                sender.sendMessage(new TranslationContainer("commands.generic.outOfWorld"));
                return false;
            }
            if (blocksAABB.intersectsWith(destinationAABB) && cloneMode != CloneMode.FORCE) {
                sender.sendMessage(new TranslationContainer("commands.clone.noOverlap"));
                return false;
            }

            Level level = begin.getLevel();

            for (int sourceChunkX = NukkitMath.floorDouble(blocksAABB.getMinX()) >> 4, destinationChunkX = NukkitMath.floorDouble(destinationAABB.getMinX()) >> 4; sourceChunkX <= NukkitMath.floorDouble(blocksAABB.getMaxX()) >> 4; sourceChunkX++, destinationChunkX++) {
                for (int sourceChunkZ = NukkitMath.floorDouble(blocksAABB.getMinZ()) >> 4, destinationChunkZ = NukkitMath.floorDouble(destinationAABB.getMinZ()) >> 4; sourceChunkZ <= NukkitMath.floorDouble(blocksAABB.getMaxZ()) >> 4; sourceChunkZ++, destinationChunkZ++) {
                    if (level.getChunkIfLoaded(sourceChunkX, sourceChunkZ) == null) {
                        sender.sendMessage(new TranslationContainer("commands.generic.outOfWorld"));
                        return false;
                    }
                    if (level.getChunkIfLoaded(destinationChunkX, destinationChunkZ) == null) {
                        sender.sendMessage(new TranslationContainer("commands.generic.outOfWorld"));
                        return false;
                    }
                }
            }

            Block[] blocks = getLevelBlocks(level, blocksAABB);
            Block[] destinationBlocks = getLevelBlocks(level, destinationAABB);
            int count = 0;

            boolean move = cloneMode == CloneMode.MOVE;
            switch (maskMode) {
                case REPLACE:
                    for (int i = 0; i < blocks.length; i++) {
                        Block block = blocks[i];
                        Block destinationBlock = destinationBlocks[i];

                        level.setBlock(destinationBlock, Block.get(block.getId(), block.getDamage()));

                        ++count;

                        if (move) {
                            level.setBlock(block, Block.get(Block.AIR));
                        }
                    }

                    break;
                case MASKED:
                    for (int i = 0; i < blocks.length; i++) {
                        Block block = blocks[i];
                        Block destinationBlock = destinationBlocks[i];

                        if (block.getId() != Block.AIR) {
                            level.setBlock(destinationBlock, Block.get(block.getId(), block.getDamage()));
                            ++count;

                            if (move) {
                                level.setBlock(block, Block.get(Block.AIR));
                            }
                        }
                    }

                    break;
                case FILTERED:
                    for (int i = 0; i < blocks.length; i++) {
                        Block block = blocks[i];
                        Block destinationBlock = destinationBlocks[i];

                        if (block.getId() == tileId && block.getDamage() == tileData) {
                            level.setBlock(destinationBlock, Block.get(block.getId(), block.getDamage()));
                            ++count;

                            if (move) {
                                level.setBlock(block, Block.get(Block.AIR));
                            }
                        }
                    }

                    break;
            }

            if (count == 0) {
                sender.sendMessage(new TranslationContainer("commands.clone.failed"));
                return false;
            } else {
                sender.sendMessage(new TranslationContainer("commands.clone.success", String.valueOf(count)));
            }
        } catch (CommandSyntaxException e) {
            sender.sendMessage(parser.getErrorMessage());
            return false;
        }

        return true;
    }

    private enum MaskMode {
        REPLACE,
        MASKED,
        FILTERED
    }

    private enum CloneMode {
        NORMAL,
        FORCE,
        MOVE
    }
}
