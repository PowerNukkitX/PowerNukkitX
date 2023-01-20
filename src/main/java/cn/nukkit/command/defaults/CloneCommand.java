package cn.nukkit.command.defaults;

import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.tree.ParamTree;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;

import java.util.Locale;
import java.util.Map;

import static cn.nukkit.utils.Utils.getLevelBlocks;

@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class CloneCommand extends VanillaCommand {

    public CloneCommand(String name) {
        super(name, "commands.clone.description");
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
        this.paramTree = new ParamTree(this);
    }

    @Since("1.19.50-r4")
    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        Position begin = list.getResult(0);
        Position end = list.getResult(1);
        Position destination = list.getResult(2);
        MaskMode maskMode = MaskMode.REPLACE;
        CloneMode cloneMode = CloneMode.NORMAL;
        int tileId = 0;
        int tileData = 0;
        switch (result.getKey()) {
            case "default" -> {
                if (list.hasResult(3)) {
                    String str = list.getResult(3);
                    maskMode = MaskMode.valueOf(str.toUpperCase(Locale.ENGLISH));
                }
                if (list.hasResult(4)) {
                    String str = list.getResult(4);
                    cloneMode = CloneMode.valueOf(str.toUpperCase(Locale.ENGLISH));
                }
            }
            case "filtered" -> {
                maskMode = MaskMode.FILTERED;
                String str = list.getResult(4);
                cloneMode = CloneMode.valueOf(str.toUpperCase(Locale.ENGLISH));
                tileId = list.getResult(5);
                tileData = list.getResult(6);
            }
            default -> {
                return 0;
            }
        }
        AxisAlignedBB blocksAABB = new SimpleAxisAlignedBB(Math.min(begin.getX(), end.getX()), Math.min(begin.getY(), end.getY()), Math.min(begin.getZ(), end.getZ()), Math.max(begin.getX(), end.getX()), Math.max(begin.getY(), end.getY()), Math.max(begin.getZ(), end.getZ()));
        int size = NukkitMath.floorDouble((blocksAABB.getMaxX() - blocksAABB.getMinX() + 1) * (blocksAABB.getMaxY() - blocksAABB.getMinY() + 1) * (blocksAABB.getMaxZ() - blocksAABB.getMinZ() + 1));

        if (size > 16 * 16 * 256 * 8) {
            log.addError("commands.clone.tooManyBlocks", String.valueOf(size), String.valueOf(16 * 16 * 256 * 8));
            log.addError("Operation will continue, but too many blocks may cause stuttering");
            log.output();
        }

        Position to = new Position(destination.getX() + (blocksAABB.getMaxX() - blocksAABB.getMinX()), destination.getY() + (blocksAABB.getMaxY() - blocksAABB.getMinY()), destination.getZ() + (blocksAABB.getMaxZ() - blocksAABB.getMinZ()));
        AxisAlignedBB destinationAABB = new SimpleAxisAlignedBB(Math.min(destination.getX(), to.getX()), Math.min(destination.getY(), to.getY()), Math.min(destination.getZ(), to.getZ()), Math.max(destination.getX(), to.getX()), Math.max(destination.getY(), to.getY()), Math.max(destination.getZ(), to.getZ()));

        if (blocksAABB.getMinY() < -64 || blocksAABB.getMaxY() > 320 || destinationAABB.getMinY() < -64 || destinationAABB.getMaxY() > 320) {
            log.addOutOfWorld().output();
            return 0;
        }
        if (blocksAABB.intersectsWith(destinationAABB) && cloneMode != CloneMode.FORCE) {
            log.addError("commands.clone.noOverlap").output();
            return 0;
        }

        Level level = begin.getLevel();

        for (int sourceChunkX = NukkitMath.floorDouble(blocksAABB.getMinX()) >> 4, destinationChunkX = NukkitMath.floorDouble(destinationAABB.getMinX()) >> 4; sourceChunkX <= NukkitMath.floorDouble(blocksAABB.getMaxX()) >> 4; sourceChunkX++, destinationChunkX++) {
            for (int sourceChunkZ = NukkitMath.floorDouble(blocksAABB.getMinZ()) >> 4, destinationChunkZ = NukkitMath.floorDouble(destinationAABB.getMinZ()) >> 4; sourceChunkZ <= NukkitMath.floorDouble(blocksAABB.getMaxZ()) >> 4; sourceChunkZ++, destinationChunkZ++) {
                if (level.getChunkIfLoaded(sourceChunkX, sourceChunkZ) == null) {
                    log.addOutOfWorld().output();
                    return 0;
                }
                if (level.getChunkIfLoaded(destinationChunkX, destinationChunkZ) == null) {
                    log.addOutOfWorld().output();
                    return 0;
                }
            }
        }

        Block[] blocks = getLevelBlocks(level, blocksAABB);
        Block[] destinationBlocks = getLevelBlocks(level, destinationAABB);
        int count = 0;

        boolean move = cloneMode == CloneMode.MOVE;
        switch (maskMode) {
            case REPLACE -> {
                for (int i = 0; i < blocks.length; i++) {
                    Block block = blocks[i];
                    Block destinationBlock = destinationBlocks[i];

                    block.cloneTo(destinationBlock);

                    ++count;

                    if (move) {
                        level.setBlock(block, Block.get(Block.AIR));
                    }
                }
            }
            case MASKED -> {
                for (int i = 0; i < blocks.length; i++) {
                    Block block = blocks[i];
                    Block destinationBlock = destinationBlocks[i];

                    if (block.getId() != Block.AIR) {
                        block.cloneTo(destinationBlock);
                        ++count;

                        if (move) {
                            level.setBlock(block, Block.get(Block.AIR));
                        }
                    }
                }
            }
            case FILTERED -> {
                for (int i = 0; i < blocks.length; i++) {
                    Block block = blocks[i];
                    Block destinationBlock = destinationBlocks[i];

                    if (block.getId() == tileId && block.getDamage() == tileData) {
                        block.cloneTo(destinationBlock);
                        ++count;

                        if (move) {
                            level.setBlock(block, Block.get(Block.AIR));
                        }
                    }
                }
            }
        }

        if (count == 0) {
            log.addError("commands.clone.failed").output();
        } else {
            log.addSuccess("commands.clone.success", String.valueOf(count)).output();
        }
        return 1;
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
