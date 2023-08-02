package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import org.jetbrains.annotations.NotNull;

//todo complete
@PowerNukkitXOnly
@Since("1.20.10-r2")
public class BlockPitcherPlant extends BlockFlowable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BooleanBlockProperty UPPER_BLOCK = new BooleanBlockProperty("upper_block_bit", false);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(UPPER_BLOCK);

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPitcherPlant() {
    }

    public BlockPitcherPlant(int meta) {
        super(meta);
    }

    public int getId() {
        return PITCHER_PLANT;
    }

    public String getName() {
        return "Pitcher Plant";
    }
}