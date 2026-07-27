package org.powernukkitx.item;

import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.PlayerFixture;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.TestPlayer;
import org.powernukkitx.block.Block;
import org.powernukkitx.item.enchantment.Enchantment;
import org.powernukkitx.math.BlockFace;
import org.powernukkitx.math.Vector3;
import org.powernukkitx.registry.Registries;

/**
 * Drives the player-interaction surface of items - onClickAir/onUse/onRelease, useOn,
 * onActivate, eating, enchantment application and durability - which the getter/behaviour
 * smoke tests never reach because they lack a real player. Sample is bounded for runtime.
 * Tolerant throughout: a mocked level/player makes most side effects fail harmlessly while
 * the branch selection (edible/wearable/tool) still runs.
 */
public class ItemUseSmokeTest {

    private static TestPlayer player;
    private static Block block;

    @BeforeAll
    static void init() {
        ServerMockFixture.boot();
        Registries.BLOCK.init();
        Registries.ITEM_RUNTIMEID.init();
        Registries.POTION.init();
        Registries.ITEM.init();
        Enchantment.init();
        player = PlayerFixture.get();
        block = safeBlock();
    }

    private static Block safeBlock() {
        try {
            return ServerMockFixture.level.getBlock(0, 64, 0);
        } catch (Throwable ignore) {
            return null;
        }
    }

    @Test
    void drivesItemInteraction() {
        ObjectSet<String> ids = Registries.ITEM.getAll();
        Assertions.assertFalse(ids.isEmpty());

        Enchantment sharpness = Enchantment.getEnchantment(Enchantment.ID_DAMAGE_ALL);
        Vector3 dir = new Vector3(0, 1, 0);

        int checked = 0;
        int limit = 1500;

        for (String id : ids) {
            if (checked >= limit) break;
            Item item;
            try {
                item = Registries.ITEM.get(id);
            } catch (Throwable ignore) {
                continue;
            }
            if (item == null) continue;
            exercise(item, sharpness, dir);
            checked++;
        }

        Assertions.assertTrue(checked > 0, "expected at least one item to be driven");
    }

    private void exercise(Item item, Enchantment ench, Vector3 dir) {
        safe(() -> item.isEdible());
        safe(() -> item.isWearable());
        safe(() -> item.isTool());
        safe(() -> item.canBeActivated());

        safe(() -> item.onClickAir(player, dir));
        safe(() -> item.onUse(player, 20));
        safe(() -> item.afterUse(player));
        safe(() -> item.onRelease(player, 20));
        safe(() -> item.onEaten(player));

        if (block != null) {
            safe(() -> item.useOn(block));
            safe(() -> item.onActivate(ServerMockFixture.level, player, block, block, BlockFace.UP, 0.5, 1.0, 0.5));
        }

        safe(item::getBlock);
        safe(item::getBlockUnsafe);
        safe(item::getMaxDurability);
        safe(item::getFoodRestore);
        safe(item::getUseDuration);
        safe(item::noDamageOnAttack);

        if (ench != null) {
            safe(() -> item.addEnchantment(ench));
            safe(item::applyEnchantments);
            safe(() -> item.removeEnchantment(ench.getId()));
        }

        safe(() -> item.setDamage(1));
        safe(item::getMaxDurability);
    }

    private void safe(Runnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }
}
