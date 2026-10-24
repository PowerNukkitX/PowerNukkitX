package org.powernukkitx.registry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class BiomeRegistryTagTest {

    @BeforeAll
    static void init() {
        Registries.BIOME.init();
    }

    @Test
    void containsTagMatchesTheBiomeTagList() {
        final Set<String> allTags = new HashSet<>();
        for (var definition : Registries.BIOME.getBiomeDefinitions()) {
            final List<Short> tags = definition.second().getTags();
            if (tags != null) {
                for (short tag : tags) {
                    allTags.add(Registries.BIOME.getFromBiomeStringList(tag));
                }
            }
        }
        Assertions.assertFalse(allTags.isEmpty());

        int checked = 0;
        for (var definition : Registries.BIOME.getBiomeDefinitions()) {
            final List<Short> tags = definition.second().getTags();
            final Set<String> own = new HashSet<>();
            if (tags != null) {
                for (short tag : tags) {
                    own.add(Registries.BIOME.getFromBiomeStringList(tag));
                }
            }
            for (String tag : allTags) {
                Assertions.assertEquals(own.contains(tag), Registries.BIOME.containsTag(tag, definition.second()),
                    "tag " + tag);
                checked++;
            }
        }
        Assertions.assertTrue(checked > 0);
    }

    @Test
    void unknownTagIsNotContained() {
        for (var definition : Registries.BIOME.getBiomeDefinitions()) {
            Assertions.assertFalse(Registries.BIOME.containsTag("pnx_test_no_such_tag", definition.second()));
        }
    }
}
