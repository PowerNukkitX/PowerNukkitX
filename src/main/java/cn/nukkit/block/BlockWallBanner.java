package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.CompassRoseDirection;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;

/**
 * @author PetteriM1
 */

public class BlockWallBanner extends BlockStandingBanner {
    public static final BlockProperties PROPERTIES = new BlockProperties(WALL_BANNER, FACING_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWallBanner() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWallBanner(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Wall Banner";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(getBlockFace().getOpposite()).isAir()) {
                this.getLevel().useBreakOn(this);
            }
            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }

    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face.getIndex());
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromIndex(getPropertyValue(FACING_DIRECTION));
    }

    @Override
    public void setDirection(CompassRoseDirection direction) {
        setBlockFace(direction.getClosestBlockFace());
    }

    @Override
    public CompassRoseDirection getDirection() {
        return getBlockFace().getCompassRoseDirection();
    }
}
