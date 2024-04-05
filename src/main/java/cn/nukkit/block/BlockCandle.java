package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Objects;

import static cn.nukkit.block.property.CommonBlockProperties.CANDLES;
import static cn.nukkit.block.property.CommonBlockProperties.LIT;

/**
 * @author Gabriel8579
 * @since 2021-07-14
 */


public class BlockCandle extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(CANDLE, CANDLES, LIT);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCandle() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCandle(BlockState blockstate) {
        super(blockstate);
    }

    public Block toCakeForm() {
        return new BlockCandleCake();
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (target.getId().equals(BlockID.CAKE) && target.isDefaultState()) {//必须是完整的蛋糕才能插蜡烛
            target.getLevel().setBlock(target, toCakeForm(), true, true);
            return true;
        }
        if (!(target instanceof BlockCandle) && target.up() instanceof BlockCandle) {
            target = target.up();
        }
        if (target.getId().equals(getId())) {
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
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        if (!Objects.equals(item.getId(), ItemID.FLINT_AND_STEEL) && !item.isNull()) {
            return false;
        }

        if (getPropertyValue(LIT) && !Objects.equals(item.getId(), ItemID.FLINT_AND_STEEL)) {
            setPropertyValue(LIT, false);
            getLevel().addSound(this, Sound.RANDOM_FIZZ);
            getLevel().setBlock(this, this, true, true);
            return true;
        } else if (!getPropertyValue(LIT) && Objects.equals(item.getId(), ItemID.FLINT_AND_STEEL)) {
            setPropertyValue(LIT, true);
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
    public int getLightLevel() {
        return getPropertyValue(LIT) ? getPropertyValue(CANDLES) * 3 : 0;
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @Override
    public double getResistance() {
        return 0.1;
    }
}