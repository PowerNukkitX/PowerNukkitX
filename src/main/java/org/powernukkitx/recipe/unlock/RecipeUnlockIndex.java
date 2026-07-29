package org.powernukkitx.recipe.unlock;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.ItemTagDescriptor;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.NameDescriptor;
import org.cloudburstmc.protocol.bedrock.data.inventory.descriptor.RecipeIngredient;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingContext;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingRequirement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.powernukkitx.recipe.CraftingRecipe;
import org.powernukkitx.recipe.Recipe;
import org.powernukkitx.recipe.SmeltingRecipe;
import org.powernukkitx.tags.ItemTags;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Reverse lookup from an item to the recipes that item unlocks.
 * <p>
 * The index follows the vanilla unlock triggers carried in each recipe's
 * {@link RecipeUnlockingRequirement}: a recipe becomes a candidate the moment the player obtains
 * one of the trigger items, which is not necessarily the full ingredient list. Wooden tools for
 * example unlock through a single stick. Recipes without unlock data, or recipes unlocked through
 * a special context such as {@code ALWAYS_UNLOCKED} or {@code PLAYER_IN_WATER}, carry no item
 * trigger and are therefore not indexed - the client resolves those contexts on its own.
 * <p>
 * Populated from {@link org.powernukkitx.registry.RecipeRegistry#register(String, Recipe)} on the
 * thread that registers recipes and read from the player handling code afterwards; it is not
 * safe to register recipes concurrently with lookups.
 */
public final class RecipeUnlockIndex {
    private final Map<String, Set<String>> recipesByItemId = new Object2ObjectOpenHashMap<>();
    private final Map<String, Set<String>> recipesByItemTag = new Object2ObjectOpenHashMap<>();

    /**
     * Indexes a recipe by its unlock trigger items. Recipes without an item based unlock
     * requirement are ignored.
     *
     * @param recipe the recipe to index
     */
    public void index(@NotNull Recipe recipe) {
        switch (recipe) {
            case CraftingRecipe crafting -> index(crafting.getRecipeId(), crafting.getRequirement());
            case SmeltingRecipe smelting -> index(smelting.getRecipeId(), smelting.getUnlockingRequirement());
            default -> {
            }
        }
    }

    private void index(String recipeId, RecipeUnlockingRequirement requirement) {
        if (requirement == null || requirement.getUnlockingContext() != RecipeUnlockingContext.NONE) {
            return;
        }
        for (RecipeIngredient trigger : requirement.getUnlockingIngredients()) {
            switch (trigger.getDescriptor()) {
                case NameDescriptor descriptor ->
                    recipesByItemId.computeIfAbsent(descriptor.getItemId().getIdentifier(), id -> new ObjectOpenHashSet<>()).add(recipeId);
                case ItemTagDescriptor descriptor ->
                    recipesByItemTag.computeIfAbsent(descriptor.getItemTag(), tag -> new ObjectOpenHashSet<>()).add(recipeId);
                default -> {
                }
            }
        }
    }

    /**
     * Recipe ids unlocked by obtaining the given item, either directly or through one of its
     * item tags.
     *
     * @param itemId the identifier of the obtained item
     * @return the unlocked recipe ids, empty when the item unlocks nothing
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
}
