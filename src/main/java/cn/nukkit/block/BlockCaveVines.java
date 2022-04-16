package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.DyeColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitOnly
@Since("1.6.0.0-PNX")
public class BlockCaveVines extends BlockTransparentMeta {
    public static final IntBlockProperty AGE_PROPERTY = new IntBlockProperty("growing_plant_age", false, 25, 0);
    public static final BlockProperties PROPERTIES = new BlockProperties(AGE_PROPERTY);

    private static final NukkitRandom RANDOM = new NukkitRandom();

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
    public boolean isTransparent() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (isValidSupport(this.up())) {
            setPropertyValue(AGE_PROPERTY, RANDOM.nextBoundedInt(25));
            return super.place(item, block, target, face, fx, fy, fz, player);
        }
        return false;
    }

    private static boolean isValidSupport(Block block) {
        return block.isSolid() || block instanceof BlockCaveVines;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item) {
        if (item.getId() == ItemID.DYE && item.getDamage() == DyeColor.BONE_MEAL.getDyeData() && this.getId() == CAVE_VINES) {
            if (up() instanceof BlockCaveVines) {
                final Block tmp = new BlockCaveVinesBodyWithBerries();
                tmp.setPropertyValue(BlockCaveVinesBodyWithBerries.AGE_PROPERTY, this.getPropertyValue(AGE_PROPERTY));
                getLevel().setBlock(this, tmp, true, true);
            } else {
                final Block tmp = new BlockCaveVinesHeadWithBerries();
                tmp.setPropertyValue(BlockCaveVinesHeadWithBerries.AGE_PROPERTY, this.getPropertyValue(AGE_PROPERTY));
                getLevel().setBlock(this, tmp, true, true);
            }
            return true;
        }
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }
}
