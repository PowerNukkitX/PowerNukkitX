package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimMaterialType;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemQuartz extends Item implements ItemTrimMaterial {

    public ItemQuartz() {
        this(0, 1);
    }

    public ItemQuartz(Integer meta) {
        this(meta, 1);
    }

    public ItemQuartz(Integer meta, int count) {
        super(NETHER_QUARTZ, 0, count, "Nether Quartz");
    }

    @Override
    public ItemTrimMaterialType getMaterial() {
        return ItemTrimMaterialType.MATERIAL_QUARTZ;
    }
}
