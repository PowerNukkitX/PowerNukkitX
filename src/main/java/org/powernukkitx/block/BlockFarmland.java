package org.powernukkitx.block;

import org.powernukkitx.block.property.CommonBlockProperties;
import org.powernukkitx.event.block.FarmLandDecayEvent;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemBlock;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.powernukkitx.math.AxisAlignedBB;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.SimpleAxisAlignedBB;
import org.powernukkitx.math.Vector3;
import org.jetbrains.annotations.NotNull;

public class BlockFarmland extends BlockTransparent {
    public static final BlockProperties PROPERTIES = new BlockProperties(FARMLAND, CommonBlockProperties.MOISTURIZED_AMOUNT);

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockFarmland() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockFarmland(BlockState blockstate) {
        super(blockstate);
    }

    @Override
    public String getName() {
        return "Farmland";
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public int onUpdate(int type) {
        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BlockID.DIRT));
    }

    @Override
    public boolean isSolid(BlockFace side) {
        return true;
    }

    public int getMoistureAmount() {
        return 7;
    }

    public void setMoistureAmount() {
        setPropertyValue(CommonBlockProperties.MOISTURIZED_AMOUNT, 7);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return new SimpleAxisAlignedBB(
                this.x,
                this.y,
                this.z,
                this.x + 1,
                this.y + (15D/16D),
                this.z + 1
        );
    }
}
