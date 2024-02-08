package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.property.CommonBlockProperties;
import cn.nukkit.block.property.enums.WoodType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import org.jetbrains.annotations.NotNull;

public class BlockStrippedCherryWood extends BlockWoodStripped {
    public static final BlockProperties PROPERTIES = new BlockProperties(STRIPPED_CHERRY_WOOD, CommonBlockProperties.PILLAR_AXIS);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockStrippedCherryWood() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockStrippedCherryWood(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Stripped Cherry Wood";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 5;
    }

    @Override
    public BlockState getStrippedState() {
        return getBlockState();
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
    public WoodType getWoodType() {
        throw new UnsupportedOperationException();
    }
}