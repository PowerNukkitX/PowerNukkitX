package org.powernukkitx.recipe;

import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeNetId;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingContext;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingRequirement;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.ShapelessRecipePayload;
import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.registry.RecipeRegistry;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class ShapelessRecipe extends CraftingRecipe {
    public ShapelessRecipe(Item result, int netId, Collection<Item> ingredients) {
        this(null, netId, 10, result, ingredients);
    }

    public ShapelessRecipe(String recipeId, int netId, int priority, Item result, Collection<Item> ingredients) {
        this(recipeId, netId, priority, result, ingredients.stream().map(item -> (ItemDescriptor) new DefaultDescriptor(item)).toList());
    }

    public ShapelessRecipe(String recipeId, int netId, int priority, Item result, List<ItemDescriptor> ingredients) {
        this(recipeId, null, netId, priority, result, ingredients);
    }

    public ShapelessRecipe(String recipeId, UUID uuid, int netId, int priority, Item result, List<ItemDescriptor> ingredients) {
        this(recipeId, uuid, netId, priority, result, ingredients, null);
    }

    public ShapelessRecipe(String recipeId, UUID uuid, int netId, int priority, Item result, List<ItemDescriptor> ingredients, RecipeUnlockingRequirement recipeUnlockingRequirement) {
        super(recipeId == null ? RecipeRegistry.computeRecipeId(List.of(result), ingredients, RecipeType.SHAPELESS) : recipeId, netId, priority, recipeUnlockingRequirement);
        this.uuid = uuid;
        this.results.add(result.clone());
        if (ingredients.size() > 9) {
            throw new IllegalArgumentException("Shapeless recipes cannot have more than 9 ingredients");
        }
        this.ingredients.addAll(ingredients);
    }

    public Item getResult() {
        return results.get(0);
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPELESS;
    }

    @Override
    public boolean match(Input input) {
        Item[][] data = input.getData();
        List<ItemDescriptor> remainingIngredients = new ArrayList<>(ingredients);

        for (int row = 0; row < input.getRow(); row++) {
            for (int col = 0; col < input.getCol(); col++) {
                Item inputItem = data[row][col];

                if (inputItem.isNull()) {
                    continue;
                }

                boolean matched = false;

                for (int i = 0; i < remainingIngredients.size(); i++) {
                    ItemDescriptor ingredient = remainingIngredients.get(i);

                    if (ingredient.match(inputItem) && inputItem.getCount() >= ingredient.getCount()) {
                        remainingIngredients.remove(i);
                        matched = true;
                        break;
                    }
                }

                if (!matched) {
                    return false;
                }
            }
        }

        return remainingIngredients.isEmpty();
    }

    public ShapelessRecipePayload toNetwork() {
        final ShapelessRecipePayload payload = new ShapelessRecipePayload();
        payload.setRecipeId(this.getRecipeId());
        payload.getIngredients().addAll(this.getIngredients().stream().map(ItemDescriptor::toNetwork).toList());
        payload.getResults().addAll(this.getResults().stream().map(Item::toRecipeNetwork).toList());
        payload.setUuid(this.getUUID());
        payload.setTag(this.getRecipeIdTag());
        payload.setPriority(this.getPriority());
        payload.setUnlockingRequirement(this.getRequirement());
        payload.setNetId(new RecipeNetId(this.getNetId()));
        return payload;
    }

    public String getRecipeIdTag() {
        return this.getCraftingTag();
    }

    /**
     * Constructs a shapeless recipe from a builder configuration.
     *
     * @param builder the recipe builder
     */
    protected ShapelessRecipe(Builder builder) {
        this(
                validateBuilder(builder).recipeId,
                builder.uuid,
                builder.netId,
                builder.priority,
                builder.result,
                builder.ingredients,
                builder.unlockingRequirement == null
                        ? new RecipeUnlockingRequirement(RecipeUnlockingContext.ALWAYS_UNLOCKED)
                        : builder.unlockingRequirement
        );

        this.setCraftingTag(builder.craftingTag);
    }

    private static Builder validateBuilder(Builder builder) {
        return builder.validate();
    }

    /**
     * Creates a new builder for a shapeless recipe.
     *
     * @return a new shapeless recipe builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating {@link ShapelessRecipe} instances using a fluent API.
     * <p>
     * Existing {@link ShapelessRecipe} constructors remain available and are not
     * affected by this builder.
     */
    public static final class Builder {
        private String recipeId;
        private UUID uuid;
        private int netId;
        private boolean netIdSet;
        private int priority;
        private Item result;
        private final List<ItemDescriptor> ingredients = new ArrayList<>();
        private String craftingTag = "crafting_table";
        private RecipeUnlockingRequirement unlockingRequirement;

        private Builder() {
        }

        /**
         * Sets the recipe identifier.
         *
         * @param recipeId the recipe identifier
         * @return this builder
         */
        public Builder id(String recipeId) {
            this.recipeId = recipeId;
            return this;
        }

        /**
         * Sets the UUID used by the recipe on the network.
         * <p>
         * When not specified, the recipe registry assigns one during registration.
         *
         * @param uuid the recipe UUID
         * @return this builder
         */
        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        /**
         * Sets the network ID of the recipe.
         *
         * @param netId the recipe network ID
         * @return this builder
         */
        public Builder netId(int netId) {
            this.netId = netId;
            this.netIdSet = true;
            return this;
        }

        /**
         * Sets the priority of the recipe.
         * <p>
         * Lower values have a higher matching priority.
         *
         * @param priority the recipe priority
         * @return this builder
         */
        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Sets the result produced by the recipe.
         *
         * @param result the recipe result
         * @return this builder
         */
        public Builder result(Item result) {
            this.result = result;
            return this;
        }

        /**
         * Adds an item as an ingredient of the recipe.
         *
         * @param item the ingredient
         * @return this builder
         */
        public Builder ingredient(Item item) {
            this.ingredients.add(new DefaultDescriptor(item));
            return this;
        }

        /**
         * Adds an item descriptor as an ingredient of the recipe.
         *
         * @param descriptor the ingredient descriptor
         * @return this builder
         */
        public Builder ingredient(ItemDescriptor descriptor) {
            this.ingredients.add(descriptor);
            return this;
        }

        /**
         * Adds multiple items as ingredients of the recipe.
         *
         * @param items the ingredients
         * @return this builder
         */
        public Builder ingredients(Item... items) {
            for (Item item : items) {
                ingredient(item);
            }
            return this;
        }

        /**
         * Sets the crafting tag used to associate this recipe with a crafting table.
         * <p>
         * The default value is {@code crafting_table}.
         *
         * @param craftingTag the crafting table tag
         * @return this builder
         */
        public Builder craftingTag(String craftingTag) {
            Preconditions.checkArgument(
                    craftingTag != null && !craftingTag.isBlank() && craftingTag.length() <= 64,
                    "Crafting tag cannot be null, blank, or exceed 64 characters"
            );

            this.craftingTag = craftingTag;
            return this;
        }

        /**
         * Adds an item that can trigger discovery of this recipe.
         * <p>
         * Calling this method uses {@link RecipeUnlockingContext#NONE} and cannot
         * be combined with a contextual unlocking rule.
         *
         * @param item the item that unlocks the recipe
         * @return this builder
         */
        public Builder unlockBy(Item item) {
            return unlockBy(new DefaultDescriptor(item));
        }

        /**
         * Adds an item descriptor that can trigger discovery of this recipe.
         * <p>
         * Multiple calls may be used to register multiple unlocking ingredients.
         * This cannot be combined with a contextual unlocking rule.
         *
         * @param descriptor the descriptor that unlocks the recipe
         * @return this builder
         */
        public Builder unlockBy(ItemDescriptor descriptor) {
            if (this.unlockingRequirement == null) {
                this.unlockingRequirement = new RecipeUnlockingRequirement(
                        RecipeUnlockingContext.NONE
                );
            }

            Preconditions.checkState(
                    this.unlockingRequirement.getUnlockingContext() == RecipeUnlockingContext.NONE,
                    "Unlocking ingredients cannot be combined with unlocking context " + this.unlockingRequirement.getUnlockingContext()
            );

            this.unlockingRequirement.getUnlockingIngredients().add(
                    descriptor.toNetwork()
            );

            return this;
        }

        /**
         * Configures this recipe to always be unlocked.
         *
         * @return this builder
         */
        public Builder alwaysUnlocked() {
            return unlockingContext(RecipeUnlockingContext.ALWAYS_UNLOCKED);
        }

        /**
         * Sets a contextual condition used to unlock this recipe, such as
         * {@link RecipeUnlockingContext#PLAYER_IN_WATER}.
         * <p>
         * {@link RecipeUnlockingContext#NONE} is reserved for ingredient-based
         * unlocking and should be configured using {@link #unlockBy(Item)}.
         *
         * @param context the recipe unlocking context
         * @return this builder
         */
        public Builder unlockingContext(RecipeUnlockingContext context) {
            Preconditions.checkArgument(
                    context != null,
                    "Unlocking context cannot be null"
            );

            Preconditions.checkArgument(
                    context != RecipeUnlockingContext.NONE,
                    "Use unlockBy(...) for RecipeUnlockingContext.NONE"
            );

            Preconditions.checkState(
                    this.unlockingRequirement == null || this.unlockingRequirement.getUnlockingIngredients().isEmpty(),
                    "Unlocking context cannot be combined with unlocking ingredients"
            );

            this.unlockingRequirement = new RecipeUnlockingRequirement(context);
            return this;
        }

        private Builder validate() {
            Preconditions.checkState(
                    this.netIdSet,
                    "Network ID must be specified"
            );

            Preconditions.checkState(
                    this.result != null,
                    "Result must be specified"
            );

            Preconditions.checkState(
                    !this.ingredients.isEmpty(),
                    "At least one ingredient must be specified"
            );

            return this;
        }

        /**
         * Builds the configured shapeless recipe.
         *
         * @return the created shapeless recipe
         * @throws IllegalStateException if the network ID, result, or ingredients have not been specified
         */
        public ShapelessRecipe build() {
            this.validate();

            ShapelessRecipe recipe = new ShapelessRecipe(
                    this.recipeId,
                    this.uuid,
                    this.netId,
                    this.priority,
                    this.result,
                    this.ingredients,
                    this.unlockingRequirement == null
                            ? new RecipeUnlockingRequirement(RecipeUnlockingContext.ALWAYS_UNLOCKED)
                            : this.unlockingRequirement
            );

            recipe.setCraftingTag(this.craftingTag);

            return recipe;
        }
    }
}
