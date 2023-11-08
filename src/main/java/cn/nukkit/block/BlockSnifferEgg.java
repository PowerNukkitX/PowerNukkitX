package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//todo complete
@PowerNukkitXOnly
@Since("1.20.10-r2")
public class BlockSnifferEgg extends BlockTransparentMeta {
    public static final ArrayBlockProperty<String> CRACKED_STATE = new ArrayBlockProperty("cracked_state", false, new String[]{"cracked", "max_cracked", "no_cracks"});

    public static final BlockProperties PROPERTIES = new BlockProperties(CRACKED_STATE);

    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSnifferEgg() {
    }

    public BlockSnifferEgg(int meta) {
        super(meta);
    }

    public int getId() {
        return SNIFFER_EGG;
    }

    public String getName() {
        return "Sniffer Egg";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        this.setPropertyValue(CRACKED_STATE, "no_cracks");
        return this.getLevel().setBlock(this, this);
    }
}