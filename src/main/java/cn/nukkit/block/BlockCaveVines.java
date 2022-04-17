package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class BlockCaveVines extends BlockTransparentMeta {
    public static final IntBlockProperty AGE_PROPERTY = new IntBlockProperty("growing_plant_age", false, 25, 0);
    public static final BlockProperties PROPERTIES = new BlockProperties(AGE_PROPERTY);

    @Override
    public String getName() {
        return "Cave Vines";
    }

    @Override
    public int getId() {
        return CAVE_VINES;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    public static boolean isValidSupport(Block block) {
        return block.isSolid() || block instanceof BlockCaveVines;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isValidSupport(this.up())) {
                this.getLevel().useBreakOn(this);
            }
        }
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            //random mature,The feature that I added.
            if (ThreadLocalRandom.current().nextInt(3) == 0) {
                int growth = getGrowth();
                if (growth + 4 < getMaxGrowth()) {
                    BlockCaveVines block = (BlockCaveVines) this.clone();
                    block.setGrowth(growth + 4);
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), false, true);
                    } else {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                } else {
                    BlockCaveVines block;
                    if (this.up() instanceof BlockCaveVines && !(this.down() instanceof BlockCaveVines)) {
                        block = new BlockCaveVinesHeadWithBerries();
                    } else block = new BlockCaveVinesBodyWithBerries();
                    block.setGrowth(getMaxGrowth());
                    BlockGrowEvent ev = new BlockGrowEvent(this, block);
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(this, ev.getNewState(), false, true);
                    } else {
                        return Level.BLOCK_UPDATE_RANDOM;
                    }
                }
            }
            //random grow feature,according to wiki in https://minecraft.fandom.com/wiki/Glow_Berries#Growth
            if (down().getId() == AIR && ThreadLocalRandom.current().nextInt(10) == 0) {
                BlockCaveVines block;
                if (this.up() instanceof BlockCaveVines && !(this.down() instanceof BlockCaveVines)) {
                    block = new BlockCaveVinesHeadWithBerries();
                } else block = new BlockCaveVinesBodyWithBerries();
                block.setGrowth(getMaxGrowth());
                BlockGrowEvent ev = new BlockGrowEvent(this, block);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.getLevel().setBlock(this.down(), ev.getNewState(), false, true);
                } else {
                    return Level.BLOCK_UPDATE_RANDOM;
                }
            } else if (down().getId() == AIR) {
                BlockCaveVines block = new BlockCaveVines();
                block.setGrowth(0);
                BlockGrowEvent ev = new BlockGrowEvent(this, block);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.getLevel().setBlock(this.down(), ev.getNewState(), false, true);
                } else {
                    return Level.BLOCK_UPDATE_RANDOM;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean onActivate(@NotNull Item item, @Nullable Player player) {
        if (item.isFertilizer()) {
            BlockCaveVines block;
            if (this.up() instanceof BlockCaveVines && !(this.down() instanceof BlockCaveVines)) {
                block = new BlockCaveVinesHeadWithBerries();
            } else block = new BlockCaveVinesBodyWithBerries();
            int max = getMaxGrowth();
            int growth = getGrowth();
            if (growth < max) {
                block.setGrowth(max);
                BlockGrowEvent ev = new BlockGrowEvent(this, block);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (ev.isCancelled()) {
                    return false;
                }
                this.getLevel().setBlock(this, ev.getNewState(), false, true);
                this.level.addParticle(new BoneMealParticle(this));
                if (player != null && !player.isCreative()) {
                    item.count--;
                }
            }
            return true;
        }
        if (item.isNull()) {
            if (this.getGrowth() == 25) {
                BlockCaveVines block = new BlockCaveVines();
                block.setGrowth(0);
                this.getLevel().setBlock(this, block, false, true);
                this.getLevel().dropItem(this, Item.get(ItemID.GLOW_BERRIES));
            }
            return true;
        }
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }

    private int getMaxGrowth() {
        return AGE_PROPERTY.getMaxValue();
    }

    private int getGrowth() {
        return getIntValue(AGE_PROPERTY);
    }

    private void setGrowth(int growth) {
        setIntValue(AGE_PROPERTY, growth);
    }
}
