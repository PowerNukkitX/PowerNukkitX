package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockState;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;
import cn.nukkit.item.Item;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.generator.object.BlockManager;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.SimpleAxisAlignedBB;
import org.cloudburstmc.protocol.bedrock.data.command.CommandParamType;

import java.util.Locale;
import java.util.Map;

import static cn.nukkit.utils.Utils.getLevelBlocks;


public class FillCommand extends VanillaCommand {

    public FillCommand(String name) {
        super(name, "commands.fill.description");
        this.setPermission("nukkit.command.fill");
        this.getCommandParameters().clear();
        this.addCommandParameters("fill-with-states", new CommandParameter[]{
                CommandParameter.newType("from", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("to", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("tileName", false, CommandEnum.ENUM_BLOCK),
                CommandParameter.newType("blockStates", false, CommandParamType.BLOCK_STATES),
                CommandParameter.newEnum("oldBlockHandling", true, new String[]{"destroy", "hollow", "keep", "outline"}),
        });
        this.addCommandParameters("fill-no-states", new CommandParameter[]{
                CommandParameter.newType("from", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("to", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("tileName", false, CommandEnum.ENUM_BLOCK),
                CommandParameter.newEnum("oldBlockHandling", true, new String[]{"destroy", "hollow", "keep", "outline"}),
        });
        this.addCommandParameters("replace", new CommandParameter[]{
                CommandParameter.newType("from", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("to", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("tileName", false, CommandEnum.ENUM_BLOCK),
                CommandParameter.newType("blockStates", false, CommandParamType.BLOCK_STATES),
                CommandParameter.newEnum("oldBlockHandling", false, new String[]{"replace"}),
                CommandParameter.newEnum("replaceTileName", true, CommandEnum.ENUM_BLOCK),
                CommandParameter.newType("replaceBlockStates", true, CommandParamType.BLOCK_STATES)
        });
        this.addCommandParameters("replace-no-states", new CommandParameter[]{
                CommandParameter.newType("from", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newType("to", false, CommandParamType.BLOCK_POSITION),
                CommandParameter.newEnum("tileName", false, CommandEnum.ENUM_BLOCK),
                CommandParameter.newEnum("oldBlockHandling", false, new String[]{"replace"}),
                CommandParameter.newEnum("replaceTileName", true, CommandEnum.ENUM_BLOCK),
                CommandParameter.newType("replaceBlockStates", true, CommandParamType.BLOCK_STATES)
        });
        this.enableParamTree();
    }

    @Override
    public int execute(CommandSender sender, String commandLabel, Map.Entry<String, ParamList> result, CommandLogger log) {
        var list = result.getValue();
        Position from = list.getResult(0);
        Position to = list.getResult(1);
        Block b = list.getResult(2);
        BlockState tileState = b.getProperties().getDefaultState();
        FillMode oldBlockHandling = FillMode.REPLACE;

        AxisAlignedBB aabb = new SimpleAxisAlignedBB(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()), Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        if (aabb.getMinY() < -64 || aabb.getMaxY() > 320) {
            log.addError("commands.fill.outOfWorld").output();
            return 0;
        }

        int size = NukkitMath.floorDouble((aabb.getMaxX() - aabb.getMinX() + 1) * (aabb.getMaxY() - aabb.getMinY() + 1) * (aabb.getMaxZ() - aabb.getMinZ() + 1));
        if (size > 16 * 16 * 16 * 8) {
            log.addError("commands.fill.tooManyBlocks", String.valueOf(size), String.valueOf(16 * 16 * 16 * 8)).output();
            return 0;
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

        final BlockManager blockManager = new BlockManager(level);
        switch (result.getKey()) {
            case "fill-with-states", "fill-no-states" -> {
                boolean withStates = result.getKey().equals("fill-with-states");
                if (withStates) tileState = list.getResult(3);
                int modeIdx = withStates ? 4 : 3;
                if (list.hasResult(modeIdx)) {
                    String str = list.getResult(modeIdx);
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
                                        blockManager.setBlockStateAt(x, y, z, tileState);
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
                                        block = tileState.toBlock();
                                    } else {
                                        block = Block.get(Block.AIR);
                                    }

                                    blockManager.setBlockStateAt(x, y, z, block.getBlockState());
                                    ++count;
                                }
                            }
                        }
                    }
                    case REPLACE -> {
                        blocks = getLevelBlocks(level, aabb);
                        for (Block block : blocks) {
                            blockManager.setBlockStateAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), tileState);
                            ++count;
                        }
                    }
                    case DESTROY -> {
                        blocks = getLevelBlocks(level, aabb);
                        boolean doDrops = level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS);
                        for (Block block : blocks) {
                            Map<Integer, Player> players = level.getChunkPlayers((int) block.x >> 4, (int) block.z >> 4);
                            level.addParticle(new DestroyBlockParticle(block.add(0.5), block), players.values());
                            if (doDrops) {
                                for (Item drop : block.getDrops(Item.AIR)) {
                                    if (drop.getCount() > 0) {
                                        level.dropItem(block.add(0.5, 0.5, 0.5), drop);
                                    }
                                }
                                int exp = block.getDropExp();
                                if (exp > 0) {
                                    level.dropExpOrb(block.add(0.5, 0.5, 0.5), exp);
                                }
                            }
                            blockManager.setBlockStateAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), tileState);
                            ++count;
                        }
                    }
                    case KEEP -> {
                        blocks = getLevelBlocks(level, aabb);
                        for (Block block : blocks) {
                            if (block.isAir()) {
                                blockManager.setBlockStateAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), tileState);
                                ++count;
                            }
                        }
                    }
                }
            }
            case "replace", "replace-no-states" -> {
                boolean withStates = result.getKey().equals("replace");
                if (withStates && list.hasResult(3)) tileState = list.getResult(3);
                int filterIdx = withStates ? 5 : 4;
                int filterStatesIdx = withStates ? 6 : 5;
                if (list.hasResult(filterIdx)) {
                    Block filterBlock = list.getResult(filterIdx);
                    blocks = getLevelBlocks(level, aabb);
                    if (list.hasResult(filterStatesIdx)) {
                        BlockState filterState = list.getResult(filterStatesIdx);
                        int filterHash = filterState.blockStateHash();
                        for (Block block : blocks) {
                            if (block.getBlockState().blockStateHash() == filterHash) {
                                blockManager.setBlockStateAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), tileState);
                                ++count;
                            }
                        }
                    } else {
                        String filterId = filterBlock.getId();
                        for (Block block : blocks) {
                            if (block.getId().equals(filterId)) {
                                blockManager.setBlockStateAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), tileState);
                                ++count;
                            }
                        }
                    }
                } else {
                    blocks = getLevelBlocks(level, aabb);
                    for (Block block : blocks) {
                        blockManager.setBlockStateAt(block.getFloorX(), block.getFloorY(), block.getFloorZ(), tileState);
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
            blockManager.applySubChunkUpdate();
            log.addSuccess("commands.fill.success", String.valueOf(count));
            return 1;
        }
    }

    private enum FillMode {
        REPLACE,
        OUTLINE,
        HOLLOW,
        DESTROY,
        KEEP
    }
}
