package cn.nukkit.block;

import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.DoublePlantType;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockPitcherPlant extends BlockDoublePlant implements Pollinable {
    public static final BlockProperties PROPERTIES = new BlockProperties(PITCHER_PLANT, CommonBlockProperties.UPPER_BLOCK_BIT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockPitcherPlant() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockPitcherPlant(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public @NotNull DoublePlantType getDoublePlantType() {
        return DoublePlantType.PITCHER_PLANT;
    }

    public String getName() {
        return "Pitcher Plant";
    }

    @Override
    public boolean canBeActivated() {
        return false;
    }

    @Override
    public boolean onActivate(@NotNull Item item, Player player, BlockFace blockFace, float fx, float fy, float fz) {
        return false;
    }

    @Override
    public boolean isFertilizable() {
        return false;
    }
}
