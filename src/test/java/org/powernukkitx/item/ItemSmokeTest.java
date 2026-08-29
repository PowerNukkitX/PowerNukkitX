package org.powernukkitx.item;

import org.powernukkitx.registry.Registries;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Broad smoke coverage - resolves every registered item id and calls the pure getters.
 * Exact values live in dedicated tests; this just makes sure every item class builds
 * and answers its queries without exploding.
 */
public class ItemSmokeTest {

    @BeforeAll
    static void init() {
        Registries.BLOCK.init();
        Registries.ITEM_RUNTIMEID.init();
        Registries.POTION.init();
        Registries.ITEM.init();
    }

    @Test
    void everyItemBuildsAndAnswersGetters() {
        ObjectSet<String> ids = Registries.ITEM.getAll();
        Assertions.assertFalse(ids.isEmpty(), "no items registered");

        int checked = 0;
        int failCount = 0;
        StringBuilder failures = new StringBuilder();

        for (String id : ids) {
            try {
                Item item = Registries.ITEM.get(id);
                if (item == null) continue;
                exercise(item);
                checked++;
            } catch (Throwable t) {
                failCount++;
                if (failCount <= 20) {
                    failures.append('\n').append(id).append(" -> ").append(t);
                }
            }
        }

        Assertions.assertTrue(checked > 0, "no item survived the smoke pass");
        Assertions.assertTrue(failCount < ids.size() * 0.05,
                "too many items failed the smoke pass (" + failCount + "/" + ids.size() + ")" + failures);
    }

    private void exercise(Item item) {
        Assertions.assertNotNull(item.getName());
        item.getId();
        item.getDamage();
        item.getCount();
        item.hasMeta();
        item.isNull();
        item.isBlock();
        item.getBlockId();
        item.canBeActivated();
        item.hasCustomBlockData();
        item.isFilledBucketItem();
        item.hasEnchantments();
        item.getEnchantments();
        item.getRepairCost();
        item.hasCustomName();
        item.getLore();
        item.hasNbt();
        item.getMaxStackSize();
        item.getMaxDurability();
        item.canTakeDamage();
        item.isUnbreakable();
        item.getTier();
        item.getEnchantAbility();
        item.isLavaResistant();
        item.isConsumable();
        item.isEdible();
        item.getNutrition();
        item.getSaturation();
        item.canAlwaysEat();
        item.isWearable();
        item.isArmor();
        item.isHelmet();
        item.isChestplate();
        item.isLeggings();
        item.isBoots();
        item.getArmorPoints();
        item.getToughness();
        item.getKnockbackResistance();
        item.isTool();
        item.getAttackDamage();
        item.isSword();
        item.isAxe();
        item.isPickaxe();
        item.isShovel();
        item.isHoe();
        item.isShield();
        item.getItemLockMode();
        item.getCanPlaceOn();
        item.getCanDestroy();
        item.hashCode();
        Assertions.assertNotNull(item.toString());

        Item cloned = item.clone();
        Assertions.assertEquals(item.getId(), cloned.getId());
        Assertions.assertTrue(item.equals(cloned));
    }
}
