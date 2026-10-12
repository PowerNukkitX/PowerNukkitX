package org.powernukkitx.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.entity.components.AttackComponent;
import org.powernukkitx.entity.components.AttackDamageComponent;
import org.powernukkitx.nbt.tag.CompoundTag;

import java.util.Random;

/**
 * Tests the attack/attack_damage attribute interaction.
 *
 * @author Curse
 */
public class AttackDamageParityTest {

    @BeforeAll
    static void initAttributes() {
        Attribute.init();
    }

    @Test
    void directAttackDamageKeepsRegistryBounds() {
        Attribute attribute = EntityLiving.createAttackDamageAttribute(
                null,
                AttackDamageComponent.value(4),
                new Random(1)
        );

        Assertions.assertNotNull(attribute);
        Assertions.assertEquals(0f, attribute.getMinValue(), 0f);
        Assertions.assertEquals(Float.MAX_VALUE, attribute.getMaxValue(), 0f);
        Assertions.assertEquals(0f, attribute.getDefaultMinimum(), 0f);
        Assertions.assertEquals(Float.MAX_VALUE, attribute.getDefaultMaximum(), 0f);
        Assertions.assertEquals(4f, attribute.getDefaultValue(), 0f);
        Assertions.assertEquals(4f, attribute.getValue(), 0f);
    }

    @Test
    void attackIsAppliedBeforeDirectAttackDamage() {
        Attribute attribute = EntityLiving.createAttackDamageAttribute(
                AttackComponent.value(8),
                AttackDamageComponent.value(4),
                new Random(1)
        );

        Assertions.assertNotNull(attribute);
        Assertions.assertEquals(8f, attribute.getMinValue(), 0f);
        Assertions.assertEquals(8f, attribute.getMaxValue(), 0f);
        Assertions.assertEquals(8f, attribute.getDefaultMinimum(), 0f);
        Assertions.assertEquals(8f, attribute.getDefaultMaximum(), 0f);
        Assertions.assertEquals(8f, attribute.getDefaultValue(), 0f);
        Assertions.assertEquals(8f, attribute.getValue(), 0f);
    }

    @Test
    void rangedAttackUpdatesBaseAndCurrentWithoutChangingBounds() {
        AttackComponent attack = AttackComponent.range(5, 10);
        Attribute attribute = EntityLiving.createAttackDamageAttribute(attack, null, new Random(1));

        Assertions.assertNotNull(attribute);
        Assertions.assertEquals(5f, attribute.getMinValue(), 0f);
        Assertions.assertEquals(5f, attribute.getMaxValue(), 0f);

        float damage = EntityLiving.rollAttackDamage(attribute, attack, new Random(2));

        Assertions.assertTrue(damage >= 5f && damage <= 10f);
        Assertions.assertEquals(5f, attribute.getMinValue(), 0f);
        Assertions.assertEquals(5f, attribute.getMaxValue(), 0f);
        Assertions.assertEquals(damage, attribute.getDefaultValue(), 0f);
        Assertions.assertEquals(damage, attribute.getValue(), 0f);
    }

    @Test
    void persistedAttackDamageDoesNotClampBaseOrCurrentToBounds() {
        CompoundTag nbt = new CompoundTag()
                .putString("Name", "minecraft:attack_damage")
                .putFloat("DefaultMin", 5)
                .putFloat("DefaultMax", 5)
                .putFloat("Min", 5)
                .putFloat("Max", 5)
                .putFloat("Base", 8)
                .putFloat("Current", 8);

        Attribute attribute = Attribute.fromNBT(nbt);

        Assertions.assertEquals(5f, attribute.getMinValue(), 0f);
        Assertions.assertEquals(5f, attribute.getMaxValue(), 0f);
        Assertions.assertEquals(8f, attribute.getDefaultValue(), 0f);
        Assertions.assertEquals(8f, attribute.getValue(), 0f);
    }
}
