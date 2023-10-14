package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.trim.ItemTrimMaterialType;

@Since("1.4.0.0-PN")
public class ItemIngotNetherite extends Item implements ItemTrimMaterial {

    @Since("1.4.0.0-PN")
    public ItemIngotNetherite() {
        this(0, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemIngotNetherite(Integer meta) {
        this(meta, 1);
    }

    public ItemIngotNetherite(Integer meta, int count) {
        super(NETHERITE_INGOT, 0, count, "Netherite Ingot");
    }

    @PowerNukkitOnly
    @Override
    public boolean isLavaResistant() {
        return true;
    }

    @Override
    public ItemTrimMaterialType getMaterial() {
        return ItemTrimMaterialType.MATERIAL_NETHERITE;
    }
}
