package cn.nukkit.recipe;

import cn.nukkit.item.Item;
import cn.nukkit.recipe.descriptor.DefaultDescriptor;
import cn.nukkit.recipe.descriptor.ItemDescriptor;
import cn.nukkit.registry.Registries;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.util.collection.CharObjectHashMap;
import it.unimi.dsi.fastutil.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
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
        ShapedRecipe.tryShrinkMatrix(input);
        Item[][] data = input.getData();
        for (int i = 0; i < input.getRow(); i++) {
            for (int j = 0; j < input.getCol(); j++) {
                ItemDescriptor ingredient = getIngredient(j, i);
                if (!ingredient.match(data[i][j])) return false;
            }
        }
        return true;
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
        Integer r = null, l = null;
        int row = input.getRow();
        int col = input.getCol();
        Item[][] data = input.getData();
        end:
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (!data[j][i].isNull()) {
                    r = i;
                    l = j;
                    break end;
                }
            }
        }
        if (r == null) {
            return;
        }
        Queue<Pair<Integer, Integer>> bfsQueue = new ArrayDeque<>(row * col);
        HashSet<Pair<Integer, Integer>> result = new HashSet<>();
        bfsQueue.add(Pair.of(l, r));
        while (!bfsQueue.isEmpty()) {
            Pair<Integer, Integer> poll = bfsQueue.poll();
            if (result.contains(poll)) continue;
            result.add(poll);
            Integer left = poll.left();
            Integer right = poll.right();
            int al = left, ar = right + 1;
            pushQueue(row, col, data, bfsQueue, result, al, ar);
            int bl = left, br = right - 1;
            pushQueue(row, col, data, bfsQueue, result, bl, br);
            int cl = left + 1, cr = right;
            pushQueue(row, col, data, bfsQueue, result, cl, cr);
            int dl = left - 1, dr = right;
            pushQueue(row, col, data, bfsQueue, result, dl, dr);
        }
        Integer minCol = null, maxCol = null, minRow = null, maxRow = null;
        for (var pair : result) {
            Integer left = pair.left();
            Integer right = pair.right();
            if (minCol == null) {
                minCol = right;
            } else {
                minCol = Math.min(minCol, right);
            }
            if (maxCol == null) {
                maxCol = right;
            } else {
                maxCol = Math.max(maxCol, right);
            }
            if (minRow == null) {
                minRow = left;
            } else {
                minRow = Math.min(minRow, left);
            }
            if (maxRow == null) {
                maxRow = left;
            } else {
                maxRow = Math.max(maxRow, left);
            }
        }
        int newRow = maxRow - minRow + 1;//+1 because is index
        int newCol = maxCol - minCol + 1;
        if (newRow > 0 && newRow < row && newCol > 0 && newCol < col) {
            Item[][] items = new Item[newRow][newCol];
            for (int i = 0; i < newCol; i++) {
                for (int j = 0; j < newRow; j++) {
                    items[j][i] = data[minRow + j][minCol + i];
                }
            }
            input.setRow(newRow);
            input.setCol(newCol);
            input.setData(items);
        }
    }

    private static void pushQueue(int row, int col, Item[][] data, Queue<Pair<Integer, Integer>> bfsQueue, HashSet<Pair<Integer, Integer>> result, int l, int r) {
        Pair<Integer, Integer> pair = Pair.of(l, r);
        if (!result.contains(pair) && l >= 0 && l < col && r >= 0 && r < row) {
            Item item = data[l][r];
            if (!item.isNull()) {
                bfsQueue.add(pair);
            }
        }
    }

    @Override
    public RecipeType getType() {
        return RecipeType.SHAPED;
    }
}
