package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Gabriel8579
 * @since 2021-07-14
 */

@PowerNukkitOnly
@Since("FUTURE")
public class BlockCandle extends BlockFlowable {

    @PowerNukkitOnly
    @Since("FUTURE")
    private static final IntBlockProperty LIT = new IntBlockProperty("lit", false, 1, 0, 1);

    @PowerNukkitOnly
    @Since("FUTURE")
    private static final IntBlockProperty CANDLES = new IntBlockProperty("candles", false, 3, 0, 2);

    @PowerNukkitOnly
    @Since("FUTURE")
    private static final BlockProperties PROPERTIES = new BlockProperties(LIT, CANDLES);

    public BlockCandle() {
        super(0);
    }

    public BlockCandle(int meta) {
        super(meta);
    }

    protected Block toCakeForm() {
        return new BlockCandleCake();
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (target.getId() == BlockID.CAKE_BLOCK) {
            target.getLevel().setBlock(target, toCakeForm(), true, true);
            return true;
        }
        if(target.up().getId() == getId()) {
            target = target.up();
        }
        if (target.getId() == getId()) {
            if (target.getPropertyValue(CANDLES) < 3) {
                target.setPropertyValue(CANDLES, target.getPropertyValue(CANDLES) + 1);
                getLevel().setBlock(target, target, true, true);
                return true;
            }
            return false;
        } else if (target instanceof BlockCandle) {
            return false;
        }

        setPropertyValue(CANDLES, 0);
        getLevel().setBlock(this, this, true, true);

        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {

        if (item.getId() != ItemID.FLINT_AND_STEEL && !item.isNull()) {
            return false;
        }

        if (getPropertyValue(LIT) == 1 && item.getId() != ItemID.FLINT_AND_STEEL) {
            setPropertyValue(LIT, 0);
            getLevel().addSound(this, Sound.RANDOM_FIZZ);
            getLevel().setBlock(this, this, true, true);
            return true;
        } else if (getPropertyValue(LIT) == 0 && item.getId() == ItemID.FLINT_AND_STEEL) {
            setPropertyValue(LIT, 1);
            getLevel().addSound(this, Sound.FIRE_IGNITE);
            getLevel().setBlock(this, this, true, true);
            return true;
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                new ItemBlock(this, 0, getPropertyValue(CANDLES) + 1)
        };
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public String getName() {
        return "Candle";
    }

    @Override
    public int getId() {
        return BlockID.CANDLE;
    }

    @Override
    public int getLightLevel() {
        return getPropertyValue(LIT) * getPropertyValue(CANDLES) * 3;
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @Override
    public double getResistance() {
        return 0.1;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
}