package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.StoneSlab3Type;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

@PowerNukkitOnly
public class BlockDoubleSlabStone3 extends BlockDoubleSlabBase {
    @PowerNukkitOnly public static final int END_STONE_BRICKS = 0;
    @PowerNukkitOnly public static final int SMOOTH_RED_SANDSTONE = 1;
    @PowerNukkitOnly public static final int POLISHED_ANDESITE = 2;
    @PowerNukkitOnly public static final int ANDESITE = 3;
    @PowerNukkitOnly public static final int DIORITE = 4;
    @PowerNukkitOnly public static final int POLISHED_DIORITE = 5;
    @PowerNukkitOnly public static final int GRANITE = 6;
    @PowerNukkitOnly public static final int POLISHED_GRANITE = 7;

    @PowerNukkitOnly
    public BlockDoubleSlabStone3() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockDoubleSlabStone3(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_STONE_SLAB3;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return BlockSlabStone3.PROPERTIES;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public StoneSlab3Type getSlabType() {
        return getPropertyValue(StoneSlab3Type.PROPERTY);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setSlabType(StoneSlab3Type type) {
        setPropertyValue(StoneSlab3Type.PROPERTY, type);
    }

    @PowerNukkitOnly
    @Override
    public String getSlabName() {
        return getSlabType().getEnglishName();
    }

    @Override
    public double getResistance() {
        return getToolType() > ItemTool.TIER_WOODEN ? 30 : 15;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @PowerNukkitOnly
    @Override
    public int getSingleSlabId() {
        return STONE_SLAB3;
    }

    @Override
    @PowerNukkitOnly
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public BlockColor getColor() {
        return getSlabType().getColor();
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
