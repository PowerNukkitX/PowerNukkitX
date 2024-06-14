package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.types.RecipeUnlockingRequirement;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.RecipeRegistry;
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
    private final boolean mirror;

    public ShapedRecipe(Item primaryResult, String[] shape, Map<Character, Item> ingredients, List<Item> extraResults) {
        this(null, 1, primaryResult, shape, ingredients, extraResults);
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
    public ShapedRecipe(String recipeId, int priority, Item primaryResult, String[] shape, Map<Character, Item> ingredients, List<Item> extraResults) {
        this(recipeId, priority, primaryResult, shape,
                Maps.transformEntries(ingredients, (Maps.EntryTransformer<Character, Item, ItemDescriptor>) (k, v) -> new DefaultDescriptor(v)),
                extraResults);
    }

    public ShapedRecipe(String recipeId, int priority, Item primaryResult, String[] shape, Map<Character, ItemDescriptor> ingredients, Collection<Item> extraResults) {
        this(recipeId, null, priority, primaryResult, shape, ingredients, extraResults, false);
    }

    public ShapedRecipe(String recipeId, UUID uuid, int priority, Item primaryResult, String[] shape, Map<Character, ItemDescriptor> ingredients, Collection<Item> extraResults, boolean mirror) {
        this(recipeId, uuid, priority, primaryResult, shape, ingredients, extraResults, mirror, null);
    }

    public ShapedRecipe(String recipeId, UUID uuid, int priority, Item primaryResult, String[] shape, Map<Character, ItemDescriptor> ingredients,
                        Collection<Item> extraResults, boolean mirror, RecipeUnlockingRequirement recipeUnlockingRequirement) {
        super(recipeId == null ? RecipeRegistry.computeRecipeId(Lists.asList(primaryResult, extraResults.toArray(Item.EMPTY_ARRAY)), ingredients.values(), SHAPED) : recipeId, priority, recipeUnlockingRequirement);
        this.uuid = uuid;
        this.row = shape.length;
        this.mirror = mirror;
        if (this.row > 3 || this.row == 0) {
            throw new RuntimeException("Shaped recipes may only have 1, 2 or 3 rows, not " + this.row);
        }

        this.col = shape[0].length();
        if (this.col > 3 || this.col == 0) {
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
        return this.results.getFirst();
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

    /**
     * Gets ingredient with a row number and col number.
     *
     * @param x the col
     * @param y the row
     * @return the ingredient
     */
    public ItemDescriptor getIngredient(int x, int y) {
        var res = this.shapedIngredients.get(this.shape[y].charAt(x));
        return res == null ? new DefaultDescriptor(Item.AIR) : res;
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

        boolean checkMirror = false;
        next:
        for (int i = 0; i < input.getRow(); i++) {
            for (int j = 0; j < input.getCol(); j++) {
                ItemDescriptor ingredient = getIngredient(j, i);
                if (!ingredient.match(input.getData()[i][j])) {
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
                    if (!ingredient.match(mirrorInput.getData()[i][j])) {
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
        int startRow = 0, endRow = inputs.length - 1;
        for (int row = 0; row < inputs.length; row++) {
            if (notAllEmptyRow(inputs[row])) {
                startRow = row;
                break;
            }
            // 发现全部都是空气，直接返回空数组
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

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPED;
    }
}
