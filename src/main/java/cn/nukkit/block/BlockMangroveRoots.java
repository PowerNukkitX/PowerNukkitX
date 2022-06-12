package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

import javax.annotation.Nonnull;

//todo 2022/06/09 实现未完成的功能
@PowerNukkitXOnly
@Since("1.6.0.0-PNX")
public class BlockMangroveRoots extends BlockTransparentMeta{
    public BlockMangroveRoots() {
        super(0);
    }

    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return CommonBlockProperties.EMPTY_PROPERTIES;
    }

    @Override
    public String getName() {
        return "Mangrove Roots";
    }

    @Override
    public int getId() {
        return MANGROVE_ROOTS;
    }


    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public double getHardness() {
        return 0.7;
    }

    @Override
    public double getResistance() {
        return 0.7;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int onUpdate(int type) {
        return 0;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        return true;
    }
}
