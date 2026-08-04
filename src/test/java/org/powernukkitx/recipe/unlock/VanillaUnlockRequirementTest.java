package org.powernukkitx.recipe.unlock;

import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingContext;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingRequirement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.block.BlockID;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.recipe.CraftingRecipe;
import org.powernukkitx.recipe.ShapedRecipe;
import org.powernukkitx.recipe.SmeltingRecipe;
import org.powernukkitx.registry.Registries;

import java.util.Set;

class VanillaUnlockRequirementTest {

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        Registries.RECIPE.init();
    }

    @Test
    void woodenSwordUnlocksThroughStick() {
        CraftingRecipe sword = (CraftingRecipe) Registries.RECIPE.get("minecraft:wooden_sword");
        Assertions.assertNotNull(sword);

        RecipeUnlockingRequirement requirement = sword.getRequirement();
        Assertions.assertEquals(RecipeUnlockingContext.NONE, requirement.getUnlockingContext());
        Assertions.assertFalse(requirement.getUnlockingIngredients().isEmpty());

        Set<String> unlockedByStick = Registries.RECIPE.getUnlockIndex().getCandidates(ItemID.STICK);
        Assertions.assertTrue(unlockedByStick.contains("minecraft:wooden_sword"));
    }

    @Test
    void planksUnlockStickCraftingButNotTheSword() {
        Set<String> unlockedByPlanks = Registries.RECIPE.getUnlockIndex().getCandidates(BlockID.OAK_PLANKS);
        Assertions.assertTrue(unlockedByPlanks.contains("minecraft:stick"));
        Assertions.assertFalse(unlockedByPlanks.contains("minecraft:wooden_sword"));
    }

    @Test
    void breadUnlocksThroughWheat() {
        Assertions.assertTrue(Registries.RECIPE.getUnlockIndex().getCandidates("minecraft:wheat").contains("minecraft:bread"));
    }

    @Test
    void contextRequirementsSurviveParsing() {
        CraftingRecipe workbench = (CraftingRecipe) Registries.RECIPE.get("minecraft:WorkBench_recipeId");
        Assertions.assertNotNull(workbench);
        Assertions.assertEquals(RecipeUnlockingContext.ALWAYS_UNLOCKED, workbench.getRequirement().getUnlockingContext());

        CraftingRecipe boat = (CraftingRecipe) Registries.RECIPE.get("minecraft:boat");
        Assertions.assertNotNull(boat);
        Assertions.assertEquals(RecipeUnlockingContext.PLAYER_IN_WATER, boat.getRequirement().getUnlockingContext());
    }

    @Test
    void requirementsReachTheNetworkPayload() {
        ShapedRecipe sword = (ShapedRecipe) Registries.RECIPE.get("minecraft:wooden_sword");
        RecipeUnlockingRequirement payloadRequirement = sword.toNetwork().getUnlockingRequirement();
        Assertions.assertNotNull(payloadRequirement);
        Assertions.assertFalse(payloadRequirement.isInvalid());
    }

    @Test
    void smeltingRecipesUnlockThroughTheirInput() {
        Assertions.assertFalse(Registries.RECIPE.getUnlockIndex().getCandidates("minecraft:deepslate_iron_ore").isEmpty());
    }

    @Test
    void spongeDryingUnlocksThroughTheDrySponge() {
        // the one smelting recipe whose vanilla trigger is not its input: wet sponge drying unlocks via dry sponge
        Assertions.assertTrue(Registries.RECIPE.getUnlockIndex().getCandidates("minecraft:sponge").stream()
            .map(Registries.RECIPE::get)
            .anyMatch(recipe -> recipe instanceof SmeltingRecipe smelting
                && smelting.getResult().getId().equals("minecraft:sponge")));
    }
}
