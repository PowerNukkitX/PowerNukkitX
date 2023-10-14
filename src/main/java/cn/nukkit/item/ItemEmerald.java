package cn.nukkit.item;

import cn.nukkit.item.trim.ItemTrimMaterialType;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemEmerald extends Item implements ItemTrimMaterial {

    public ItemEmerald() {
        this(0, 1);
    }

    public ItemEmerald(Integer meta) {
        this(meta, 1);
    }

    public ItemEmerald(Integer meta, int count) {
        super(EMERALD, meta, count, "Emerald");
    }

    @Override
    public ItemTrimMaterialType getMaterial() {
        return ItemTrimMaterialType.MATERIAL_EMERALD;
    }
}
