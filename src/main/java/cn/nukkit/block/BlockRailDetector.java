package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityMinecartAbstract;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.OptionalBoolean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author CreeperFace (Nukkit Project), larryTheCoder (Minecart and Riding Project)
 * @since 2015/11/22 
 */
public class BlockRailDetector extends BlockRail {

    public static Set<Position> activeDetectors = new HashSet<>();

    static{
        Server.getInstance().getScheduler().scheduleRepeatingTask(() -> {
            for (Position pos : activeDetectors.toArray(new Position[0])) {
                BlockRailDetector detector;
                if (pos.getLevel().getBlock(pos) instanceof BlockRailDetector) {
                    detector = (BlockRailDetector) pos.getLevel().getBlock(pos);
                }else{
                    activeDetectors.remove(pos);
                    return;
                }
                for (Entity entity : detector.level.getNearbyEntities(new SimpleAxisAlignedBB(
                        detector.getFloorX() + 0.2D,
                        detector.getFloorY(),
                        detector.getFloorZ() + 0.2D,
                        detector.getFloorX() + 0.8D,
                        detector.getFloorY() + 0.8D,
                        detector.getFloorZ() + 0.8D))) {
                    if (entity instanceof EntityMinecartAbstract) {
                        return;
                    }
                }
                detector.setActive(false);
                detector.level.setBlock(detector,detector,true,true);
                activeDetectors.remove(detector);
            }
        }, 20);
    }

    public BlockRailDetector() {
        this(0);
        canBePowered = true;
    }

    public BlockRailDetector(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DETECTOR_RAIL;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return ACTIVABLE_PROPERTIES;
    }

    @Override
    public String getName() {
        return "Detector Rail";
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isActive() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return isActive() ? 0 : (side == BlockFace.UP ? 15 : 0);
    }

    public void setActive() {
        if (this.isActive()){
            return;
        }
        setActive(true);
        this.level.setBlock(this, this, true, true);
        level.updateComparatorOutputLevel(this);
        activeDetectors.add(this);
    }

    @Override
    public boolean isActive() {
        return getBooleanValue(ACTIVE);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public OptionalBoolean isRailActive() {
        return OptionalBoolean.of(getBooleanValue(ACTIVE));
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setRailActive(boolean active) {
        setBooleanValue(ACTIVE, active);
    }
}
