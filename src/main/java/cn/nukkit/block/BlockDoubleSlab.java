package cn.nukkit.block;

import cn.nukkit.api.DeprecationDetails;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Deprecated
@DeprecationDetails(reason = "Unused and the same as BlockDoubleSlabStone", since = "1.4.0.0-PN", replaceWith = "BlockDoubleSlabBase")

public class BlockDoubleSlab extends BlockDoubleSlabStone {
    public static final int STONE = 0;
    public static final int SANDSTONE = 1;
    public static final int WOODEN = 2;
    public static final int COBBLESTONE = 3;
    public static final int BRICK = 4;
    public static final int STONE_BRICK = 5;
    public static final int QUARTZ = 6;
    public static final int NETHER_BRICK = 7;

    public BlockDoubleSlab() {
        this(0);
    }

    public BlockDoubleSlab(BlockState blockstate) {
        super(blockstate);
    }
}
