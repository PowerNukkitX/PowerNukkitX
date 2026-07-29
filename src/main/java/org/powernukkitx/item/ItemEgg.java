package org.powernukkitx.item;

import org.powernukkitx.entity.ClimateVariant;
import org.powernukkitx.item.definition.ItemDefinition;
import org.powernukkitx.nbt.tag.CompoundTag;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemEgg extends ProjectileItem {
    public static final ItemDefinition DEFINITION = ProjectileItem.DEFINITION.toBuilder()
            .maxStackSize(16)
            .build();

    public ItemEgg() {
        this(0, 1, DEFINITION);
    }

    public ItemEgg(ItemDefinition definition) {
        this(0, 1, definition);
    }

    public ItemEgg(Integer meta) {
        this(meta, 1, DEFINITION);
    }

    public ItemEgg(Integer meta, ItemDefinition definition) {
        this(meta, 1, definition);
    }

    public ItemEgg(Integer meta, int count) {
        this(meta, count, DEFINITION);
    }

    public ItemEgg(Integer meta, int count, ItemDefinition definition) {
        super(EGG, meta, count, "Egg", definition);
    }

    protected ItemEgg(String id, Integer meta, int count, String name) {
        this(id, meta, count, name, DEFINITION);
    }

    protected ItemEgg(String id, Integer meta, int count, String name, ItemDefinition definition) {
        super(id, meta, count, name, definition);
    }

    @Override
    public String getProjectileEntityType() {
        return EGG;
    }

    @Override
    public float getThrowForce() {
        return 1.5f;
    }

    @Override
    protected void correctNBT(CompoundTag nbt) {
        nbt.putString("variant", ClimateVariant.Variant.TEMPERATE.getName());
    }
}
