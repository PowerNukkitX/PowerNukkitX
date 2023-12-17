package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.CompassRoseDirection;
import org.jetbrains.annotations.NotNull;

import static cn.nukkit.blockproperty.CommonBlockProperties.FACING_DIRECTION;

/**
 * @author PetteriM1
 */
@PowerNukkitDifference(since = "1.4.0.0-PN", info = "Implements BlockEntityHolder only in PowerNukkit")
public class BlockWallBanner extends BlockBanner {


    public static final BlockProperties PROPERTIES = CommonBlockProperties.FACING_DIRECTION_BLOCK_PROPERTIES;

    public BlockWallBanner() {
        this(0);
    }

    public BlockWallBanner(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WALL_BANNER;
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Wall Banner";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.getSide(getBlockFace().getOpposite()).getId() == AIR) {
                this.getLevel().useBreakOn(this);
            }
            return Level.BLOCK_UPDATE_NORMAL;
        }
        return 0;
    }


    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(FACING_DIRECTION, face);
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(FACING_DIRECTION);
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
