package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.event.block.BlockFormEvent;
import org.powernukkitx.item.ItemTool;
import org.powernukkitx.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * @author Pub4Game
 * @since 27.12.2015
 */
public class BlockSoulSand extends BlockSolid {
    public static final BlockProperties PROPERTIES = new BlockProperties(SOUL_SAND);
    public static final BlockDefinition DEFINITION = SOLID.toBuilder()
            .hardness(0.5)
            .resistance(2.5)
            .toolType(ItemTool.TYPE_SHOVEL)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockSoulSand() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockSoulSand(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public String getName() {
        return "Soul Sand";
    }

    @Override
    public double getMaxY() {
        return this.y + 1;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean isSoulSpeedCompatible() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.motionX *= 0.4d;
        entity.motionZ *= 0.4d;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block up = up();
            if (up instanceof BlockFlowingWater w && (w.getLiquidDepth() == 0 || w.getLiquidDepth() == 8)) {
                BlockFormEvent event = new BlockFormEvent(up, new BlockBubbleColumn());
                if (!event.isCancelled()) {
                    if (event.getNewState().getWaterloggingLevel() > 0) {
                        this.getLevel().setBlock(up, 1, new BlockFlowingWater(), true, false);
                    }
                    this.getLevel().setBlock(up, 0, event.getNewState(), true, true);
                }
            }
        }
        return 0;
    }

}
