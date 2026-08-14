package org.powernukkitx.recipe;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.util.collection.CharObjectHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Value;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeNetId;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingContext;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.RecipeUnlockingRequirement;
import org.cloudburstmc.protocol.bedrock.data.payload.crafting.ShapedRecipePayload;
import org.jetbrains.annotations.NotNull;
import org.powernukkitx.item.Item;
import org.powernukkitx.recipe.descriptor.DefaultDescriptor;
import org.powernukkitx.recipe.descriptor.InvalidDescriptor;
import org.powernukkitx.recipe.descriptor.ItemDescriptor;
import org.powernukkitx.registry.RecipeRegistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.powernukkitx.recipe.RecipeType.SHAPED;

public class ShapedRecipe extends CraftingRecipe {
    private final String[] shape;
    private final CharObjectHashMap<ItemDescriptor> shapedIngredients = new CharObjectHashMap<>();
    private final int row;
    private final int col;
    private final boolean mirror;
    private boolean assumeSymmetry;

    public ShapedRecipe(Item primaryResult, int netId, String[] shape, Map<Character, Item> ingredients, List<Item> extraResults) {
        this(null, netId, 1, primaryResult, shape, ingredients, extraResults);
    }

    /**
     * Constructs a ShapedRecipe instance.
     *
     * @param primaryResult Primary result of the recipe
     * @param shape         <br>        Array of 1, 2, or 3 strings representing the rows of the recipe.
     *                      This accepts an array of 1, 2 or 3 strings. Each string should be of the same length and must be at most 3
     *                      characters long. Each character represents a unique type of ingredient. Spaces are interpreted as air.
     * @param ingredients   <br>  Char =&gt; Item map of items to be set into the shape.
     *                      This accepts an array of Items, indexed by character. Every unique character (except space) in the shape
     *                      array MUST have a corresponding item in this list. Space character is automatically treated as air.
     * @param extraResults  <br> List of additional result items to leave in the crafting grid afterwards. Used for things like cake recipe
     *                      empty buckets.
     *                      <p>
     *                      Note: Recipes do not need to be square. Do NOT add padding for empty rows/columns.
     */
    public ShapedRecipe(String recipeId, int netId, int priority, Item primaryResult, String[] shape, Map<Character, Item> ingredients, List<Item> extraResults) {
        this(recipeId, netId, priority, primaryResult, shape,
            Maps.transformEntries(ingredients, (Maps.EntryTransformer<Character, Item, ItemDescriptor>) (k, v) -> new DefaultDescriptor(v)),
            extraResults);
    }

    public ShapedRecipe(String recipeId, int netId, int priority, Item primaryResult, String[] shape, Map<Character, ItemDescriptor> ingredients, Collection<Item> extraResults) {
        this(recipeId, null, netId, priority, primaryResult, shape, ingredients, extraResults, false);
    }

    public ShapedRecipe(String recipeId, UUID uuid, int netId, int priority, Item primaryResult, String[] shape, Map<Character, ItemDescriptor> ingredients, Collection<Item> extraResults, boolean mirror) {
        this(recipeId, new Data(uuid, netId, priority), primaryResult, shape, ingredients, extraResults, mirror, null);
    }

    public ShapedRecipe(String recipeId, Data data, Item primaryResult, String[] shape, Map<Character, ItemDescriptor> ingredients,
                        Collection<Item> extraResults, boolean mirror, RecipeUnlockingRequirement recipeUnlockingRequirement) {
        super(recipeId == null ? RecipeRegistry.computeRecipeId(Lists.asList(primaryResult, extraResults.toArray(Item.EMPTY_ARRAY)), ingredients.values(), SHAPED) : recipeId, data.getNetId(), data.getPriority(), recipeUnlockingRequirement);
        this.uuid = data.getUuid();
        this.mirror = mirror;
        this.assumeSymmetry = mirror;

        this.row = shape.length;
        Preconditions.checkArgument(this.row <= 3 && 1 <= this.row, "Shaped recipes may only have 1, 2 or 3 rows, not " + this.row);

        this.col = shape[0].length();
        Preconditions.checkArgument(this.col <= 3 && 1 <= this.col, "Shaped recipes may only have 1, 2 or 3 columns, not " + this.col);

        for (int i = 0, shapeLength = shape.length; i < shapeLength; i++) {
            String row = getShape(ingredients, shape[i]);
            shape[i] = row.intern();
        }
        this.results.add(primaryResult.clone());//primaryResult
        this.results.addAll(extraResults);//extraResults
        this.shape = shape;

        for (var entry : ingredients.entrySet()) {
            char key = entry.getKey();
            Preconditions.checkArgument(String.join("", this.shape).indexOf(key) >= 0, "Symbol does not appear in the shape: " + key);

            var item = entry.getValue();
            this.shapedIngredients.put(key, item);
            this.ingredients.add(entry.getValue());
        }
    }

    private @NotNull String getShape(Map<Character, ItemDescriptor> ingredients, String shape) {
        Preconditions.checkArgument(shape.length() == this.col, "Shaped recipe rows must all have the same length (expected " + this.col + ", got " + shape.length() + ")");
        for (int x = 0; x < this.col; ++x) {
            char c = shape.charAt(x);
            Preconditions.checkArgument(c == ' ' || ingredients.containsKey(c), "No item specified for symbol '" + c + "'");
        }
        return shape;
    }

    public int getWidth() {
        return this.col;
    }

    public int getHeight() {
        return this.row;
    }

    public Item getResult() {
        return this.results.getFirst();
    }

    public ShapedRecipe setIngredient(String key, Item item) {
        return this.setIngredient(key.charAt(0), item);
    }

    public ShapedRecipe setIngredient(char key, Item item) {
        return this.setIngredient(key, new DefaultDescriptor(item));
    }

    public ShapedRecipe setIngredient(char key, ItemDescriptor item) {
        Preconditions.checkArgument(String.join("", this.shape).indexOf(key) >= 0, "Symbol does not appear in the shape: " + key);

        this.shapedIngredients.put(key, item);

        List<ItemDescriptor> items = new ArrayList<>();
        for (int y = 0, y2 = getHeight(); y < y2; ++y) {
            for (int x = 0, x2 = getWidth(); x < x2; ++x) {
                items.add(getIngredient(x, y));
            }
        }
        ingredients.clear();
        ingredients.addAll(items);
        return this;
    }

    /**
     * Gets ingredient with a row number and col number.
     *
     * @param x the col
     * @param y the row
     * @return the ingredient
     */
    public ItemDescriptor getIngredient(int x, int y) {
        String shape = this.shape[y];
        var res = x < shape.length() ? this.shapedIngredients.get(shape.charAt(x)) : null;
        return res == null ? InvalidDescriptor.INSTANCE : res;
    }

    public String[] getShape() {
        return shape;
    }

    @Override
    public boolean match(Input input) {
        Input mirrorInput = null;
        if (mirror && input.getCol() == 3 && input.getRow() == 3) {
            mirrorInput = new Input(3, 3, mirrorItemArray(input.getData()));
        }
        tryShrinkMatrix(input);
        if (input.getRow() != row || input.getCol() != col) return false;

        boolean checkMirror = false;
        next:
        for (int i = 0; i < input.getRow(); i++) {
            for (int j = 0; j < input.getCol(); j++) {
                ItemDescriptor ingredient = getIngredient(j, i);
                Item inputItem = input.getData()[i][j];

                if (!ingredient.match(inputItem) || inputItem.getCount() < ingredient.getCount()) {
                    if (mirrorInput != null) {
                        checkMirror = true;
                        break next;
                    } else {
                        return false;
                    }
                }
            }
        }
        if (checkMirror) {
            tryShrinkMatrix(mirrorInput);
            for (int i = 0; i < mirrorInput.getRow(); i++) {
                for (int j = 0; j < mirrorInput.getCol(); j++) {
                    ItemDescriptor ingredient = getIngredient(j, i);
                    Item inputItem = mirrorInput.getData()[i][j];

                    if (!ingredient.match(inputItem) || inputItem.getCount() < ingredient.getCount()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static Item[][] mirrorItemArray(Item[][] data) {
        Item[][] clone = new Item[3][3];
        System.arraycopy(data[0], 0, clone[0], 0, 3);
        System.arraycopy(data[1], 0, clone[1], 0, 3);
        System.arraycopy(data[2], 0, clone[2], 0, 3);
        Item tmp;
        for (int i = 0; i < 3; i++) {
            tmp = clone[i][2];
            clone[i][2] = clone[i][0];
            clone[i][0] = tmp;
        }
        return clone;
    }

    /**
     * Try shrink the item matrix.This will remove air item that does not participate in the craft.
     * <p>
     * Example:
     * x represents air item, o represents valid items
     * <p>
     * <pre>
     *     [              [
     *      [x,x,x],
     *      [x,o,o], =>    [o,o],
     *      [x,o,o]        [o,o]
     *     ]              ]
     * </pre>
     *
     * @param input the input
     */
    public static void tryShrinkMatrix(Input input) {
        Item[][] inputs = input.getData();
        if (inputs.length == 0) return;
        int startRow = 0, endRow = inputs.length - 1;
        for (int row = 0; row < inputs.length; row++) {
            if (notAllEmptyRow(inputs[row])) {
                startRow = row;
                break;
            }
            // All slots turned out to be air, so just return an empty array
            if (row == inputs.length - 1) {
                input.setCol(0);
                input.setRow(0);
                input.setData(Input.EMPTY_INPUT_ARRAY);
                return;
            }
        }
        for (int row = inputs.length - 1; row >= 0; row--) {
            if (notAllEmptyRow(inputs[row])) {
                endRow = row;
                break;
            }
        }
        int startColumn = 0, endColumn = inputs[0].length - 1;
        for (int column = 0; column < inputs[0].length; column++) {
            if (notAllEmptyColumn(inputs, column)) {
                startColumn = column;
                break;
            }
        }
        for (int column = inputs[0].length - 1; column >= 0; column--) {
            if (notAllEmptyColumn(inputs, column)) {
                endColumn = column;
                break;
            }
        }

        if (startRow == 0 && endRow == inputs.length - 1 && startColumn == 0 && endColumn == inputs[0].length - 1) {
            input.setData(inputs);
            return;
        }
        int newRow = endRow - startRow + 1;
        int newCol = endColumn - startColumn + 1;
        Item[][] result = new Item[newRow][newCol];
        for (int row = startRow; row <= endRow; row++) {
            if (endColumn + 1 - startColumn >= 0)
                System.arraycopy(inputs[row], startColumn, result[row - startRow], 0, endColumn + 1 - startColumn);
        }
        input.setRow(newRow);
        input.setCol(newCol);
        input.setData(result);
    }

    private static boolean notAllEmptyRow(Item[] inputs) {
        for (var item : inputs) {
            if (!item.isNull()) {
                return true;
            }
        }
        return false;
    }

    private static boolean notAllEmptyColumn(Item[][] inputs, int column) {
        for (var row : inputs) {
            if (!row[column].isNull()) {
                return true;
            }
        }
        return false;
    }

    public boolean isMirror() {
        return mirror;
    }

    public boolean isAssumeSymmetry() {
        return assumeSymmetry;
    }

    public ShapedRecipe setAssumeSymmetry(boolean assumeSymmetry) {
        this.assumeSymmetry = assumeSymmetry;
        return this;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPED;
    }

    public ShapedRecipePayload toNetwork() {
        final List<ItemDescriptor> ingredients = new ObjectArrayList<>();
        for (int y = 0; y < this.getHeight(); y++) {
            for (int x = 0; x < this.getWidth(); x++) {
                ingredients.add(this.getIngredient(x, y));
            }
        }
        final ShapedRecipePayload payload = new ShapedRecipePayload();
        payload.setRecipeId(this.getRecipeId());
        payload.setWidth(this.getWidth());
        payload.setHeight(this.getHeight());
        payload.getIngredients().addAll(ingredients.stream().map(ItemDescriptor::toNetwork).toList());
        payload.getResults().addAll(this.getResults().stream().map(Item::toRecipeNetwork).toList());
        payload.setUuid(this.getUUID());
        payload.setTag(this.getCraftingTag());
        payload.setPriority(this.getPriority());
        payload.setAssumeSymmetry(this.isAssumeSymmetry());
        payload.setUnlockingRequirement(this.getRequirement());
        payload.setNetId(new RecipeNetId(this.getNetId()));
        return payload;
    }

    @Value
    public static class Data {
        UUID uuid;
        int netId;
        int priority;
    }

    /**
     * Constructs a shaped recipe from a builder configuration.
     *
     * @param builder the recipe builder
     */
    protected ShapedRecipe(Builder builder) {
        this(
                validateBuilder(builder).recipeId,
                new Data(
                        builder.uuid,
                        builder.netId,
                        builder.priority
                ),
                builder.result,
                builder.shape.clone(),
                builder.ingredients,
                builder.extraResults,
                builder.mirror,
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
     * Creates a new builder for a shaped recipe.
     *
     * @return a new shaped recipe builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for creating {@link ShapedRecipe} instances using a fluent API.
     * <p>
     * Existing {@link ShapedRecipe} constructors remain available and are not
     * affected by this builder.
     */
    public static final class Builder {
        private String recipeId;
        private UUID uuid;
        private int netId;
        private boolean netIdSet;
        private int priority;
        private Item result;
        private String[] shape;
        private final Map<Character, ItemDescriptor> ingredients = Maps.newHashMap();
        private final List<Item> extraResults = new ArrayList<>();
        private boolean mirror;
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
         * Sets the primary result produced by the recipe.
         *
         * @param result the primary recipe result
         * @return this builder
         */
        public Builder result(Item result) {
            this.result = result;
            return this;
        }

        /**
         * Sets the crafting pattern of the recipe.
         * <p>
         * Each string represents one row and each non-space character must have
         * a corresponding ingredient.
         *
         * @param shape the recipe pattern
         * @return this builder
         */
        public Builder shape(String... shape) {
            this.shape = shape;
            return this;
        }

        /**
         * Associates a character in the recipe shape with an item.
         *
         * @param key the shape character
         * @param item the ingredient
         * @return this builder
         */
        public Builder ingredient(char key, Item item) {
            this.ingredients.put(key, new DefaultDescriptor(item));
            return this;
        }

        /**
         * Associates a character in the recipe shape with an item descriptor.
         *
         * @param key the shape character
         * @param descriptor the ingredient descriptor
         * @return this builder
         */
        public Builder ingredient(char key, ItemDescriptor descriptor) {
            this.ingredients.put(key, descriptor);
            return this;
        }

        /**
         * Adds an additional result produced by the recipe.
         *
         * @param item the additional result
         * @return this builder
         */
        public Builder extraResult(Item item) {
            this.extraResults.add(item);
            return this;
        }

        /**
         * Sets whether the shaped recipe may also match its mirrored pattern.
         *
         * @param mirror whether mirrored matching is allowed
         * @return this builder
         */
        public Builder mirror(boolean mirror) {
            this.mirror = mirror;
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
            } else if (this.unlockingRequirement.getUnlockingContext() != RecipeUnlockingContext.NONE) {
                throw new IllegalStateException(
                        "Unlocking ingredients cannot be combined with unlocking context "
                                + this.unlockingRequirement.getUnlockingContext()
                );
            }

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
                    this.shape != null && this.shape.length > 0,
                    "Shape must be specified"
            );

            return this;
        }

        /**
         * Builds the configured shaped recipe.
         *
         * @return the created shaped recipe
         * @throws IllegalStateException if the network ID, result, or shape have not been specified
         */
        public ShapedRecipe build() {
            this.validate();

            ShapedRecipe recipe = new ShapedRecipe(
                    this.recipeId,
                    new Data(
                            this.uuid,
                            this.netId,
                            this.priority
                    ),
                    this.result,
                    this.shape.clone(),
                    this.ingredients,
                    this.extraResults,
                    this.mirror,
                    this.unlockingRequirement == null
                            ? new RecipeUnlockingRequirement(RecipeUnlockingContext.ALWAYS_UNLOCKED)
                            : this.unlockingRequirement
            );

            recipe.setCraftingTag(this.craftingTag);

            return recipe;
        }
    }
}
