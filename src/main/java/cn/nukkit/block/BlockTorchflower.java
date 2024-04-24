package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;


public class BlockTorchflower extends BlockFlower {
    public static final BlockProperties PROPERTIES = new BlockProperties(TORCHFLOWER);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockTorchflower() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockTorchflower(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }

    public String getName() {
        return "Torchflower";
    }
}