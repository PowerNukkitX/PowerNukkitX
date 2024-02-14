package cn.nukkit.command.defaults;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;

import java.util.Locale;
import java.util.Map;

import static cn.nukkit.utils.Utils.getLevelBlocks;


public class FillCommand extends VanillaCommand {

    public FillCommand(String name) {
        super(name, "commands.fill.description");
        this.setPermission("nukkit.command.fill");
        this.getCommandParameters().clear();
        this.addCommandParameters("default", new CommandParameter[]{
                CommandParameter.newType("from", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("to", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("tileName", false, CommandEnum.BLOCK),
                CommandParameter.newType("tileData", true, CommandParamType.INT),
                CommandParameter.newEnum("oldBlockHandling", true, new String[]{"destroy", "hollow", "keep", "outline", "replace"}),
        });
        this.addCommandParameters("replace", new CommandParameter[]{
                CommandParameter.newType("from", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("to", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("tileName", false, CommandEnum.BLOCK),
                CommandParameter.newType("tileData", false, CommandParamType.INT),
                CommandParameter.newEnum("oldBlockHandling", false, new String[]{"replace"}),
                CommandParameter.newEnum("replaceTileName", false, CommandEnum.BLOCK),
                CommandParameter.newType("replaceDataValue", true, CommandParamType.INT)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        Position from = list.getResult(0);
        Position to = list.getResult(1);
        Block tileName = list.getResult(2);
        String tileId = tileName.getId();
        int tileData = 0;
        FillMode oldBlockHandling = FillMode.REPLACE;
        String replaceTileId;
        int replaceDataValue = -1;

        AxisAlignedBB aabb = new SimpleAxisAlignedBB(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()), Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        if (aabb.getMinY() < -64 || aabb.getMaxY() > 320) {
            log.addError("commands.fill.outOfWorld").output();
            return 0;
        }

        int size = NukkitMath.floorDouble((aabb.getMaxX() - aabb.getMinX() + 1) * (aabb.getMaxY() - aabb.getMinY() + 1) * (aabb.getMaxZ() - aabb.getMinZ() + 1));
        if (size > 16 * 16 * 16 * 8) {
            log.addError("commands.fill.tooManyBlocks", String.valueOf(size), String.valueOf(16 * 16 * 16 * 8));
            log.addError("Operation will continue, but too many blocks may cause stuttering");
        }

        Level level = from.getLevel();

        for (int chunkX = NukkitMath.floorDouble(aabb.getMinX()) >> 4; chunkX <= NukkitMath.floorDouble(aabb.getMaxX()) >> 4; chunkX++) {
            for (int chunkZ = NukkitMath.floorDouble(aabb.getMinZ()) >> 4; chunkZ <= NukkitMath.floorDouble(aabb.getMaxZ()) >> 4; chunkZ++) {
                if (level.getChunkIfLoaded(chunkX, chunkZ) == null) {
                    log.addError("commands.fill.failed").output();
                    return 0;
                }
            }
        }
        Block[] blocks;
        int count = 0;

        switch (result.getKey()) {
            case "default" -> {
                if (list.hasResult(3)) tileData = list.getResult(3);
                if (list.hasResult(4)) {
                    String str = list.getResult(4);
                    oldBlockHandling = FillMode.valueOf(str.toUpperCase(Locale.ENGLISH));
                }
                switch (oldBlockHandling) {
                    case OUTLINE -> {
                        for (int x = NukkitMath.floorDouble(aabb.getMinX()); x <= NukkitMath.floorDouble(aabb.getMaxX()); x++) {
                            for (int z = NukkitMath.floorDouble(aabb.getMinZ()); z <= NukkitMath.floorDouble(aabb.getMaxZ()); z++) {
                                for (int y = NukkitMath.floorDouble(aabb.getMinY()); y <= NukkitMath.floorDouble(aabb.getMaxY()); y++) {

                                    boolean isBorderX = x == NukkitMath.floorDouble(from.x) || x == NukkitMath.floorDouble(to.x);
                                    boolean isBorderZ = z == NukkitMath.floorDouble(from.z) || z == NukkitMath.floorDouble(to.z);
                                    boolean isBorderY = y == NukkitMath.floorDouble(from.y) || y == NukkitMath.floorDouble(to.y);

                                    if (isBorderX || isBorderZ || isBorderY) {
                                        level.setBlock(x, y, z, getBlockWithDamage(tileId, tileData), false, true);
                                        ++count;
                                    }
                                }
                            }
                        }
                    }
                    case HOLLOW -> {
                        for (int x = NukkitMath.floorDouble(aabb.getMinX()); x <= NukkitMath.floorDouble(aabb.getMaxX()); x++) {
                            for (int z = NukkitMath.floorDouble(aabb.getMinZ()); z <= NukkitMath.floorDouble(aabb.getMaxZ()); z++) {
                                for (int y = NukkitMath.floorDouble(aabb.getMinY()); y <= NukkitMath.floorDouble(aabb.getMaxY()); y++) {
                                    Block block;
                                    boolean isBorderX = x == NukkitMath.floorDouble(from.x) || x == NukkitMath.floorDouble(to.x);
                                    boolean isBorderZ = z == NukkitMath.floorDouble(from.z) || z == NukkitMath.floorDouble(to.z);
                                    boolean isBorderY = y == NukkitMath.floorDouble(from.y) || y == NukkitMath.floorDouble(to.y);

                                    if (isBorderX || isBorderZ || isBorderY) {
                                        block = getBlockWithDamage(tileId, tileData);
                                    } else {
                                        block = Block.get(Block.AIR);
                                    }

                                    level.setBlock(x, y, z, block, false, true);
                                    ++count;
                                }
                            }
                        }
                    }
                    case REPLACE -> {
                        blocks = getLevelBlocks(level, aabb);
                        for (Block block : blocks) {
                            level.setBlock(block, getBlockWithDamage(tileId, tileData));
                            ++count;
                        }
                    }
                    case DESTROY -> {
                        blocks = getLevelBlocks(level, aabb);
                        for (Block block : blocks) {
                            level.useBreakOn(block, null, null, null, true);
                            level.setBlock(block, getBlockWithDamage(tileId, tileData));
                            ++count;
                        }
                    }
                    case KEEP -> {
                        blocks = getLevelBlocks(level, aabb);
                        for (Block block : blocks) {
                            if (block.getId() == Block.AIR) {
                                level.setBlock(block, getBlockWithDamage(tileId, tileData));
                                ++count;
                            }
                        }
                    }
                }
            }
            case "replace" -> {
                tileData = list.getResult(3);
                Block replaceTileName = list.getResult(5);
                replaceTileId = replaceTileName.getId();
                if (list.hasResult(6)) {
                    replaceDataValue = list.getResult(6);
                }
                blocks = getLevelBlocks(level, aabb);
                for (Block block : blocks) {
                    if (block.getId() == replaceTileId) {
                        level.setBlock(block, getBlockWithDamage(tileId, tileData));
                        ++count;
                    }
                }
            }
            default -> {
                return 0;
            }
        }

        if (count == 0) {
            log.addError("commands.fill.failed");
            return 0;
        } else {
            log.addSuccess("commands.fill.success", String.valueOf(count));
            return 1;
        }
    }

    private Block getBlockWithDamage(String id, int damage) {
        Block block = Block.get(id);
        BlockState blockState = block.getProperties().getBlockState((short) damage);
        return blockState.toBlock();
    }

    private enum FillMode {
        REPLACE,
        OUTLINE,
        HOLLOW,
        DESTROY,
        KEEP
    }
}
