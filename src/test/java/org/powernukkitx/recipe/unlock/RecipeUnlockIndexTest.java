package org.powernukkitx.recipe.unlock;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.recipe.MultiRecipe;
import org.powernukkitx.recipe.ShapelessRecipe;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.recipe.descriptor.ItemTagDescriptor;
import org.powernukkitx.registry.Registries;
import org.powernukkitx.tags.ItemTags;

import java.util.List;
import java.util.Set;
import java.util.UUID;

class RecipeUnlockIndexTest {
    private static final String TAG = "unlock_index_test_tag";

    private static Item planks;
    private static Item diamond;

    @BeforeAll
    @SneakyThrows
    static void before() {
        Registries.POTION.init();
        Registries.BLOCK.init();
        Registries.ITEM.init();
        Registries.ITEM_RUNTIMEID.init();
        planks = Item.get(ItemID.PLANKS);
        diamond = Item.get(ItemID.DIAMOND);
        ItemTags.register(diamond.getId(), List.of(TAG));
    }

    private static ShapelessRecipe recipe(String id, ItemDescriptor... ingredients) {
        return new ShapelessRecipe(id, UUID.randomUUID(), 1, 0, Item.get(ItemID.STICK), List.of(ingredients));
    }

    @Test
    void indexesDirectIngredients() {
        RecipeUnlockIndex index = new RecipeUnlockIndex();
        index.index(recipe("direct", new DefaultDescriptor(planks)));

        Assertions.assertEquals(Set.of("direct"), index.getCandidates(planks.getId()));
        Assertions.assertTrue(index.getCandidates(Item.get(ItemID.STICK).getId()).isEmpty());
    }

    @Test
    void indexesTaggedIngredients() {
        RecipeUnlockIndex index = new RecipeUnlockIndex();
        index.index(recipe("tagged", new ItemTagDescriptor(TAG, 1)));

        Assertions.assertEquals(Set.of("tagged"), index.getCandidates(diamond.getId()));
    }

    @Test
    void mergesDirectAndTaggedCandidates() {
        RecipeUnlockIndex index = new RecipeUnlockIndex();
        index.index(recipe("direct", new DefaultDescriptor(diamond)));
        index.index(recipe("tagged", new ItemTagDescriptor(TAG, 1)));

        Assertions.assertEquals(Set.of("direct", "tagged"), index.getCandidates(diamond.getId()));
    }

    @Test
    void skipsRecipesTheBookNeverShows() {
        RecipeUnlockIndex index = new RecipeUnlockIndex();
        index.index(new MultiRecipe(UUID.randomUUID(), 1));

        Assertions.assertTrue(index.getCandidates(planks.getId()).isEmpty());
    }

    @Test
    void clearDropsEverything() {
        RecipeUnlockIndex index = new RecipeUnlockIndex();
        index.index(recipe("direct", new DefaultDescriptor(planks)));
        index.clear();

        Assertions.assertTrue(index.getCandidates(planks.getId()).isEmpty());
    }
}
