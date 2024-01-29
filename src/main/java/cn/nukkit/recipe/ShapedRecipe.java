package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.Registries;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.util.collection.CharObjectHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static cn.nukkit.recipe.RecipeType.SHAPED;

public class ShapedRecipe extends CraftingRecipe {
    private final String[] shape;
    private final CharObjectHashMap<ItemDescriptor> shapedIngredients = new CharObjectHashMap<>();

    private final int row;
    private final int col;

    public ShapedRecipe(Item primaryResult, String[] shape, Map<Character, Item> ingredients, List<Item> extraResults) {
        this(null, 1, primaryResult, shape, ingredients, extraResults);
    }

    /**
     * Constructs a ShapedRecipe instance.
     *
     * @param primaryResult    Primary result of the recipe
     * @param shape<br>        Array of 1, 2, or 3 strings representing the rows of the recipe.
     *                         This accepts an array of 1, 2 or 3 strings. Each string should be of the same length and must be at most 3
     *                         characters long. Each character represents a unique type of ingredient. Spaces are interpreted as air.
     * @param ingredients<br>  Char =&gt; Item map of items to be set into the shape.
     *                         This accepts an array of Items, indexed by character. Every unique character (except space) in the shape
     *                         array MUST have a corresponding item in this list. Space character is automatically treated as air.
     * @param extraResults<br> List of additional result items to leave in the crafting grid afterwards. Used for things like cake recipe
     *                         empty buckets.
     *                         <p>
     *                         Note: Recipes **do not** need to be square. Do NOT add padding for empty rows/columns.
     */
    public ShapedRecipe(String recipeId, int priority, Item primaryResult, String[] shape, Map<Character, Item> ingredients, List<Item> extraResults) {
        this(recipeId, priority, primaryResult, shape,
                Maps.transformEntries(ingredients, (Maps.EntryTransformer<Character, Item, ItemDescriptor>) (k, v) -> new DefaultDescriptor(v)),
                extraResults);
    }

    public ShapedRecipe(String recipeId, int priority, Item primaryResult, String[] shape, Map<Character, ItemDescriptor> ingredients, Collection<Item> extraResults) {
        this(recipeId, null, priority, primaryResult, shape, ingredients, extraResults);
    }

    public ShapedRecipe(String recipeId, UUID uuid, int priority, Item primaryResult, String[] shape, Map<Character, ItemDescriptor> ingredients, Collection<Item> extraResults) {
        super(recipeId == null ? Registries.RECIPE.computeRecipeId(Lists.asList(primaryResult, extraResults.toArray(Item.EMPTY_ARRAY)), ingredients.values(), SHAPED) : recipeId, priority);
        this.uuid = uuid;
        this.row = shape.length;
        if (this.row > 3 || this.row <= 0) {
            throw new RuntimeException("Shaped recipes may only have 1, 2 or 3 rows, not " + this.row);
        }

        this.col = shape[0].length();
        if (this.col > 3 || this.col <= 0) {
            throw new RuntimeException("Shaped recipes may only have 1, 2 or 3 columns, not " + this.col);
        }

        for (int i = 0, shapeLength = shape.length; i < shapeLength; i++) {
            String row = shape[i];
            if (row.length() != this.col) {
                throw new RuntimeException("Shaped recipe rows must all have the same length (expected " + this.col + ", got " + row.length() + ")");
            }

            for (int x = 0; x < this.col; ++x) {
                char c = row.charAt(x);
                if (c != ' ' && !ingredients.containsKey(c)) {
                    throw new RuntimeException("No item specified for symbol '" + c + "'");
                }
            }
            shape[i] = row.intern();
        }
        this.results.add(primaryResult.clone());//primaryResult
        this.results.addAll(extraResults);//extraResults
        this.shape = shape;

        for (var entry : ingredients.entrySet()) {
            char key = entry.getKey();
            var item = entry.getValue();
            if (String.join("", this.shape).indexOf(key) < 0) {
                throw new RuntimeException("Symbol does not appear in the shape: " + key);
            }
            this.shapedIngredients.put(key, item);
            this.ingredients.add(entry.getValue());
        }
    }

    public int getWidth() {
        return this.col;
    }

    public int getHeight() {
        return this.row;
    }

    public Item getResult() {
        return this.results.get(0);
    }

    public ShapedRecipe setIngredient(String key, Item item) {
        return this.setIngredient(key.charAt(0), item);
    }

    public ShapedRecipe setIngredient(char key, Item item) {
        return this.setIngredient(key, new DefaultDescriptor(item));
    }

    public ShapedRecipe setIngredient(char key, ItemDescriptor item) {
        if (String.join("", this.shape).indexOf(key) < 0) {
            throw new RuntimeException("Symbol does not appear in the shape: " + key);
        }
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

    public ItemDescriptor getIngredient(int x, int y) {
        var res = this.shapedIngredients.get(this.shape[y].charAt(x));
        return res == null ? new DefaultDescriptor(Item.AIR) : res;
    }

    public String[] getShape() {
        return shape;
    }

    @Override
    public boolean match(Input input) {
        return false;
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPED;
    }
}
