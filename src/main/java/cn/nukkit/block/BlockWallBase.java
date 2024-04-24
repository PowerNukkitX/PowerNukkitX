package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.enums.Attachment;
import cn.nukkit.block.property.enums.WallConnectionType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.Faceable;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import static cn.nukkit.block.property.CommonBlockProperties.WALL_CONNECTION_TYPE_EAST;
import static cn.nukkit.block.property.CommonBlockProperties.WALL_CONNECTION_TYPE_NORTH;
import static cn.nukkit.block.property.CommonBlockProperties.WALL_CONNECTION_TYPE_SOUTH;
import static cn.nukkit.block.property.CommonBlockProperties.WALL_CONNECTION_TYPE_WEST;
import static cn.nukkit.block.property.CommonBlockProperties.WALL_POST_BIT;
import static cn.nukkit.math.VectorMath.calculateAxis;
import static cn.nukkit.math.VectorMath.calculateFace;


@Slf4j
public abstract class BlockWallBase extends BlockTransparent implements BlockConnectable {
    private static final double MIN_POST_BB = 5.0 / 16;
    private static final double MAX_POST_BB = 11.0 / 16;

    public BlockWallBase(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    private boolean shouldBeTall(Block above, BlockFace face) {
        return switch (above.getId()) {
            case AIR, SKULL -> false;

            // If the bell is standing and follow the path, make it tall
            case BELL -> {
                BlockBell bell = (BlockBell) above;
                yield bell.getAttachment() == Attachment.STANDING
                        && bell.getBlockFace().getAxis() != face.getAxis();
            }
            default -> {
                if (above instanceof BlockWallBase) {
                    yield ((BlockWallBase) above).getConnectionType(face) != WallConnectionType.NONE;
                } else if (above instanceof BlockConnectable) {
                    yield ((BlockConnectable) above).isConnected(face);
                } else if (above instanceof BlockPressurePlateBase || above instanceof BlockStairs) {
                    yield true;
                }
                yield above.isSolid() && !above.isTransparent() || shouldBeTallBasedOnBoundingBox(above, face);
            }
        };
    }

    private boolean shouldBeTallBasedOnBoundingBox(Block above, BlockFace face) {
        AxisAlignedBB boundingBox = above.getBoundingBox();
        if (boundingBox == null) {
            return false;
        }
        boundingBox = boundingBox.getOffsetBoundingBox(-above.x, -above.y, -above.z);
        if (boundingBox.getMinY() > 0) {
            return false;
        }
        int offset = face.getXOffset();
        if (offset < 0) {
            return boundingBox.getMinX() < MIN_POST_BB
                    && boundingBox.getMinZ() < MIN_POST_BB && MAX_POST_BB < boundingBox.getMaxZ();
        } else if (offset > 0) {
            return MAX_POST_BB < boundingBox.getMaxX()
                    && MAX_POST_BB < boundingBox.getMaxZ() && boundingBox.getMinZ() < MAX_POST_BB;
        } else {
            offset = face.getZOffset();
            if (offset < 0) {
                return boundingBox.getMinZ() < MIN_POST_BB
                        && boundingBox.getMinX() < MIN_POST_BB && MIN_POST_BB < boundingBox.getMaxX();
            } else if (offset > 0) {
                return MAX_POST_BB < boundingBox.getMaxZ()
                        && MAX_POST_BB < boundingBox.getMaxX() && boundingBox.getMinX() < MAX_POST_BB;
            }
        }
        return false;
    }

    public boolean autoConfigureState() {
        final short previousMeta = blockstate.specialValue();

        setWallPost(true);

        Block above = up(1, 0);

        for (BlockFace blockFace : BlockFace.Plane.HORIZONTAL) {
            Block side = getSideAtLayer(0, blockFace);
            if (canConnect(side)) {
                try {
                    connect(blockFace, above, false);
                } catch (RuntimeException e) {
                    log.error("Failed to connect the block {} at {} to {} which is {} at {}",
                            this, getLocation(), blockFace, side, side.getLocation(), e);
                    throw e;
                }
            } else {
                disconnect(blockFace);
            }
        }

        recheckPostConditions(above);
        return blockstate.specialValue() != previousMeta;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (autoConfigureState()) {
                level.setBlock(this, this, true);
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        autoConfigureState();
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    public boolean isWallPost() {
        return getPropertyValue(WALL_POST_BIT);
    }

    public void setWallPost(boolean wallPost) {
        setPropertyValue(WALL_POST_BIT, wallPost);
    }

    public void clearConnections() {
        setPropertyValue(WALL_CONNECTION_TYPE_EAST, WallConnectionType.NONE);
        setPropertyValue(WALL_CONNECTION_TYPE_WEST, WallConnectionType.NONE);
        setPropertyValue(WALL_CONNECTION_TYPE_NORTH, WallConnectionType.NONE);
        setPropertyValue(WALL_CONNECTION_TYPE_SOUTH, WallConnectionType.NONE);
    }

    public Map<BlockFace, WallConnectionType> getWallConnections() {
        EnumMap<BlockFace, WallConnectionType> connections = new EnumMap<>(BlockFace.class);
        for (BlockFace blockFace : BlockFace.Plane.HORIZONTAL) {
            WallConnectionType connectionType = getConnectionType(blockFace);
            if (connectionType != WallConnectionType.NONE) {
                connections.put(blockFace, connectionType);
            }
        }
        return connections;
    }

    public WallConnectionType getConnectionType(BlockFace blockFace) {
        return switch (blockFace) {
            case NORTH -> getPropertyValue(WALL_CONNECTION_TYPE_NORTH);
            case SOUTH -> getPropertyValue(WALL_CONNECTION_TYPE_SOUTH);
            case WEST -> getPropertyValue(WALL_CONNECTION_TYPE_WEST);
            case EAST -> getPropertyValue(WALL_CONNECTION_TYPE_EAST);
            default -> WallConnectionType.NONE;
        };
    }

    public boolean setConnection(BlockFace blockFace, WallConnectionType type) {
        return switch (blockFace) {
            case NORTH -> {
                setPropertyValue(WALL_CONNECTION_TYPE_NORTH, type);
                yield true;
            }
            case SOUTH -> {
                setPropertyValue(WALL_CONNECTION_TYPE_SOUTH, type);
                yield true;
            }
            case WEST -> {
                setPropertyValue(WALL_CONNECTION_TYPE_WEST, type);
                yield true;
            }
            case EAST -> {
                setPropertyValue(WALL_CONNECTION_TYPE_EAST, type);
                yield true;
            }
            default -> false;
        };
    }

    /**
     * @return true if it should be a post
     */
    public void autoUpdatePostFlag() {
        setWallPost(recheckPostConditions(up(1, 0)));
    }

    public boolean hasConnections() {
        return getPropertyValue(WALL_CONNECTION_TYPE_EAST) != WallConnectionType.NONE
                || getPropertyValue(WALL_CONNECTION_TYPE_WEST) != WallConnectionType.NONE
                || getPropertyValue(WALL_CONNECTION_TYPE_NORTH) != WallConnectionType.NONE
                || getPropertyValue(WALL_CONNECTION_TYPE_SOUTH) != WallConnectionType.NONE;
    }

    private boolean recheckPostConditions(Block above) {
        // If nothing is connected, it should be a post
        if (!hasConnections()) {
            return true;
        }

        // If it's not straight, it should be a post
        Map<BlockFace, WallConnectionType> connections = getWallConnections();
        if (connections.size() != 2) {
            return true;
        }

        Iterator<Map.Entry<BlockFace, WallConnectionType>> iterator = connections.entrySet().iterator();
        Map.Entry<BlockFace, WallConnectionType> entryA = iterator.next();
        Map.Entry<BlockFace, WallConnectionType> entryB = iterator.next();
        if (entryA.getValue() != entryB.getValue() || entryA.getKey().getOpposite() != entryB.getKey()) {
            return true;
        }

        BlockFace.Axis axis = entryA.getKey().getAxis();

        switch (above.getId()) {
            // These special blocks forces walls to become a post
            case FLOWER_POT, SKULL, CONDUIT, STANDING_BANNER, TURTLE_EGG -> {
                return true;
            }

            // End rods make it become a post if it's placed on the wall
            case END_ROD -> {
                if (((Faceable) above).getBlockFace() == BlockFace.UP) {
                    return true;
                }
            }

            // If the bell is standing and don't follow the path, make it a post
            case BELL -> {
                BlockBell bell = (BlockBell) above;
                if (bell.getAttachment() == Attachment.STANDING
                        && bell.getBlockFace().getAxis() == axis) {
                    return true;
                }
            }
            default -> {
                if (above instanceof BlockWallBase) {
                    // If the wall above is a post, it should also be a post

                    if (((BlockWallBase) above).isWallPost()) {
                        return true;
                    }

                } else if (above instanceof BlockLantern) {
                    // Lanterns makes this become a post if they are not hanging

                    if (!((BlockLantern) above).isHanging()) {
                        return true;
                    }

                } else if (above.getId().equals(LEVER) || above instanceof BlockTorch || above instanceof BlockButton) {
                    // These blocks make this become a post if they are placed down (facing up)

                    if (((Faceable) above).getBlockFace() == BlockFace.UP) {
                        return true;
                    }

                } else if (above instanceof BlockFenceGate) {
                    // If the gate don't follow the path, make it a post

                    if (((Faceable) above).getBlockFace().getAxis() == axis) {
                        return true;
                    }

                } else if (above instanceof BlockConnectable) {
                    // If the connectable block above don't share 2 equal connections, then this should be a post

                    int shared = 0;
                    for (BlockFace connection : ((BlockConnectable) above).getConnections()) {
                        if (connections.containsKey(connection) && ++shared == 2) {
                            break;
                        }
                    }

                    if (shared < 2) {
                        return true;
                    }

                }
            }
        }

        // Sign posts always makes the wall become a post
        return above instanceof BlockStandingSign;
    }

    public boolean isSameHeightStraight() {
        Map<BlockFace, WallConnectionType> connections = getWallConnections();
        if (connections.size() != 2) {
            return false;
        }

        Iterator<Map.Entry<BlockFace, WallConnectionType>> iterator = connections.entrySet().iterator();
        Map.Entry<BlockFace, WallConnectionType> a = iterator.next();
        Map.Entry<BlockFace, WallConnectionType> b = iterator.next();
        return a.getValue() == b.getValue() && a.getKey().getOpposite() == b.getKey();
    }

    public boolean connect(BlockFace blockFace) {
        return connect(blockFace, true);
    }

    public boolean connect(BlockFace blockFace, boolean recheckPost) {
        if (blockFace.getHorizontalIndex() < 0) {
            return false;
        }

        Block above = getSideAtLayer(0, BlockFace.UP);
        return connect(blockFace, above, recheckPost);
    }

    private boolean connect(BlockFace blockFace, Block above, boolean recheckPost) {
        WallConnectionType type = shouldBeTall(above, blockFace) ? WallConnectionType.TALL : WallConnectionType.SHORT;
        if (setConnection(blockFace, type)) {
            if (recheckPost) {
                recheckPostConditions(above);
            }
            return true;
        }
        return false;
    }

    public boolean disconnect(BlockFace blockFace) {
        if (blockFace.getHorizontalIndex() < 0) {
            return false;
        }

        if (setConnection(blockFace, WallConnectionType.NONE)) {
            autoUpdatePostFlag();
            return true;
        }
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {

        boolean north = this.canConnect(this.getSide(BlockFace.NORTH));
        boolean south = this.canConnect(this.getSide(BlockFace.SOUTH));
        boolean west = this.canConnect(this.getSide(BlockFace.WEST));
        boolean east = this.canConnect(this.getSide(BlockFace.EAST));

        double n = north ? 0 : 0.25;
        double s = south ? 1 : 0.75;
        double w = west ? 0 : 0.25;
        double e = east ? 1 : 0.75;

        if (north && south && !west && !east) {
            w = 0.3125;
            e = 0.6875;
        } else if (!north && !south && west && east) {
            n = 0.3125;
            s = 0.6875;
        }

        return new SimpleAxisAlignedBB(
                this.x + w,
                this.y,
                this.z + n,
                this.x + e,
                this.y + 1.5,
                this.z + s
        );
    }

    @Override
    public boolean canConnect(Block block) {
        switch (block.getId()) {
            case GLASS_PANE, IRON_BARS, GLASS -> {
                return true;
            }
            default -> {
                if (block instanceof BlockGlassStained || block instanceof BlockGlassPaneStained || block instanceof BlockWallBase) {
                    return true;
                }
                if (block instanceof BlockFenceGate fenceGate) {
                    return fenceGate.getBlockFace().getAxis() != calculateAxis(this, block);
                }
                if (block instanceof BlockStairs) {
                    return ((BlockStairs) block).getBlockFace().getOpposite() == calculateFace(this, block);
                }
                if (block instanceof BlockTrapdoor trapdoor) {
                    return trapdoor.isOpen() && trapdoor.getBlockFace() == calculateFace(this, trapdoor);
                }
                return block.isSolid() && !block.isTransparent();
            }
        }
    }

    @Override
    public boolean isConnected(BlockFace face) {
        return getConnectionType(face) != WallConnectionType.NONE;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
