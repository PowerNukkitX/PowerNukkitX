package org.powernukkitx.entity.data.sulfurcube;

import org.powernukkitx.entity.data.property.EnumEntityProperty;
import org.powernukkitx.tags.ItemTags;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SulfurCubeArchetypeTest {

    @Test
    void everyArchetypeIsBackedByAGamedataTag() {
        for (SulfurCubeArchetype archetype : SulfurCubeArchetype.values()) {
            assertFalse(ItemTags.getItemSet(archetype.getItemTag()).isEmpty(),
                    "no items tagged " + archetype.getItemTag());
        }
    }

    @Test
    void taggedBlocksResolveToTheirArchetype() {
        assertSame(SulfurCubeArchetype.EXPLOSIVE, SulfurCubeArchetype.fromIdentifier("minecraft:tnt"));
        assertSame(SulfurCubeArchetype.HOT, SulfurCubeArchetype.fromIdentifier("minecraft:magma"));
        assertSame(SulfurCubeArchetype.STICKY, SulfurCubeArchetype.fromIdentifier("minecraft:honeycomb_block"));
    }

    @Test
    void untaggedIdentifierHasNoArchetype() {
        assertNull(SulfurCubeArchetype.fromIdentifier(null));
        assertNull(SulfurCubeArchetype.fromIdentifier("minecraft:not_a_real_block"));
    }

    @Test
    void swallowedBlockFallsBackToRegular() {
        assertNull(SulfurCubeArchetype.forSwallowed(null));
        assertSame(SulfurCubeArchetype.REGULAR, SulfurCubeArchetype.forSwallowed("minecraft:not_a_real_block"));
        assertSame(SulfurCubeArchetype.EXPLOSIVE, SulfurCubeArchetype.forSwallowed("minecraft:tnt"));
    }

    @Test
    void knockbackFactorIsNeverNegative() {
        for (SulfurCubeArchetype archetype : SulfurCubeArchetype.values()) {
            assertTrue(archetype.getKnockbackFactor() >= 0f, archetype.name());
        }
    }

    @Test
    void keepFactorsStayWithinUnitRange() {
        for (SulfurCubeArchetype archetype : SulfurCubeArchetype.values()) {
            assertTrue(archetype.getGroundKeepPerTick() >= 0f && archetype.getGroundKeepPerTick() <= 1f,
                    archetype.name() + " ground " + archetype.getGroundKeepPerTick());
            assertTrue(archetype.getAirKeepPerTick() >= 0f && archetype.getAirKeepPerTick() <= 1f,
                    archetype.name() + " air " + archetype.getAirKeepPerTick());
        }
    }

    @Test
    void entityPropertyListsNoneFirstThenEveryArchetype() {
        EnumEntityProperty property = SulfurCubeArchetype.entityProperty();
        assertNotNull(property);
        assertEquals(SulfurCubeArchetype.NONE, property.getDefaultValue());

        String[] enums = property.getEnums();
        assertEquals(SulfurCubeArchetype.values().length + 1, enums.length);
        assertEquals(SulfurCubeArchetype.NONE, enums[0]);
        for (SulfurCubeArchetype archetype : SulfurCubeArchetype.values()) {
            assertEquals(archetype.getPropertyName(), enums[archetype.ordinal() + 1]);
        }
        assertEquals(enums.length, Arrays.stream(enums).distinct().count());
    }
}
