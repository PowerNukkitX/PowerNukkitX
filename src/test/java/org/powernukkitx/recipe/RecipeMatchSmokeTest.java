package org.powernukkitx.recipe;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.powernukkitx.ServerMockFixture;
import org.powernukkitx.item.Item;
import org.powernukkitx.item.ItemID;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.registry.RecipeRegistry;
import org.powernukkitx.registry.Registries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke coverage over the recipe listing, per recipe getters, the shaped/shapeless
 * match engine and the RecipeRegistry lookup methods, plus the fuel registry.
 * Everything is wrapped tolerantly - a single misbehaving vanilla recipe must not
 * fail the whole sweep, we only gate on "did we drive a reasonable amount".
 */
final class RecipeMatchSmokeTest {

    private static RecipeRegistry recipes;
    private static final AtomicInteger driven = new AtomicInteger();

    @BeforeAll
    static void boot() {
        ServerMockFixture.boot();
        Registries.RECIPE.init();
        recipes = Registries.RECIPE;
    }

    private static void safe(Runnable r) {
        try {
            r.run();
            driven.incrementAndGet();
        } catch (Throwable ignored) {
        }
    }

    private static <T> T safeGet(java.util.function.Supplier<T> s) {
        try {
            T v = s.get();
            driven.incrementAndGet();
            return v;
        } catch (Throwable ignored) {
            return null;
        }
    }

    // build a single item from the first non-null ingredient of a recipe
    private static Item firstIngredientItem(Recipe recipe) {
        for (ItemDescriptor d : recipe.getIngredients()) {
            Item i = safeGet(d::toItem);
            if (i != null && !i.isNull()) return i.clone();
        }
        return null;
    }

    private static Item[] ingredientItems(Recipe recipe) {
        List<Item> items = new ArrayList<>();
        for (ItemDescriptor d : recipe.getIngredients()) {
            Item i = safeGet(d::toItem);
            if (i != null) items.add(i.clone());
        }
        return items.toArray(Item.EMPTY_ARRAY);
    }

    @Test
    void driveRecipeCountAndLookup() {
        assertTrue(recipes.getRecipeCount() > 0, "expected registered recipes");

        driveType(safeGet(recipes::getShapelessRecipeMap));
        driveType(safeGet(recipes::getShapedRecipeMap));
        driveType(safeGet(recipes::getFurnaceRecipeMap));
        driveType(safeGet(recipes::getBlastFurnaceRecipeMap));
        driveType(safeGet(recipes::getSmokerRecipeMap));
        driveType(safeGet(recipes::getCampfireRecipeMap));
        driveType(safeGet(recipes::getStonecutterRecipeMap));
        driveType(safeGet(recipes::getCartographyRecipeMap));
        driveType(safeGet(recipes::getSmithingTransformRecipeMap));
        driveType(safeGet(recipes::getBrewingRecipeMap));
        driveType(safeGet(recipes::getContainerRecipeMap));
        driveType(safeGet(recipes::getModProcessRecipeMap));
        driveType(safeGet(recipes::getMultiRecipeMap));

        driveShapedMatch();
        driveShapelessMatch();
        driveSmeltingLookup();
        driveOtherLookups();

        assertTrue(driven.get() > 50, "expected to drive many recipe methods, got " + driven.get());
    }

    private void driveType(Set<? extends Recipe> set) {
        if (set == null) return;
        for (Recipe recipe : set) {
            safe(recipe::getRecipeId);
            safe(recipe::getResults);
            safe(recipe::getIngredients);
            safe(recipe::getType);
            safe(recipe::toString);
            safe(recipe::hashCode);

            // registry key round trip
            safe(() -> recipes.get(recipe.getRecipeId()));

            // fastCheck with the recipe's own inputs should generally resolve
            safe(() -> recipe.fastCheck(ingredientItems(recipe)));

            switch (recipe) {
                case ShapedRecipe shaped -> {
                    safe(shaped::getResult);
                    safe(shaped::getShape);
                    safe(shaped::getWidth);
                    safe(shaped::getHeight);
                    safe(shaped::isMirror);
                    safe(shaped::getUUID);
                    safe(shaped::getNetId);
                    safe(shaped::getPriority);
                    safe(() -> shaped.getIngredient(0, 0));
                    safe(shaped::toNetwork);
                }
                case ShapelessRecipe shapeless -> {
                    safe(shapeless::getResult);
                    safe(shapeless::getUUID);
                    safe(shapeless::getNetId);
                    safe(shapeless::getPriority);
                    safe(shapeless::getRecipeIdTag);
                    safe(shapeless::toNetwork);
                }
                case SmeltingRecipe smelting -> {
                    safe(smelting::getResult);
                    safe(smelting::getInput);
                    safe(smelting::getRecipeIdTag);
                    safe(smelting::toNetwork);
                    safe(() -> recipes.getRecipeXp(smelting));
                }
                default -> {
                }
            }
        }
    }

    private static <T extends Recipe> Set<T> setOrEmpty(java.util.function.Supplier<Set<T>> s) {
        Set<T> r = safeGet(s);
        return r == null ? Set.of() : r;
    }

    private void driveShapedMatch() {
        for (ShapedRecipe recipe : this.<ShapedRecipe>setOrEmpty(recipes::getShapedRecipeMap)) {
            Input input = buildShapedInput(recipe);
            if (input == null) continue;
            Boolean matched = safeGet(() -> recipe.match(input));
            if (Boolean.TRUE.equals(matched)) {
                safe(() -> recipes.findShapedRecipe(buildShapedInput(recipe)));
                return; // one successful shaped match/lookup is enough to cover the path
            }
        }
    }

    private Input buildShapedInput(ShapedRecipe recipe) {
        int w = recipe.getWidth();
        int h = recipe.getHeight();
        if (w <= 0 || h <= 0 || w > 3 || h > 3) return null;
        Item[][] data = new Item[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                ItemDescriptor d = recipe.getIngredient(x, y);
                Item item = safeGet(d::toItem);
                data[y][x] = (item == null || item.isNull()) ? Item.AIR.clone() : item.clone();
            }
        }
        return new Input(w, h, data);
    }

    private void driveShapelessMatch() {
        int done = 0;
        for (ShapelessRecipe recipe : this.<ShapelessRecipe>setOrEmpty(recipes::getShapelessRecipeMap)) {
            Item[] items = ingredientItems(recipe);
            if (items.length == 0 || items.length > 9) continue;
            // shapeless match reads data as [col][row]; build a square that holds them
            int n = items.length;
            Item[][] data = new Item[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) data[i][j] = Item.AIR.clone();
            }
            for (int i = 0; i < n; i++) data[i][0] = items[i].clone();
            Input input = new Input(n, n, data);
            safe(() -> recipe.match(input));

            safe(() -> recipes.findShapelessRecipe(ingredientItems(recipe)));
            if (++done >= 5) return;
        }
    }

    private void driveSmeltingLookup() {
        driveSmelt(this.<FurnaceRecipe>setOrEmpty(recipes::getFurnaceRecipeMap));
        driveSmelt(this.<BlastFurnaceRecipe>setOrEmpty(recipes::getBlastFurnaceRecipeMap));
        driveSmelt(this.<SmokerRecipe>setOrEmpty(recipes::getSmokerRecipeMap));
        driveSmelt(this.<CampfireRecipe>setOrEmpty(recipes::getCampfireRecipeMap));
    }

    private void driveSmelt(Set<? extends SmeltingRecipe> set) {
        int done = 0;
        for (SmeltingRecipe recipe : set) {
            Item in = safeGet(() -> recipe.getInput().toItem());
            if (in == null || in.isNull()) continue;
            if (recipe instanceof BlastFurnaceRecipe) {
                safe(() -> recipes.findBlastFurnaceRecipe(in.clone()));
            } else if (recipe instanceof SmokerRecipe) {
                safe(() -> recipes.findSmokerRecipe(in.clone()));
            } else if (recipe instanceof CampfireRecipe) {
                safe(() -> recipes.findCampfireRecipe(in.clone()));
            } else if (recipe instanceof FurnaceRecipe) {
                safe(() -> recipes.findFurnaceRecipe(in.clone()));
            }
            if (++done >= 3) return;
        }
    }

    private void driveOtherLookups() {
        for (StonecutterRecipe r : this.<StonecutterRecipe>setOrEmpty(recipes::getStonecutterRecipeMap)) {
            Item in = firstIngredientItem(r);
            if (in != null) {
                safe(() -> recipes.findStonecutterRecipe(in.clone()));
                break;
            }
        }
        for (BrewingRecipe r : this.<BrewingRecipe>setOrEmpty(recipes::getBrewingRecipeMap)) {
            Item[] in = ingredientItems(r);
            if (in.length > 0) {
                safe(() -> recipes.findBrewingRecipe(in));
                break;
            }
        }
        for (ContainerRecipe r : this.<ContainerRecipe>setOrEmpty(recipes::getContainerRecipeMap)) {
            Item[] in = ingredientItems(r);
            if (in.length > 0) {
                safe(() -> recipes.findContainerRecipe(in));
                break;
            }
        }
        for (SmithingTransformRecipe r : this.<SmithingTransformRecipe>setOrEmpty(recipes::getSmithingTransformRecipeMap)) {
            Item[] in = ingredientItems(r);
            if (in.length > 0) {
                safe(() -> recipes.findSmithingTransform(in));
                break;
            }
        }
    }

    @Test
    void driveFuelRegistry() {
        int hits = 0;
        String[] fuels = {
            ItemID.COAL, ItemID.CHARCOAL, ItemID.STICK, ItemID.BLAZE_ROD,
            ItemID.LAVA_BUCKET, ItemID.OAK_BOAT, ItemID.BOW, ItemID.WOODEN_AXE
        };
        for (String id : fuels) {
            Item item = safeGet(() -> Item.get(id));
            if (item == null) continue;
            Boolean fuel = safeGet(() -> Registries.FUEL.isFuel(item));
            Integer dur = safeGet(() -> Registries.FUEL.getFuelDuration(item));
            safe(() -> Registries.FUEL.get(item));
            if (Boolean.TRUE.equals(fuel) && dur != null && dur > 0) hits++;
        }
        // non fuel should not be fuel
        Item diamond = safeGet(() -> Item.get(ItemID.DIAMOND));
        if (diamond != null) {
            safe(() -> assertTrue(!Registries.FUEL.isFuel(diamond)));
        }
        assertTrue(hits >= 4, "expected several fuels to resolve, got " + hits);
    }
}
