package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.OptionalBoolean;
import cn.nukkit.utils.Rail;
import cn.nukkit.utils.Rail.Orientation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.nukkit.block.property.CommonBlockProperties.RAIL_DIRECTION_10;
import static cn.nukkit.math.BlockFace.EAST;
import static cn.nukkit.math.BlockFace.NORTH;
import static cn.nukkit.math.BlockFace.SOUTH;
import static cn.nukkit.math.BlockFace.UP;
import static cn.nukkit.math.BlockFace.WEST;
import static cn.nukkit.utils.Rail.Orientation.CURVED_NORTH_EAST;
import static cn.nukkit.utils.Rail.Orientation.CURVED_NORTH_WEST;
import static cn.nukkit.utils.Rail.Orientation.CURVED_SOUTH_EAST;
import static cn.nukkit.utils.Rail.Orientation.CURVED_SOUTH_WEST;
import static cn.nukkit.utils.Rail.Orientation.STRAIGHT_NORTH_SOUTH;
import static cn.nukkit.utils.Rail.Orientation.ascending;
import static cn.nukkit.utils.Rail.Orientation.curved;
import static cn.nukkit.utils.Rail.Orientation.straight;
import static cn.nukkit.utils.Rail.Orientation.straightOrCurved;

/**
 * @author Snake1999
 * @since 2016/1/11
 */
public class BlockRail extends BlockFlowable implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(RAIL, RAIL_DIRECTION_10);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    // 0x8: Set the block active
    // 0x7: Reset the block to normal
    // If the rail can be powered. So its a complex rail!
    protected boolean canBePowered = false;

    public BlockRail() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockRail(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getName() {
        return "Rail";
    }

    @Override
    public double getHardness() {
        return 0.7;
    }

    @Override
    public double getResistance() {
        return 3.5;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean sticksToPiston() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Optional<BlockFace> ascendingDirection = this.getOrientation().ascendingDirection();
            if (!checkCanBePlace(this.down()) || (ascendingDirection.isPresent() && !checkCanBePlace(this.getSide(ascendingDirection.get())))) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        if (type == Level.BLOCK_UPDATE_REDSTONE && this.getRailDirection().isCurved()) {
            var connect = checkRailsConnected().values();
            List<BlockFace> railFace = new ArrayList<>();
            for (BlockFace face : connect) {
                if (this.getSide(face.getOpposite()) instanceof BlockRail) {
                    railFace.add(face.getOpposite());
                } else {
                    railFace.add(face);
                }
            }
            Orientation orient;
            if (railFace.contains(SOUTH)) {
                if (railFace.contains(EAST)) {
                    orient = CURVED_SOUTH_EAST;
                } else orient = CURVED_SOUTH_WEST;
            } else {
                if (railFace.contains(EAST)) {
                    orient = CURVED_NORTH_EAST;
                } else orient = CURVED_NORTH_WEST;
            }
            setOrientation(orient);
        }
        return 0;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.125;
    }

    @Override
    public AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    //Information from http://minecraft.wiki/w/Rail
    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        if (!checkCanBePlace(down)) {
            return false;
        }
        Map<BlockRail, BlockFace> railsAround = this.checkRailsAroundAffected();
        List<BlockRail> rails = new ArrayList<>(railsAround.keySet());
        List<BlockFace> faces = new ArrayList<>(railsAround.values());
        if (railsAround.size() == 1) {
            BlockRail other = rails.get(0);
            this.setRailDirection(this.connect(other, railsAround.get(other)));
        } else if (railsAround.size() == 4) {
            if (this.isAbstract()) {
                this.setRailDirection(this.connect(rails.get(faces.indexOf(SOUTH)), SOUTH, rails.get(faces.indexOf(EAST)), EAST));
            } else {
                this.setRailDirection(this.connect(rails.get(faces.indexOf(EAST)), EAST, rails.get(faces.indexOf(WEST)), WEST));
            }
        } else if (!railsAround.isEmpty()) {
            if (this.isAbstract()) {
                if (railsAround.size() == 2) {
                    BlockRail rail1 = rails.get(0);
                    BlockRail rail2 = rails.get(1);
                    this.setRailDirection(this.connect(rail1, railsAround.get(rail1), rail2, railsAround.get(rail2)));
                } else {
                    List<BlockFace> cd = Stream.of(CURVED_SOUTH_EAST, CURVED_NORTH_EAST, CURVED_SOUTH_WEST)
                            .filter(o -> new HashSet<>(faces).containsAll(o.connectingDirections()))
                            .findFirst().get().connectingDirections();
                    BlockFace f1 = cd.get(0);
                    BlockFace f2 = cd.get(1);
                    this.setRailDirection(this.connect(rails.get(faces.indexOf(f1)), f1, rails.get(faces.indexOf(f2)), f2));
                }
            } else {
                BlockFace f = faces.stream().min((f1, f2) -> (f1.getIndex() < f2.getIndex()) ? 1 : ((x == y) ? 0 : -1)).get();
                BlockFace fo = f.getOpposite();
                if (faces.contains(fo)) { //Opposite connectable
                    this.setRailDirection(this.connect(rails.get(faces.indexOf(f)), f, rails.get(faces.indexOf(fo)), fo));
                } else {
                    this.setRailDirection(this.connect(rails.get(faces.indexOf(f)), f));
                }
            }
        }
        this.level.setBlock(this, this, true, true);
        if (!isAbstract()) {
            level.scheduleUpdate(this, this, 0);
        }

        return true;
    }

    private boolean checkCanBePlace(Block check) {
        if (check == null) {
            return false;
        }
        return check.isSolid(UP) || check instanceof BlockCauldron;
    }

    private Orientation connect(BlockRail rail1, BlockFace face1, BlockRail rail2, BlockFace face2) {
        this.connect(rail1, face1);
        this.connect(rail2, face2);

        if (face1.getOpposite() == face2) {
            int delta1 = (int) (this.y - rail1.y);
            int delta2 = (int) (this.y - rail2.y);

            if (delta1 == -1) {
                return Orientation.ascending(face1);
            } else if (delta2 == -1) {
                return Orientation.ascending(face2);
            }
        }
        return straightOrCurved(face1, face2);
    }

    private Orientation connect(BlockRail other, BlockFace face) {
        int delta = (int) (this.y - other.y);
        Map<BlockRail, BlockFace> rails = other.checkRailsConnected();
        if (rails.isEmpty()) { //Only one
            other.setOrientation(delta == 1 ? ascending(face.getOpposite()) : straight(face));
            return delta == -1 ? ascending(face) : straight(face);
        } else if (rails.size() == 1) { //Already connected
            BlockFace faceConnected = rails.values().iterator().next();

            if (other.isAbstract() && faceConnected != face) { //Curve!
                other.setOrientation(curved(face.getOpposite(), faceConnected));
                return delta == -1 ? ascending(face) : straight(face);
            } else if (faceConnected == face) { //Turn!
                if (!other.getOrientation().isAscending()) {
                    other.setOrientation(delta == 1 ? ascending(face.getOpposite()) : straight(face));
                }
                return delta == -1 ? ascending(face) : straight(face);
            } else if (other.getOrientation().hasConnectingDirections(NORTH, SOUTH)) { //North-south
                other.setOrientation(delta == 1 ? ascending(face.getOpposite()) : straight(face));
                return delta == -1 ? ascending(face) : straight(face);
            }
        }
        return STRAIGHT_NORTH_SOUTH;
    }

    private Map<BlockRail, BlockFace> checkRailsAroundAffected() {
        Map<BlockRail, BlockFace> railsAround = this.checkRailsAround(Arrays.asList(SOUTH, EAST, WEST, NORTH));
        return railsAround.keySet().stream()
                .filter(r -> r.checkRailsConnected().size() != 2)
                .collect(Collectors.toMap(r -> r, railsAround::get));
    }

    private Map<BlockRail, BlockFace> checkRailsAround(Collection<BlockFace> faces) {
        Map<BlockRail, BlockFace> result = new HashMap<>();
        faces.forEach(f -> {
            Block b = this.getSide(f);
            Stream.of(b, b.up(), b.down())
                    .filter(Rail::isRailBlock)
                    .forEach(block -> result.put((BlockRail) block, f));
        });
        return result;
    }

    protected Map<BlockRail, BlockFace> checkRailsConnected() {
        Map<BlockRail, BlockFace> railsAround = this.checkRailsAround(this.getOrientation().connectingDirections());
        return railsAround.keySet().stream()
                .filter(r -> r.getOrientation().hasConnectingDirections(railsAround.get(r).getOpposite()))
                .collect(Collectors.toMap(r -> r, railsAround::get));
    }

    public boolean isAbstract() {
        return this.getId().equals(RAIL);
    }

    public boolean canPowered() {
        return this.canBePowered;
    }

    @NotNull public final Orientation getRailDirection() {
        return getOrientation();
    }

    /**
     * Changes the rail direction without changing anything else.
     *
     * @param orientation The new orientation
     */
    public void setRailDirection(Orientation orientation) {
        setPropertyValue(RAIL_DIRECTION_10, orientation.metadata());
    }

    /**
     * Get the rail orientation.
     *
     * @return the orientation
     */
    public Orientation getOrientation() {
        return Orientation.byMetadata(getPropertyValue(RAIL_DIRECTION_10));
    }

    /**
     * Changes the rail direction and update the state in the world if the orientation changed in a single call.
     * <p>
     * Note that the level block won't change if the current block has already the given orientation.
     *
     * @see #setRailDirection(Orientation)
     * @see Level#setBlock(Vector3, int, Block, boolean, boolean)
     */
    public void setOrientation(Orientation o) {
        if (o != getOrientation()) {
            setRailDirection(o);
            this.level.setBlock(this, this, true, true);
        }
    }

    public int getRealMeta() {
        // Check if this can be powered
        // Avoid modifying the value from meta (The rail orientation may be false)
        // Reason: When the rail is curved, the meta will return STRAIGHT_NORTH_SOUTH.
        // OR Null Pointer Exception
        if (!isAbstract()) {
            return this.getBlockState().specialValue() & 0x7;
        }
        // Return the default: This meta
        return this.getBlockState().specialValue();
    }

    public boolean isActive() {
        return getProperties().containProperty(CommonBlockProperties.ACTIVE) && getPropertyValue(CommonBlockProperties.ACTIVE);
    }

    /**
     * Changes the active flag and update the state in the world in a single call.
     * <p>
     * The active flag will not change if the block state don't have the {@link CommonBlockProperties#ACTIVE} property,
     * and it will not throw exceptions related to missing block properties.
     * <p>
     * The level block will always update.
     *
     * @see #setRailDirection(Orientation)
     * @see Level#setBlock(Vector3, int, Block, boolean, boolean)
     */
    public void setActive(boolean active) {
        if (getProperties().containProperty(CommonBlockProperties.ACTIVE)) {
            setRailActive(active);
        }
        level.setBlock(this, this, true, true);
    }

    public OptionalBoolean isRailActive() {
        return getProperties().containProperty(CommonBlockProperties.ACTIVE) ?
                OptionalBoolean.of(getPropertyValue(CommonBlockProperties.ACTIVE)) :
                OptionalBoolean.empty();
    }

    /**
     * @throws NoSuchElementException If attempt to set the rail to active but it don't have the {@link CommonBlockProperties#ACTIVE} property.
     */
    public void setRailActive(boolean active){
        if (!active && !getProperties().containProperty(CommonBlockProperties.ACTIVE)) {
            return;
        }
        setPropertyValue(CommonBlockProperties.ACTIVE,active);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getBlockState().specialValue() & 0x07);
    }

    @Override
    public boolean breaksWhenMoved() {
        return false;
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }
}
