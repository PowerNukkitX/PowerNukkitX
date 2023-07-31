package cn.nukkit.block.impl;

import static cn.nukkit.block.property.CommonBlockProperties.PILLAR_AXIS;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.PowerNukkitXOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.BlockSolidMeta;
import cn.nukkit.block.property.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import org.jetbrains.annotations.NotNull;

/**
 * @author GoodLucky777
 */
@PowerNukkitOnly
@Since("FUTURE")
public class BlockInfestedDeepslate extends BlockSolidMeta {
    @PowerNukkitXOnly
    @Since("1.6.0.0-PNX")
    public static final BlockProperties PROPERTIES = new BlockProperties(PILLAR_AXIS);

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockInfestedDeepslate() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("FUTURE")
    public BlockInfestedDeepslate(int meta) {
        super(meta);
    }

    @Since("1.6.0.0-PN")
    @PowerNukkitOnly
    @NotNull @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public int getId() {
        return INFESTED_DEEPSLATE;
    }

    @Override
    public String getName() {
        return "Infested Deepslate";
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0.75;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public Item[] getDrops(Item item) {
        return Item.EMPTY_ARRAY;
    }
}
