package cn.nukkit.block.property;

import static cn.nukkit.math.CompassRoseDirection.*;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.property.value.ChiselType;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.CompassRoseDirection;
import cn.nukkit.utils.DyeColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public final class CommonBlockProperties {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties EMPTY_PROPERTIES = new BlockProperties();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty OPEN = new BooleanBlockProperty("open_bit", false);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty TOGGLE = new BooleanBlockProperty("toggle_bit", false);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final IntBlockProperty REDSTONE_SIGNAL = new IntBlockProperty("redstone_signal", false, 15);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BooleanBlockProperty PERMANENTLY_DEAD = new BooleanBlockProperty("dead_bit", true);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties REDSTONE_SIGNAL_BLOCK_PROPERTY = new BlockProperties(REDSTONE_SIGNAL);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty UPPER_BLOCK = new BooleanBlockProperty("upper_block_bit", false);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<BlockFace> FACING_DIRECTION = new ArrayBlockProperty<>(
                    "facing_direction", false, new BlockFace[] {
                        // Index based
                        BlockFace.DOWN, BlockFace.UP,
                        BlockFace.NORTH, BlockFace.SOUTH,
                        BlockFace.WEST, BlockFace.EAST,
                    })
            .ordinal(true);

    @Since("1.20.0-r2")
    @PowerNukkitXOnly
    public static final ArrayBlockProperty<BlockFace> FACING_DIRECTION_ARRAY =
            new ArrayBlockProperty<>("facing_direction", false, new BlockFace[] {
                // Index based
                BlockFace.DOWN, BlockFace.UP,
                BlockFace.NORTH, BlockFace.SOUTH,
                BlockFace.WEST, BlockFace.EAST,
            });

    public static final ArrayBlockProperty<CompassRoseDirection> GROUND_SIGN_DIRECTION = new ArrayBlockProperty<>(
                    "ground_sign_direction", false, new CompassRoseDirection[] {
                        SOUTH, SOUTH_SOUTH_WEST, SOUTH_WEST, WEST_SOUTH_WEST,
                        WEST, WEST_NORTH_WEST, NORTH_WEST, NORTH_NORTH_WEST,
                        NORTH, NORTH_NORTH_EAST, NORTH_EAST, EAST_NORTH_EAST,
                        EAST, EAST_SOUTH_EAST, SOUTH_EAST, SOUTH_SOUTH_EAST
                    })
            .ordinal(true);

    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    public static final BooleanBlockProperty ATTACHED = new BooleanBlockProperty("attached_bit", false);

    @PowerNukkitXOnly
    @Since("1.20.0-r2")
    public static final BooleanBlockProperty HANGING = new BooleanBlockProperty("hanging", false);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final ArrayBlockProperty<ChiselType> CHISEL_TYPE =
            new ArrayBlockProperty<>("chisel_type", true, ChiselType.class);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final IntBlockProperty AGE_15 = new IntBlockProperty("age", false, 15);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties FACING_DIRECTION_BLOCK_PROPERTIES = new BlockProperties(FACING_DIRECTION);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<BlockFace> DIRECTION = new ArrayBlockProperty<>(
                    "direction", false, new BlockFace[] {
                        // Horizontal-index based
                        BlockFace.SOUTH, BlockFace.WEST,
                        BlockFace.NORTH, BlockFace.EAST,
                    })
            .ordinal(true);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<BlockFace.Axis> PILLAR_AXIS = new ArrayBlockProperty<>(
            "pillar_axis", false, new BlockFace.Axis[] {BlockFace.Axis.Y, BlockFace.Axis.X, BlockFace.Axis.Z});

    @Since("1.20.0-r2")
    @PowerNukkitXOnly
    public static final ArrayBlockProperty<BlockFace> DIRECTION_ARRAY = (ArrayBlockProperty<BlockFace>) DIRECTION;

    @Since("1.20.0-r2")
    @PowerNukkitXOnly
    public static final ArrayBlockProperty<BlockFace.Axis> PILLAR_AXIS_ARRAY =
            (ArrayBlockProperty<BlockFace.Axis>) PILLAR_AXIS;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final IntBlockProperty DEPRECATED = new IntBlockProperty("deprecated", false, 3);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperty<DyeColor> COLOR = new ArrayBlockProperty<>(
            "color",
            true,
            new DyeColor[] {
                DyeColor.WHITE,
                DyeColor.ORANGE,
                DyeColor.MAGENTA,
                DyeColor.LIGHT_BLUE,
                DyeColor.YELLOW,
                DyeColor.LIME,
                DyeColor.PINK,
                DyeColor.GRAY,
                DyeColor.LIGHT_GRAY,
                DyeColor.CYAN,
                DyeColor.PURPLE,
                DyeColor.BLUE,
                DyeColor.BROWN,
                DyeColor.GREEN,
                DyeColor.RED,
                DyeColor.BLACK
            },
            4,
            "color",
            false,
            new String[] {
                "white",
                "orange",
                "magenta",
                "light_blue",
                "yellow",
                "lime",
                "pink",
                "gray",
                "silver",
                "cyan",
                "purple",
                "blue",
                "brown",
                "green",
                "red",
                "black"
            });

    @Since("1.20.0-r2")
    @PowerNukkitXOnly
    public static final ArrayBlockProperty<DyeColor> COLOR_ARRAY = (ArrayBlockProperty<DyeColor>) COLOR;

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties COLOR_BLOCK_PROPERTIES = new BlockProperties(COLOR);

    private CommonBlockProperties() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty POWERED = new BooleanBlockProperty("powered_bit", false);
}
