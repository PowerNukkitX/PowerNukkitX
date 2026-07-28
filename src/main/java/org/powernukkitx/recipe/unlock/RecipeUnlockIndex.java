package org.powernukkitx.recipe.unlock;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.powernukkitx.recipe.CraftingRecipe;
import org.powernukkitx.recipe.Recipe;
import org.powernukkitx.recipe.SmeltingRecipe;
import org.powernukkitx.recipe.SmithingTransformRecipe;
import org.powernukkitx.recipe.SmithingTrimRecipe;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.recipe.descriptor.ItemTagDescriptor;
import org.powernukkitx.tags.ItemTags;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Reverse lookup from an item to the recipes it may unlock.
 * <p>
 * Only recipes that the client shows in its recipe book are indexed, since only those can be
 * addressed by {@link org.cloudburstmc.protocol.bedrock.packet.UnlockedRecipesPacket}. The index
 * narrows the candidate set for a newly obtained item; whether a candidate is actually unlockable
 * is decided by matching its ingredients against the player inventory.
 * <p>
 * Populated from {@link org.powernukkitx.registry.RecipeRegistry#register(String, Recipe)} on the
 * thread that registers recipes and read from the player handling code afterwards; it is not
 * safe to register recipes concurrently with lookups.
 */
public final class RecipeUnlockIndex {
    private final Map<String, Set<String>> recipesByItemId = new Object2ObjectOpenHashMap<>();
    private final Map<String, Set<String>> recipesByItemTag = new Object2ObjectOpenHashMap<>();

    /**
     * Indexes a recipe by every ingredient it can be identified through. Recipes that never show up
     * in the recipe book, and ingredient descriptors that cannot be resolved to a concrete item or
     * tag, are ignored.
     *
     * @param recipe the recipe to index
     */
    public void index(@NotNull Recipe recipe) {
        if (!isBookVisible(recipe)) {
            return;
        }
        final String recipeId = recipe.getRecipeId();
        for (ItemDescriptor ingredient : recipe.getIngredients()) {
            switch (ingredient) {
                case DefaultDescriptor descriptor ->
                    recipesByItemId.computeIfAbsent(descriptor.getItem().getId(), id -> new ObjectOpenHashSet<>()).add(recipeId);
                case ItemTagDescriptor descriptor ->
                    recipesByItemTag.computeIfAbsent(descriptor.getItemTag(), tag -> new ObjectOpenHashSet<>()).add(recipeId);
                default -> {
                }
            }
        }
    }

    /**
     * Recipe ids that use the given item as an ingredient, either directly or through one of its
     * item tags.
     *
     * @param itemId the identifier of the obtained item
     * @return the candidate recipe ids, empty when the item is not an ingredient of anything
     */
    public @NotNull @UnmodifiableView Set<String> getCandidates(@NotNull String itemId) {
        final Set<String> direct = recipesByItemId.get(itemId);
        final Set<String> tags = ItemTags.getTagSet(itemId);
        Set<String> merged = null;
        for (String tag : tags) {
            final Set<String> tagged = recipesByItemTag.get(tag);
            if (tagged == null) {
                continue;
            }
            if (merged == null) {
                merged = new ObjectOpenHashSet<>(tagged.size() + (direct == null ? 0 : direct.size()));
                if (direct != null) {
                    merged.addAll(direct);
                }
            }
            merged.addAll(tagged);
        }
        if (merged != null) {
            return Collections.unmodifiableSet(merged);
        }
        return direct == null ? Set.of() : Collections.unmodifiableSet(direct);
    }

    public void clear() {
        recipesByItemId.clear();
        recipesByItemTag.clear();
    }

    private static boolean isBookVisible(Recipe recipe) {
        return recipe instanceof CraftingRecipe
            || recipe instanceof SmeltingRecipe
            || recipe instanceof SmithingTransformRecipe
            || recipe instanceof SmithingTrimRecipe;
    }
}
