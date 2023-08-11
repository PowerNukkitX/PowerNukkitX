package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.block.property.IntBlockProperty;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import java.util.concurrent.ThreadLocalRandom;
import org.jetbrains.annotations.NotNull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class BlockCrops extends BlockFlowable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final IntBlockProperty GROWTH = new IntBlockProperty("growth", false, 7);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(GROWTH);

    @PowerNukkitOnly
    public static final int MINIMUM_LIGHT_LEVEL = 9;

    protected BlockCrops(int meta) {
        super(meta);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getMinimumLightLevel() {
        return MINIMUM_LIGHT_LEVEL;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getMaxGrowth() {
        return GROWTH.getMaxValue();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getGrowth() {
        return getIntValue(GROWTH);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setGrowth(int growth) {
        setIntValue(GROWTH, growth);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public boolean isFullyGrown() {
        return getGrowth() >= getMaxGrowth();
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean place(
            @NotNull Item item,
            @NotNull Block block,
            @NotNull Block target,
            @NotNull BlockFace face,
            double fx,
            double fy,
            double fz,
            Player player) {
        if (block.down().getId() == FARMLAND) {
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        // Bone meal
        if (item.isFertilizer()) {
            int max = getMaxGrowth();
            int growth = getGrowth();
            if (growth < max) {
                BlockCrops block = (BlockCrops) this.clone();
                growth += ThreadLocalRandom.current().nextInt(3) + 2;
                block.setGrowth(Math.min(growth, max));
                BlockGrowEvent event = new BlockGrowEvent(this, block);
                event.call();

                if (event.isCancelled()) {
                    return false;
                }

                this.getLevel().setBlock(this, event.getNewState(), false, true);
                this.getLevel().addParticle(new BoneMealParticle(this));

                if (player != null && !player.isCreative()) {
                    item.count--;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() != FARMLAND) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(2) == 1
                    && getLevel().getFullLight(this) >= getMinimumLightLevel()) {
                int growth = getGrowth();
                if (growth < getMaxGrowth()) {
                    BlockCrops block = (BlockCrops) this.clone();
                    block.setGrowth(growth + 1);
                    BlockGrowEvent event = new BlockGrowEvent(this, block);
                    event.call();

                    if (!event.isCancelled()) {
                        this.getLevel().setBlock(this, event.getNewState(), false, true);
                    } else {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            } else {
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }

        return 0;
    }

    @Override
    public boolean isFertilizable() {
        return true;
    }
}
