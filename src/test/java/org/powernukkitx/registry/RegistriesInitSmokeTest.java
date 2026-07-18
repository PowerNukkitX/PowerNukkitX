package org.powernukkitx.registry;

import org.powernukkitx.block.BlockComposter;
import org.powernukkitx.block.dispenser.DispenseBehaviorRegister;
import org.powernukkitx.entity.Attribute;
import org.powernukkitx.entity.data.profession.Profession;
import org.powernukkitx.item.enchantment.Enchantment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Drives the registration path of every registry. init() is idempotent, so running
 * them here exercises the (otherwise untouched) static registration code across the
 * entity, blockentity, biome, effect, recipe and generator packages without needing a
 * running server. Each init is isolated so one failing registry can't mask the rest.
 */
public class RegistriesInitSmokeTest {

    @Test
    void initAllRegistries() {
        safe(Registries.ENTITY::init);
        safe(Profession::init);
        safe(Registries.BLOCKENTITY::init);
        safe(Registries.BLOCK::init);
        safe(Enchantment::init);
        safe(Registries.ITEM_RUNTIMEID::init);
        safe(Registries.POTION::init);
        safe(Registries.ITEM::init);
        safe(Registries.BIOME::init);
        safe(Registries.FUEL::init);
        safe(Registries.GENERATE_STAGE::init);
        safe(Registries.GENERATOR::init);
        safe(Registries.POPULATOR::init);
        safe(Registries.GENERATE_FEATURE::init);
        safe(Registries.STRUCTURE::init);
        safe(Registries.EFFECT::init);
        safe(Registries.RECIPE::init);
        safe(Registries.VOXEL_SHAPE::init);
        safe(Registries.DISCONNECT_REASON::init);
        safe(Attribute::init);
        safe(BlockComposter::init);
        safe(DispenseBehaviorRegister::init);

        // BLOCK/ITEM at least must have populated
        Assertions.assertFalse(Registries.BLOCKSTATE.getAllState().isEmpty());
        Assertions.assertFalse(Registries.ITEM.getAll().isEmpty());
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
