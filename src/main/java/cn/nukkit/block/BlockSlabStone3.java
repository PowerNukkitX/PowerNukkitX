package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.StoneSlab3Type;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

@PowerNukkitOnly
public class BlockSlabStone3 extends BlockSlab {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(
            StoneSlab3Type.PROPERTY,
            TOP_SLOT_PROPERTY
    );
    
    @PowerNukkitOnly public static final int END_STONE_BRICKS = 0;
    @PowerNukkitOnly public static final int SMOOTH_RED_SANDSTONE = 1;
    @PowerNukkitOnly public static final int POLISHED_ANDESITE = 2;
    @PowerNukkitOnly public static final int ANDESITE = 3;
    @PowerNukkitOnly public static final int DIORITE = 4;
    @PowerNukkitOnly public static final int POLISHED_DIORITE = 5;
    @PowerNukkitOnly public static final int GRANITE = 6;
    @PowerNukkitOnly public static final int POLISHED_GRANITE = 7;

    @PowerNukkitOnly
    public BlockSlabStone3() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockSlabStone3(int meta) {
        super(meta, DOUBLE_STONE_SLAB3);
    }

    @Override
    public int getId() {
        return STONE_SLAB3;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @NotNull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @PowerNukkitOnly
    @Override
    public String getSlabName() {
        return getSlabType().getEnglishName();
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
    public boolean isSameType(BlockSlab slab) {
        return slab.getId() == getId() && getSlabType().equals(slab.getPropertyValue(StoneSlab3Type.PROPERTY));
    }


    @Override
    @PowerNukkitOnly
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
