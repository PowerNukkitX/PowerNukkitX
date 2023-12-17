package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import org.jetbrains.annotations.NotNull;


public abstract class BlockDoubleSlabBase extends BlockSolidMeta {


    public BlockDoubleSlabBase(int meta) {
        super(meta);
    }


    public BlockDoubleSlabBase(){}

    @Override
    public String getName() {
        return "Double "+getSlabName()+" Slab";
    }


    @NotNull
    @Override
    public BlockProperties getProperties() {
        return BlockSlab.SIMPLE_SLAB_PROPERTIES;
    }


    public abstract String getSlabName();


    public abstract int getSingleSlabId();

    @Override
    public Item toItem() {
        return getCurrentState().forItem().withBlockId(getSingleSlabId()).asItemBlock();
    }


    protected boolean isCorrectTool(Item item) {
        return canHarvestWithHand() || canHarvest(item);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (isCorrectTool(item)) {
            Item slab = toItem();
            slab.setCount(2);
            return new Item[]{ slab };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
}
