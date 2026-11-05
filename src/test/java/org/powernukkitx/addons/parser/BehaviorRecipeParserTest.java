package org.powernukkitx.addons.parser;

import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.addons.AddonFeatureTracker;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.recipe.FurnaceRecipe;
import org.powernukkitx.recipe.ContainerRecipe;
import org.powernukkitx.recipe.CraftingRecipe;
import org.powernukkitx.recipe.SmithingTransformRecipe;
import org.powernukkitx.recipe.Recipe;
import org.powernukkitx.recipe.ShapedRecipe;
import org.powernukkitx.recipe.StonecutterRecipe;
import org.powernukkitx.registry.Registries;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers parsing behaviour pack recipe documents into the recipe registry. Every case uses its own
 * identifier, because the registry is process-wide and a registration cannot be undone.
 */
class BehaviorRecipeParserTest {

    private AddonFeatureTracker tracker;
    private BehaviorRecipeParser parser;

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
    }

    @BeforeEach
    void setUp() {
        tracker = new AddonFeatureTracker();
        parser = new BehaviorRecipeParser(tracker);
    }

    private int parse(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        return parser.parseAndRegister("TestPack", "recipe.json", root);
    }

    @Test
    void registersShapedRecipeOncePerTag() {
        assertEquals(2, parse("""
                { "format_version": "1.12", "minecraft:recipe_shaped": {
                  "description": { "identifier": "pnxtest:two_stations" },
                  "tags": [ "crafting_table", "pnxtest_workbench" ],
                  "pattern": [ "SS", "S" ],
                  "key": { "S": { "item": "minecraft:stick" }, "X": { "item": "minecraft:diamond" } },
                  "result": { "item": "minecraft:diamond", "count": 2 } } }
                """));

        ShapedRecipe table = assertInstanceOf(ShapedRecipe.class, Registries.RECIPE.get("pnxtest:two_stations"));
        ShapedRecipe custom = assertInstanceOf(ShapedRecipe.class, Registries.RECIPE.get("pnxtest:two_stations#pnxtest_workbench"));
        assertEquals("crafting_table", table.getCraftingTag());
        assertEquals("pnxtest_workbench", custom.getCraftingTag());
        assertArrayEquals(new String[]{"SS", "S "}, table.getShape(), "short rows must be padded to the widest one");
        assertTrue(table.isMirror(), "assume_symmetry defaults to true");
        assertEquals(2, table.getResults().getFirst().getCount());
        assertNotEquals(table.getNetId(), custom.getNetId());
        assertSame(table, Registries.RECIPE.getRecipeByNetworkId(table.getNetId()));
        assertTrue(tracker.isEmpty(), "a key symbol the pattern does not use is ignored, not rejected");
    }

    @Test
    void replacesVanillaRecipeOfTheSameIdentifier() {
        Registries.RECIPE.init();
        Recipe vanilla = Registries.RECIPE.get("minecraft:jukebox_from_crimson_planks");
        assertNotNull(vanilla, "the vanilla jukebox recipe is registered");

        assertEquals(1, parse("""
                { "format_version": "1.12", "minecraft:recipe_shapeless": {
                  "description": { "identifier": "minecraft:jukebox_from_crimson_planks" },
                  "tags": [ "crafting_table" ],
                  "ingredients": [ { "item": "minecraft:dirt" } ],
                  "result": { "item": "minecraft:jukebox" } } }
                """));

        CraftingRecipe replaced = assertInstanceOf(CraftingRecipe.class, Registries.RECIPE.get("minecraft:jukebox_from_crimson_planks"));
        assertNotSame(vanilla, replaced);
        assertEquals(1, replaced.getIngredients().size());
        assertNull(Registries.RECIPE.getRecipeByNetworkId(((CraftingRecipe) vanilla).getNetId()),
                "the vanilla recipe is gone, not kept next to the pack's");
        assertTrue(tracker.isEmpty());
    }

    @Test
    void registersStonecutterAndFurnaceRecipes() {
        assertEquals(1, parse("""
                { "minecraft:recipe_shapeless": {
                  "description": { "identifier": "pnxtest:cut_stick" },
                  "tags": [ "stonecutter" ],
                  "ingredients": [ { "item": "minecraft:diamond", "data": 0 } ],
                  "result": { "item": "minecraft:stick", "count": 4 } } }
                """));
        assertEquals(1, parse("""
                { "minecraft:recipe_furnace": {
                  "description": { "identifier": "pnxtest:smelt_stick" },
                  "tags": [ "furnace" ],
                  "input": "minecraft:diamond_hoe",
                  "output": "minecraft:stick" } }
                """));

        assertInstanceOf(StonecutterRecipe.class, Registries.RECIPE.get("pnxtest:cut_stick"));
        FurnaceRecipe furnace = assertInstanceOf(FurnaceRecipe.class, Registries.RECIPE.get("pnxtest:smelt_stick"));
        assertEquals(ItemID.STICK, furnace.getResult().getId());
        assertSame(furnace, Registries.RECIPE.findFurnaceRecipe(Item.get(ItemID.DIAMOND_HOE)));
    }

    @Test
    void skipsRecipeWithUnknownItem() {
        assertEquals(0, parse("""
                { "minecraft:recipe_shaped": {
                  "description": { "identifier": "pnxtest:needs_missing_item" },
                  "tags": [ "crafting_table" ],
                  "pattern": [ "M" ],
                  "key": { "M": { "item": "pnxtest:not_registered" } },
                  "result": { "item": "minecraft:stick" } } }
                """));

        Recipe recipe = Registries.RECIPE.get("pnxtest:needs_missing_item");
        assertNull(recipe);
        assertFalse(tracker.isEmpty());
    }

    @Test
    void skipsDuplicateIdentifierAndUnsupportedType() {
        String recipe = """
                { "minecraft:recipe_shapeless": {
                  "description": { "identifier": "pnxtest:duplicate" },
                  "tags": [ "crafting_table" ],
                  "ingredients": [ { "item": "minecraft:stick" }, { "item": "minecraft:stick" } ],
                  "result": "minecraft:diamond" } }
                """;

        assertEquals(1, parse(recipe));
        assertTrue(tracker.isEmpty());
        assertEquals(0, parse(recipe));
        assertFalse(tracker.isEmpty());

        setUp();
        assertEquals(0, parse("""
                { "minecraft:recipe_smithing_transform": { "description": { "identifier": "pnxtest:smithing" } } }
                """));
        assertFalse(tracker.isEmpty());
    }

    @Test
    void registersSmithingAndBrewingRecipes() {
        assertEquals(1, parse("""
                { "minecraft:recipe_smithing_transform": {
                  "description": { "identifier": "pnxtest:smith_stick" },
                  "tags": [ "smithing_table" ],
                  "template": "minecraft:netherite_upgrade_smithing_template",
                  "base": "minecraft:diamond_hoe", "addition": "minecraft:stick", "result": "minecraft:diamond" } }
                """));
        assertEquals(1, parse("""
                { "minecraft:recipe_brewing_container": {
                  "description": { "identifier": "pnxtest:brew_stick" },
                  "tags": [ "brewing_stand" ],
                  "input": "minecraft:diamond", "reagent": "minecraft:stick", "output": "minecraft:diamond_hoe" } }
                """));

        assertInstanceOf(SmithingTransformRecipe.class, Registries.RECIPE.get("pnxtest:smith_stick"));
        assertInstanceOf(ContainerRecipe.class, Registries.RECIPE.get("pnxtest:brew_stick"));
        assertTrue(tracker.isEmpty());
    }

    @Test
    void readsUnlockContext() {
        assertEquals(1, parse("""
                { "minecraft:recipe_shapeless": {
                  "description": { "identifier": "pnxtest:unlocked_in_water" },
                  "tags": [ "crafting_table" ],
                  "unlock": { "context": "PlayerInWater" },
                  "ingredients": [ { "item": "minecraft:diamond" } ],
                  "result": "minecraft:diamond_hoe" } }
                """));

        CraftingRecipe recipe = assertInstanceOf(CraftingRecipe.class, Registries.RECIPE.get("pnxtest:unlocked_in_water"));
        assertEquals(RecipeUnlockingContext.PLAYER_IN_WATER, recipe.getRequirement().getUnlockingContext());
    }
}
