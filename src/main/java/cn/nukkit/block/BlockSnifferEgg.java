package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.CrackedState;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//todo complete
public class BlockSnifferEgg extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(SNIFFER_EGG, CommonBlockProperties.CRACKED_STATE);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSnifferEgg() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockSnifferEgg(BlockState blockstate) {
        super(blockstate);
    }

    public String getName() {
        return "Sniffer Egg";
    }

    @Override
    public boolean place(@NotNull Item item, @NotNull Block block, @NotNull Block target, @NotNull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        this.setPropertyValue(CommonBlockProperties.CRACKED_STATE, CrackedState.NO_CRACKS);
        return this.getLevel().setBlock(this, this);
    }
}