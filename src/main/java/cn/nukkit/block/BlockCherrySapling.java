package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

@Since("1.20.0-r2")
@PowerNukkitXOnly
public class BlockCherrySapling extends BlockFlowable {

    public static final BlockProperties PROPERTIES = new BlockProperties(BlockSapling.AGED);

    @PowerNukkitOnly
    public BlockCherrySapling() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockCherrySapling(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_SAPLING;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Cherry Sapling";
    }

    @Override
    public int onUpdate(int type) {
        //todo
        return 0;
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, Player player) {
        if (isSupportInvalid()) {
            return false;
        }

        if (this.getLevelBlock() instanceof BlockLiquid || this.getLevelBlockAtLayer(1) instanceof BlockLiquid) {
            return false;
        }

        this.level.setBlock(this, this, true, true);
        return true;
    }


    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player) {
        //todo
        return false;
    }

    private boolean isSupportInvalid() {
        int downId = down().getId();
        return downId != DIRT && downId != GRASS && downId != SAND && downId != GRAVEL && downId != PODZOL;
    }

    public boolean isAge() {
        return this.getPropertyValue(BlockSapling.AGED);
    }

    public void setAge(boolean age) {
        this.setBooleanValue(BlockSapling.AGED, age);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockCherrySapling());
    }
}
