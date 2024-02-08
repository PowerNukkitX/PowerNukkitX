package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;


public class BlockCarvedPumpkin extends BlockPumpkin {
    public static final BlockProperties PROPERTIES = new BlockProperties(CARVED_PUMPKIN, CommonBlockProperties.MINECRAFT_CARDINAL_DIRECTION);

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockCarvedPumpkin() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockCarvedPumpkin(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Carved Pumpkin";
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }
}
