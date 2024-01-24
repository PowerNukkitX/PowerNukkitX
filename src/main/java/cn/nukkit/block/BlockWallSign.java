package cn.nukkit.block;

import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.CompassRoseDirection;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.block.property.CommonBlockProperties.FACING_DIRECTION;

/**
 * @author Pub4Game
 * @since 26.12.2015
 */
public class BlockWallSign extends BlockStandingSign {
    public static final BlockProperties PROPERTIES = new BlockProperties(WALL_SIGN, FACING_DIRECTION);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWallSign() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWallSign(BlockState blockState) {
        super(blockState);
    }

    @Override
    public String getWallSignId() {
        return getId();
    }

    @Override
    public String getStandingSignId() {
        return STANDING_SIGN;
    }

    @Override
    public String getName() {
        return "Wall Sign";
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
    public void setSignDirection(CompassRoseDirection direction) {
        setBlockFace(direction.getClosestBlockFace());
    }

    @Override
    public CompassRoseDirection getSignDirection() {
        return getBlockFace().getCompassRoseDirection();
    }
}
