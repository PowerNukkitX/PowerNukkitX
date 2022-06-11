package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import org.jetbrains.annotations.NotNull;

public class BlockMangroveLeaves extends BlockLeaves{

    public static final BlockProperties PROPERTIES = new BlockProperties(PERSISTENT, UPDATE);

    @Override
    public String getName() {
        return "Mangrove Leaves";
    }

    @Override
    public int getId() {
        return MANGROVE_LEAVES;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
}
