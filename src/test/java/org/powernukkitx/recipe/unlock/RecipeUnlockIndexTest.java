package org.powernukkitx.recipe.unlock;

import lombok.SneakyThrows;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingContext;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingRequirement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.recipe.FurnaceRecipe;
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

    private static RecipeUnlockingRequirement triggeredBy(ItemDescriptor... triggers) {
        RecipeUnlockingRequirement requirement = new RecipeUnlockingRequirement(RecipeUnlockingContext.NONE);
        for (ItemDescriptor trigger : triggers) {
            requirement.getUnlockingIngredients().add(trigger.toNetwork());
        }
        return requirement;
    }

    private static ShapelessRecipe recipe(String id, RecipeUnlockingRequirement requirement) {
        return new ShapelessRecipe(id, UUID.randomUUID(), 1, 0, Item.get(ItemID.STICK),
            List.of(new DefaultDescriptor(planks)), requirement);
    }

    @Test
    void indexesDirectTriggers() {
        RecipeUnlockIndex index = new RecipeUnlockIndex();
        index.index(recipe("direct", triggeredBy(new DefaultDescriptor(diamond))));

        Assertions.assertEquals(Set.of("direct"), index.getCandidates(diamond.getId()));
        Assertions.assertTrue(index.getCandidates(Item.get(ItemID.STICK).getId()).isEmpty());
    }

    @Test
    void indexesTaggedTriggers() {
        RecipeUnlockIndex index = new RecipeUnlockIndex();
        index.index(recipe("tagged", triggeredBy(new ItemTagDescriptor(TAG, 1))));

        Assertions.assertEquals(Set.of("tagged"), index.getCandidates(diamond.getId()));
    }

    @Test
    void mergesDirectAndTaggedCandidates() {
        RecipeUnlockIndex index = new RecipeUnlockIndex();
        index.index(recipe("direct", triggeredBy(new DefaultDescriptor(diamond))));
        index.index(recipe("tagged", triggeredBy(new ItemTagDescriptor(TAG, 1))));

        Assertions.assertEquals(Set.of("direct", "tagged"), index.getCandidates(diamond.getId()));
    }

    @Test
    void triggerItemDoesNotHaveToBeAnIngredient() {
        RecipeUnlockIndex index = new RecipeUnlockIndex();
        index.index(recipe("wooden_sword_like", triggeredBy(new DefaultDescriptor(diamond))));

        Assertions.assertTrue(index.getCandidates(planks.getId()).isEmpty());
        Assertions.assertEquals(Set.of("wooden_sword_like"), index.getCandidates(diamond.getId()));
    }

    @Test
    void skipsRecipesWithoutItemTriggers() {
        RecipeUnlockIndex index = new RecipeUnlockIndex();
        index.index(recipe("invalid", RecipeUnlockingRequirement.INVALID));
        index.index(recipe("always", new RecipeUnlockingRequirement(RecipeUnlockingContext.ALWAYS_UNLOCKED)));
        index.index(new MultiRecipe(UUID.randomUUID(), 1));

        Assertions.assertTrue(index.getCandidates(planks.getId()).isEmpty());
        Assertions.assertTrue(index.getCandidates(diamond.getId()).isEmpty());
    }

    @Test
    void indexesSmeltingRecipesByInput() {
        RecipeUnlockIndex index = new RecipeUnlockIndex();
        index.index(new FurnaceRecipe(Item.get(ItemID.IRON_INGOT), diamond.clone()));

        Assertions.assertEquals(1, index.getCandidates(diamond.getId()).size());
    }

    @Test
    void clearDropsEverything() {
        RecipeUnlockIndex index = new RecipeUnlockIndex();
        index.index(recipe("direct", triggeredBy(new DefaultDescriptor(diamond))));
        index.clear();

        Assertions.assertTrue(index.getCandidates(diamond.getId()).isEmpty());
    }
}
