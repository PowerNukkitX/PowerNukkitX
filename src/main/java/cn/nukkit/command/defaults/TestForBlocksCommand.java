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
public class TestForBlocksCommand extends VanillaCommand {

    public TestForBlocksCommand(String name) {
        super(name, "commands.testforblocks.description");
        this.setPermission("nukkit.command.testforblocks");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("begin", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("end", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("destination", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("mode", true, new String[]{"all", "masked"})
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
        TestForBlocksMode mode = TestForBlocksMode.ALL;

        if (list.hasResult(3)) {
            String str = list.getResult(3);
            mode = TestForBlocksMode.valueOf(str.toUpperCase(Locale.ENGLISH));
        }

        AxisAlignedBB blocksAABB = new SimpleAxisAlignedBB(Math.min(begin.getX(), end.getX()), Math.min(begin.getY(), end.getY()), Math.min(begin.getZ(), end.getZ()), Math.max(begin.getX(), end.getX()), Math.max(begin.getY(), end.getY()), Math.max(begin.getZ(), end.getZ()));
        int size = NukkitMath.floorDouble((blocksAABB.getMaxX() - blocksAABB.getMinX() + 1) * (blocksAABB.getMaxY() - blocksAABB.getMinY() + 1) * (blocksAABB.getMaxZ() - blocksAABB.getMinZ() + 1));

        if (size > 16 * 16 * 256 * 8) {
            log.addError("commands.fill.tooManyBlocks", String.valueOf(size), String.valueOf(16 * 16 * 256 * 8));
            log.addError("Operation will continue, but too many blocks may cause stuttering");
            log.output();
        }

        Position to = new Position(destination.getX() + (blocksAABB.getMaxX() - blocksAABB.getMinX()), destination.getY() + (blocksAABB.getMaxY() - blocksAABB.getMinY()), destination.getZ() + (blocksAABB.getMaxZ() - blocksAABB.getMinZ()));
        AxisAlignedBB destinationAABB = new SimpleAxisAlignedBB(Math.min(destination.getX(), to.getX()), Math.min(destination.getY(), to.getY()), Math.min(destination.getZ(), to.getZ()), Math.max(destination.getX(), to.getX()), Math.max(destination.getY(), to.getY()), Math.max(destination.getZ(), to.getZ()));

        if (blocksAABB.getMinY() < 0 || blocksAABB.getMaxY() > 255 || destinationAABB.getMinY() < 0 || destinationAABB.getMaxY() > 255) {
            log.addError("commands.testforblock.outOfWorld").output();
            return 0;
        }

        Level level = begin.getLevel();

        for (int sourceChunkX = NukkitMath.floorDouble(blocksAABB.getMinX()) >> 4, destinationChunkX = NukkitMath.floorDouble(destinationAABB.getMinX()) >> 4; sourceChunkX <= NukkitMath.floorDouble(blocksAABB.getMaxX()) >> 4; sourceChunkX++, destinationChunkX++) {
            for (int sourceChunkZ = NukkitMath.floorDouble(blocksAABB.getMinZ()) >> 4, destinationChunkZ = NukkitMath.floorDouble(destinationAABB.getMinZ()) >> 4; sourceChunkZ <= NukkitMath.floorDouble(blocksAABB.getMaxZ()) >> 4; sourceChunkZ++, destinationChunkZ++) {
                if (level.getChunkIfLoaded(sourceChunkX, sourceChunkZ) == null) {
                    log.addError("commands.testforblock.outOfWorld").output();
                    return 0;
                }
                if (level.getChunkIfLoaded(destinationChunkX, destinationChunkZ) == null) {
                    log.addError("commands.testforblock.outOfWorld").output();
                    return 0;
                }
            }
        }

        Block[] blocks = getLevelBlocks(level, blocksAABB);
        Block[] destinationBlocks = getLevelBlocks(level, destinationAABB);
        int count = 0;

        switch (mode) {
            case ALL -> {
                for (int i = 0; i < blocks.length; i++) {
                    Block block = blocks[i];
                    Block destinationBlock = destinationBlocks[i];

                    if (block.equalsBlock(destinationBlock)) {
                        ++count;
                    } else {
                        log.addError("commands.compare.failed").output();
                        return 0;
                    }
                }
            }
            case MASKED -> {
                for (int i = 0; i < blocks.length; i++) {
                    Block block = blocks[i];
                    Block destinationBlock = destinationBlocks[i];

                    if (block.equalsBlock(destinationBlock)) {
                        ++count;
                    } else if (block.getId() != Block.AIR) {
                        log.addError("commands.compare.failed").output();
                        return 0;
                    }
                }
            }
        }
        log.addSuccess("commands.compare.success", String.valueOf(count));
        return 1;
    }

    public enum TestForBlocksMode {
        ALL,
        MASKED
    }
}
