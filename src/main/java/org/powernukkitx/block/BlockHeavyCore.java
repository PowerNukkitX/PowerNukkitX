package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.item.ItemTool;
import org.powernukkitx.math.AxisAlignedBB;
import org.jetbrains.annotations.NotNull;

public class BlockHeavyCore extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(HEAVY_CORE);
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .hardness(10)
            .resistance(30)
            .toolType(ItemTool.TYPE_PICKAXE)
            .toolTier(ItemTool.TIER_WOODEN)
            .canPassThrough(false)
            .canBePulled(false)
            .breaksWhenMoved(false)
            .build();

    @Override
    @NotNull
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockHeavyCore() {
        super(PROPERTIES.getDefaultState());
    }

    public BlockHeavyCore(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return this;
    }

    @Override
    public double getMinX() {
        return this.x + 0.25;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.25;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.75;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.50;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.75;
    }

    
    @Override
    public String getName() {
        return "Heavy Core";
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    }