package org.powernukkitx.block;

import org.powernukkitx.block.definition.BlockDefinition;

import org.powernukkitx.entity.Entity;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemString;
import org.powernukkitx.item.ItemTool;
import org.jetbrains.annotations.NotNull;

public class BlockWeb extends BlockFlowable {
    public static final BlockProperties PROPERTIES = new BlockProperties(WEB);
    public static final BlockDefinition DEFINITION = FLOWABLE.toBuilder()
            .hardness(4)
            .resistance(20)
            .toolType(ItemTool.TYPE_SWORD)
            .build();

    @Override
    @NotNull public BlockProperties getProperties() {
        return PROPERTIES;
    }

    public BlockWeb() {
        this(PROPERTIES.getDefaultState());
    }

    public BlockWeb(BlockState blockstate) {
        super(blockstate, DEFINITION);
    }

    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    this.toItem()
            };
        } else if (item.isSword()) {
            return new Item[]{
                    new ItemString()
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public boolean diffusesSkyLight() {
        return true;
    }
}